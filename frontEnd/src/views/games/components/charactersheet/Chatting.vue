<template>
  <div class="chatting-container" :style="backgroundStyle">
    <div class="tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        @click="selectTab(tab.key)"
        :class="{ active: activeTab === tab.key, [tab.key]: true }"
      >
        {{ tab.label }}
      </button>
    </div>
    <div class="chat-content">
      <!-- 전체 채팅 로그 출력 -->
      <div v-if="activeTab === 'all'">
        <div v-for="msg in allMessages" :key="msg.id">
          <strong>{{ msg.sender }}:</strong> {{ msg.content }}
        </div>
      </div>

      <!-- 채팅 로그 출력 -->
      <div v-else-if="activeTab === 'chat'">
        <div v-for="msg in chatMessages" :key="msg.id">
          <strong>{{ msg.sender }}:</strong> {{ msg.content }}
        </div>
      </div>

      <!-- 귓속말 로그 출력 -->
      <div v-else-if="activeTab === 'whisper'">
        <select v-model="selectedUser" class="whisper-dropdown">
          <option v-for="user in users" :key="user.id" :value="user">{{ user.name }}</option>
        </select>
        <div class="whisper-chat" v-if="selectedUser">
          <div v-for="msg in whisperMessages[selectedUser.id]" :key="msg.id">
            <strong>{{ msg.sender }}:</strong> {{ msg.content }}
          </div>
        </div>
      </div>

      <!-- 챗봇 AI -->
      <div v-else-if="activeTab === 'bot'">챗봇 AI</div>
    </div>
    <div class="chat-input">
      <input
        type="text"
        v-model="message"
        @keydown.enter="sendMessage"
        placeholder="메시지를 입력하세요"
      />
      <button @click="sendMessage" :style="sendButtonStyle"></button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getRoomInfo } from '@/common/api/RoomsAPI';
import { useRoute } from 'vue-router';

const tabs = [
  { key: 'all', label: '전체' },
  { key: 'chat', label: '채팅' },
  { key: 'whisper', label: '귓속말' },
  { key: 'bot', label: '챗봇AI' },
];

const route = useRoute();
const roomID = ref(Number(route.params.roomId));
const activeTab = ref('all');
const message = ref('');
const allMessages = ref([]); // 모든 메시지를 저장하는 배열
const chatMessages = ref([]); // 채팅 메시지를 저장하는 배열
const whisperMessages = ref({}); // 귓속말 메시지를 저장하는 객체
const users = ref([
  { id: 1, name: 'Player1' },
  { id: 2, name: 'Player2' },
  { id: 3, name: 'Player3' },
  { id: 4, name: 'Player4' },
  { id: 5, name: 'Player5' },
  { id: 6, name: 'Player6' },
  { id: 7, name: 'Player7' },
  { id: 8, name: 'Player8' },
  { id: 'GM', name: 'Game Master' },
]); // 예시 사용자 데이터
const selectedUser = ref(null);
const roomInfo = ref(null); // 방 정보를 저장할 변수


// 컴포넌트가 마운트되면 방 정보 가져오기
onMounted(async () => {
  try {
    // 방 정보 가져오기
    roomInfo.value = await getRoomInfo(roomID);
    console.log('Room Info:', roomInfo.value);
  } catch (error) {
    console.error('Error fetching room info:', error);
  }
});

// 이미지 경로 설정
const backgroundImage = require('@/assets/images/ingame/Border8.png');
const sendButtonImage = require('@/assets/images/ingame/Send_Button.png');

// 배경 스타일 설정
const backgroundStyle = {
  backgroundImage: `url(${backgroundImage})`,
  backgroundSize: '100%',
  backgroundPosition: 'center',
  padding: '10px',
  boxSizing: 'border-box',
  display: 'flex',
  flexDirection: 'column',
  height: '100%',
};

// 전송 버튼 스타일 설정
const sendButtonStyle = {
  width: '40px',
  height: '40px',
  backgroundImage: `url(${sendButtonImage})`,
  backgroundSize: 'contain',
  backgroundPosition: 'center',
  backgroundColor: 'transparent', // 배경색을 투명하게 설정
  border: 'none',
  cursor: 'pointer',
  backgroundRepeat: "no-repeat",
};

// 메시지 전송 함수
const sendMessage = () => {
  if (message.value.trim() !== '') {
    const messageData = {
      id: Date.now(), // 메시지 ID 생성
      roomID: roomInfo.value ? roomInfo.value.id : roomID, // 가져온 방 정보에서 roomID 사용
      memberID: 1, // 사용자 ID, 임시 값으로 설정
      sender: 'Me', // 사용자 이름, 임시 값으로 설정
      content: message.value, // 메시지 내용
      type: selectedUser.value ? 'whisper' : 'chat', // 메시지 유형
      targetId: selectedUser.value ? selectedUser.value.id : null,
      roomType: roomInfo.value ? roomInfo.value.roomType : null, // 방 정보에서 roomType 사용
    };

    console.log('Sending message:', messageData); // 전송 메시지 확인

    // 전체 메시지에 추가
    allMessages.value.push(messageData);

    // 메시지 유형에 따라 적절한 배열에 추가
    if (messageData.type === 'chat') {
      chatMessages.value.push(messageData);
    } else if (messageData.type === 'whisper' && messageData.targetId) {
      const log = whisperMessages.value[messageData.targetId] || [];
      log.push(messageData);
      whisperMessages.value = { ...whisperMessages.value, [messageData.targetId]: log };
    }

    message.value = ''; // 입력 필드 초기화

    // 서버로 메시지 전송
    // WebSocketService.sendMessage(messageData); // 실제 구현 시 주석 해제
  }
};

// 탭 선택 함수
const selectTab = (key) => {
  activeTab.value = key;
  if (key !== 'whisper') {
    selectedUser.value = null;
  }
};
</script>

<style scoped>
.chatting-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 2px solid #000;
}

.tabs {
  display: flex;
  margin-bottom: 0;
}

.tabs button {
  flex: 1;
  padding: 10px;
  cursor: pointer;
  border: 1px solid #333;
  color: white;
  background: linear-gradient(to bottom, #444, #222);
}

.tabs button.active {
  background-color: #555;
  color: #fff;
}

.tabs button.all {
  background: linear-gradient(to bottom, #1A4E23, #102F12);
}

.tabs button.chat {
  background: linear-gradient(to bottom, #0B3A73, #062048);
}

.tabs button.whisper {
  background: linear-gradient(to bottom, #8C2A0F, #601A0A);
}

.tabs button.bot {
  background: linear-gradient(to bottom, #805500, #5A3B00);
}

.chat-content {
  flex: 1;
  padding: 10px;
  overflow-y: auto;
  border: 1px solid #444;
  color: white;
}

.chat-input {
  display: flex;
  align-items: center;
  padding: 5px;
  background-color: rgba(75, 58, 41, 0.9);
}

.chat-input input {
  flex: 1;
  padding: 8px;
  border: 1px solid #444;
  background-color: #222;
  color: white;
}

.chat-input input::placeholder {
  color: white;
}

.whisper-dropdown {
  width: 100%;
  margin: 10px 0;
  padding: 5px;
  border: 1px solid #444;
  background-color: #333;
  color: white;
}

.whisper-chat {
  padding: 10px;
  border-top: 1px solid #ddd;
}

.chat-content > div {
  color: #fff;
}
</style>
