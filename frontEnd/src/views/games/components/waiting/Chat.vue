<template>
  <div :style="chatSectionStyle" class="chat-section">
    <div class="chat-tabs">
      <div
        v-for="tab in ['all', 'chat', 'whisper']"
        :key="tab"
        @click="selectTab(tab)"
        :class="{ tab: true }"
        :style="getTabStyle(tab)"
      >
        <span>{{ getTabText(tab) }}</span>
      </div>
    </div>
    <div :style="chatWindowStyle" class="chat-window log-content">
      <!-- 필터링된 메시지 출력 -->
      <div v-for="message in filteredMessages" :key="message.id" class="chat-message">
        <span class="sender">{{ message.nickName }}:</span>
        <span class="text">{{ message.content }}</span>
      </div>
    </div>
    <div :style="inputContainerStyle" class="input-container">
      <input
        v-model="newMessage"
        @keydown.enter="sendMessage"
        placeholder="메시지를 입력하세요..."
        :style="chatInputStyle"
        class="chat-input"
      />
      <button @click="sendMessage" :style="sendButtonStyle" class="send-button"></button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineProps, nextTick, watch } from "vue";
import WebSocketService from "@/store/websocket/waiting"; // WebSocket 서비스 가져오기
import { getRoomInfo } from "@/common/api/RoomsAPI";
import { getMemberInfo } from "@/common/api/mypageAPI";
import { useRoute, useRouter } from "vue-router";

// 이미지 동적 로드
const background1 = require("@/assets/images/room/chat/chat_background1.png");
const background2 = require("@/assets/images/room/chat/chat_background2.png");
const tabAllImage = require("@/assets/images/room/chat/chat_all.png");
const tabChatImage = require("@/assets/images/room/chat/chat_common.png");
const tabWhisperImage = require("@/assets/images/room/chat/chat_whisper.png");
const tabSelectImage = require("@/assets/images/room/chat/chat_select.png");
const inputBackground = require("@/assets/images/room/chat/chat_input.png");
const sendButtonImage = require("@/assets/images/room/chat/Send_Button.png");

const selectedTab = ref("ALL");
const newMessage = ref("");
const messages = ref([]); // 모든 메시지를 저장하는 배열
const roomInfo = ref(null); // 방 정보를 저장할 변수
const user = ref({});
const route = useRoute();
// 로그로직 추가해야한다 ! //
const router = useRouter();

// 현재 방 ID를 가져오기 위한 변수 (예: 실제로 사용하고자 하는 방 ID)
const initialRoomId = route.params.roomId; // 초기 방 ID 설정, 실제로 사용할 방 ID로 설정

// const userId = ref(0);

// 컴포넌트가 마운트되면 WebSocket 연결 설정 및 방 정보 가져오기
onMounted(async () => {
  //회원 기본 정보
  getMemberInfo()
    .then((response) => {
      console.log(response.data.data);
      user.value = response.data.data;
      console.log("-----------");
      console.log("user", user.value);
      // userId.value = response.data.data.id;
    })
    .catch((error) => {
      console.error("Failed to fetch member info:", error);
    });

  // WebSocketService.connect(route.params.roomId, userId.value);

  // "TALK" 메시지 타입에 대한 콜백 등록
  WebSocketService.onMessageReceived("CHAT", (message) => {
    console.log("Talk message received:", message);
    messages.value.push(message); // 메시지 목록에 추가
    console.log("메시지 목록에 추가");
  });

  // "ENTER" 메시지 타입에 대한 콜백 등록
  WebSocketService.onMessageReceived("ENTER", (message) => {
    console.log("Enter message received:", message);
    messages.value.push(message); // 메시지 목록에 추가
    console.log("메시지 목록에 추가");
    console.log(messages.value);
    console.log("====================");
  });

  // WebSocketService.connect(route.params.roomId, user.value.id);

  // // 서버로부터 메시지를 수신할 때마다 콜백 실행
  // WebSocketService.onMessageReceived((message) => {
  //   console.log("경민"+" "+message)
  //   switch(message.type){
  //     case "ENTER":
  //       console.log("222")
  //     case "TALK":
  //       console.log("222111")
  //   }
  //   messages.value.push(message); // 메시지 목록에 추가
  // });
  scrollToBottom();
});

const selectTab = (tab) => {
  selectedTab.value = tab.toUpperCase(); // 탭 선택
};

const getTabStyle = (tab) => {
  const isSelected = selectedTab.value === tab.toUpperCase();
  return {
    ...tabStyle,
    backgroundImage: `url(${isSelected ? tabSelectImage : getTabImage(tab)})`,
  };
};

const getTabImage = (tab) => {
  switch (tab) {
    case "all":
      return tabAllImage;
    case "chat":
      return tabChatImage;
    case "whisper":
      return tabWhisperImage;
    default:
      return tabAllImage;
  }
};

const getTabText = (tab) => {
  switch (tab) {
    case "all":
      return "전체";
    case "chat":
      return "채팅";
    case "whisper":
      return "귓속말";
    default:
      return "";
  }
};

const sendMessage = () => {
  if (newMessage.value.trim() === "") return;

  const messageData = {
    roomID: parseInt(route.params.roomId, 10), // 채팅방 ID
    memberID: user.value.id,
    // recipient: selectedTab.value === "whisper" ? "recipientUsername" : "", // 귓속말 대상자, 귓속말일 때만 설정
    content: newMessage.value, // 메시지 내용
    type: selectedTab.value === "WHISPER" ? "WHISPER" : "CHAT", // 메시지 유형
    //roomType: roomInfo.value ? roomInfo.value.type : null, // 방 정보에서 roomType 사용
  };

  // 메시지를 화면에 즉시 추가
  // messages.value.push({
  //   id: Date.now(),
  //   sender: "Me", // 로컬 사용자의 이름
  //   text: newMessage.value,
  //   type: messageData.type,
  // });

  //WebSocketService.sendMessage(messageData, user.value.id); // 서버로 메시지 전송
  WebSocketService.sendMessage(messageData);

  newMessage.value = ""; // 입력 필드 초기화
  console.log("메시지가 추가되었습니다:", newMessage.value);
};

const filteredMessages = computed(() => {
  if (selectedTab.value === "ALL") {
    return messages.value; // 전체 메시지 반환
  }
  return messages.value.filter((message) => message.type === selectedTab.value); // 선택된 탭에 맞는 메시지 필터링
});

// 스크롤을 맨 아래로 이동시키는 함수
const scrollToBottom = () => {
  nextTick(() => {
    const logContent = document.querySelector(".log-content");
    if (logContent) {
      logContent.scrollTop = logContent.scrollHeight;
    }
  });
};

// 메시지 배열의 깊은 변경을 감지
watch(
  filteredMessages,
  (newMessages) => {
    scrollToBottom();
  },
  { deep: true }
);

const chatSectionStyle = {
  height: "45%",
  width: "97%",
  display: "flex",
  flexDirection: "column",
  justifyContent: "space-between",
  backgroundImage: `url(${background2})`,
  backgroundRepeat: "no-repeat",
  backgroundPosition: "center center",
  backgroundSize: "cover",
  borderRadius: "10px",
  padding: "10px",
  margin: "10px",
  boxSizing: "border-box",
  border: "1px solid #4A3A2E",
};

const chatWindowStyle = {
  flexGrow: 1,
  overflowY: "auto",
  // padding: "10px",
  backgroundImage: `url(${background1})`,
  backgroundRepeat: "no-repeat",
  backgroundPosition: "center center",
  backgroundSize: "cover",
  // marginBottom: "10px",
  borderRadius: "5px",
  marginTop: "0",
};

const tabStyle = {
  cursor: "pointer",
  width: "120px",
  height: "30px",
  backgroundSize: "contain",
  backgroundRepeat: "no-repeat",
  backgroundPosition: "center",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  fontSize: "14px",
  padding: "0",
  margin: "0",
  color: "white",
  fontWeight: "bold",
  transition: "background-image 0.3s",
};

const tabAllStyle = {
  ...tabStyle,
  backgroundImage: `url(${tabAllImage})`,
};

const tabChatStyle = {
  ...tabStyle,
  backgroundImage: `url(${tabChatImage})`,
};

const tabWhisperStyle = {
  ...tabStyle,
  backgroundImage: `url(${tabWhisperImage})`,
};

const activeTabStyle = {
  backgroundColor: "rgba(255, 255, 255, 0.3)",
  border: "2px solid #fff",
};

const inputContainerStyle = {
  display: "flex",
  alignItems: "center",
  backgroundImage: `url(${inputBackground})`,
  backgroundRepeat: "no-repeat",
  backgroundSize: "cover",
  borderRadius: "5px",
  padding: "5px",
  border: "1px solid #4A3A2E",
};

const chatInputStyle = {
  flexGrow: 1,
  border: "none",
  background: "transparent",
  color: "white",
  padding: "10px",
  outline: "none",
  borderRadius: "5px",
};

const sendButtonStyle = {
  width: "40px",
  height: "40px",
  backgroundImage: `url(${sendButtonImage})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  backgroundColor: "transparent", // 배경색 투명으로 설정
  border: "none",
  padding: "0", // 패딩 제거
  margin: "0", // 마진 제거
  outline: "none",
  cursor: "pointer",
  border: "1px solid #4A3A2E",
};
</script>

<style scoped>
.chat-tabs {
  display: flex;
  gap: 0;
  padding: 0;
  margin: 0;
  justify-content: flex-start;
}

.tab {
  flex-grow: 0;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.chat-window {
  flex-grow: 1;
  overflow-y: auto;
  /* padding: 10px; */
  background-color: rgba(41, 23, 7, 0.8); /* 가독성을 위한 약간의 오버레이 추가 */
  margin-bottom: 10px;
  border-radius: 5px;
  margin-top: 0; /* 상단 마진 제거 */
  padding-top: 0; /* 상단 패딩 제거 */
}

.chat-message {
  /* margin-bottom: 5px; */
  color: white;
}

.sender {
  font-weight: bold;
}

input::placeholder {
  color: white;
}

@media (max-width: 768px) {
  .chat-section {
    height: auto;
    /* margin: 5px; */
  }

  .chat-tabs {
    flex-direction: column;
  }

  .chat-tabs div {
    /* margin: 5px 0; */
  }
}

/* 스크롤바 스타일링 */
.log-content::-webkit-scrollbar {
  width: 8px;
}

.log-content::-webkit-scrollbar-track {
}

.log-content::-webkit-scrollbar-thumb {
  background-color: #af8552;
  border-radius: 5px;
  border: 1px solid #274213;
}
</style>
