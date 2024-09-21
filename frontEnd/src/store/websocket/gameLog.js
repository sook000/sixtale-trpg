import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";

class GameLogWebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.roomID = null;
    this.subscriptionUrls = [];
    this.callbacks = {}; // 콜백 함수들을 저장하는 객체
    this.isConnecting = false; // 현재 연결 시도 중인지 확인
  }

  // WebSocket 및 STOMP 연결 설정
  connect(roomID) {
    if (this.connected || this.isConnecting) {
      return; // 이미 연결 중이거나 연결된 상태라면 연결 시도를 건너뜁니다.
    }

    if (!roomID) {
      console.error("Room ID is required to connect to WebSocket");
      return;
    }

    this.roomID = roomID;
    this.isConnecting = true;

    const socket = new SockJS("https://i11d108.p.ssafy.io/api/v1/ws"); // SockJS로 WebSocket 연결 생성
    this.stompClient = Stomp.over(socket); // STOMP 클라이언트 생성

    this.stompClient.connect(
      {},
      () => {
        this.connected = true;
        this.isConnecting = false;
        console.log("Connected to Game Log WebSocket");

        // 모든 메시지 타입에 대해 등록된 콜백 함수 실행
        for (const messageType in this.callbacks) {
          if (this.callbacks.hasOwnProperty(messageType)) {
            this.subscribeToUrl(`/sub/game/messages/${this.roomID}`);
          }
        }
      },
      (error) => {
        this.isConnecting = false;
        console.error("Game Log WebSocket connection error:", error); // 연결 오류 처리
      }
    );
  }

  // 기존 GameLogWebSocket 연결 해제
  disconnect() {
    if (this.connected && this.stompClient) {
      this.stompClient.disconnect(() => {
        console.log("Disconnected from WebSocket");
        this.connected = false;
        this.subscriptionUrls = [];
      });
    }
  }

  // 메시지 수신 시 호출할 콜백 등록
  onMessageReceived(messageType, callback) {
    console.log("messageType", messageType);
    console.log("test11111111111");
    if (!this.callbacks[messageType]) {
      console.log("test12222222222222");
      this.callbacks[messageType] = [];
    }
    console.log("test3333333333333");

    this.callbacks[messageType].push(callback);
    console.log("tes444444444444444");

    if (this.connected) {
      console.log("test55555555555555555");
      this.subscribeToUrl(`/sub/game/messages/${this.roomID}`);
    } else if (!this.isConnecting) {
      console.log("test666666666666666");
      this.connect(this.roomID);
    }
  }

  // 메시지를 서버로 전송
  sendMessage(message) {
    if (this.connected) {
      console.log("Sending in-game message:", message);
      this.stompClient.send("/pub/game/message", {}, JSON.stringify(message)); // 메시지 전송
    } else {
      console.error("Game Log WebSocket is not connected");
    }
  }

  // 특정 URL 구독
  subscribeToUrl(url) {
    if (this.subscriptionUrls.includes(url)) {
      return; // 이미 구독된 URL이면 중복 구독 방지
    }
    this.subscriptionUrls.push(url);

    this.stompClient.subscribe(url, (message) => {
      console.log(`Message received from ${url}:`, message.body);
      const parsedMessage = JSON.parse(message.body);

      console.log("parsedMessage", parsedMessage);

      console.log("parsedMessage.gameType", parsedMessage.gameType);
      console.log(
        "this.callbacks[parsedMessage.gameType]",
        this.callbacks[parsedMessage.gameType]
      );

      // 메시지 타입별로 콜백 호출
      if (parsedMessage.gameType && this.callbacks[parsedMessage.gameType]) {
        console.log("호출");
        this.callbacks[parsedMessage.gameType].forEach((callback) => {
          callback(parsedMessage);
        });
        console.log("완료");
      }
    });
  }
}

export default new GameLogWebSocketService();
