import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";

class GameChatSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.roomID = null;
    this.memberID = null;
    this.subscriptionUrls = [];
    this.callbacks = {}; // 콜백 함수들을 저장하는 객체
    this.isConnecting = false; // 현재 연결 시도 중인지 확인
  }

  // WebSocket 및 STOMP 연결 설정
  connect(roomID, memberID) {
    if (this.connected || this.isConnecting) {
      return; // 이미 연결 중이거나 연결된 상태라면 연결 시도를 건너뜁니다.
    }

    if (!roomID) {
      console.error("Room ID is required to connect to WebSocket");
      return;
    }

    this.roomID = roomID;
    this.memberID = memberID;
    this.isConnecting = true;

    const socket = new SockJS("https://i11d108.p.ssafy.io/api/v1/ws"); // SockJS로 WebSocket 연결 생성
    this.stompClient = Stomp.over(socket); // STOMP 클라이언트 생성

    this.stompClient.connect(
      {},
      () => {
        this.connected = true;
        this.isConnecting = false;
        console.log("Connected to WebSocket");

        // 모든 메시지 타입에 대해 등록된 콜백 함수 실행
        for (const messageType in this.callbacks) {
          if (this.callbacks.hasOwnProperty(messageType)) {
            this.subscribeToUrl(`/sub/play/chat/room/${this.roomID}`);
            this.subscribeToUrl(`/sub/play/chat/whisper/${this.roomID}/${this.memberID}`);
          }
        }
      },
      (error) => {
        this.isConnecting = false;
        console.error("WebSocket connection error:", error); // 연결 오류 처리
      }
    );
  }

  // 메시지 수신 시 호출할 콜백 등록
  onMessageReceived(messageType, callback) {
    if (!this.callbacks[messageType]) {
      this.callbacks[messageType] = [];
    }
    this.callbacks[messageType].push(callback);

    if (this.connected) {
      this.subscribeToUrl(`/sub/play/chat/room/${this.roomID}`);
      this.subscribeToUrl(`/sub/play/chat/whisper/${this.roomID}/${this.memberID}`);
    } else if (!this.isConnecting) {
      this.connect(this.roomID, this.memberID);
    }
  }

  // 메시지를 서버로 전송
  sendMessage(message) {
    if (this.connected) {
      console.log("Sending message:", message);
      this.stompClient.send("/pub/play/chat/message", {}, JSON.stringify(message)); // 메시지 전송
    } else {
      console.error("WebSocket is not connected");
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

      // 메시지 타입별로 콜백 호출
      if (parsedMessage.type && this.callbacks[parsedMessage.type]) {
        this.callbacks[parsedMessage.type].forEach((callback) => {
          callback(parsedMessage);
        });
      }
    });
  }
}

export default new GameChatSocketService();
