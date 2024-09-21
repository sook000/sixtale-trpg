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
          <strong>{{ msg.sender }}:</strong> {{ msg.text }}
        </div>
      </div>

      <!-- 채팅 로그 출력 -->
      <div v-else-if="activeTab === 'chat'">
        <div v-for="msg in chatMessages" :key="msg.id">
          <strong>{{ msg.sender }}:</strong> {{ msg.text }}
        </div>
      </div>

      <!-- 귓속말 로그 출력 -->
      <div v-else-if="activeTab === 'whisper'">
        <select v-model="selectedUser" class="whisper-dropdown">
          <option v-for="user in users" :key="user.id" :value="user">{{ user.name }}</option>
        </select>
        <div class="whisper-chat" v-if="selectedUser">
          <div v-for="msg in whisperMessages[selectedUser.id]" :key="msg.id">
            <strong>{{ msg.sender }}:</strong> {{ msg.text }}
          </div>
        </div>
      </div>

      <!-- 챗봇 AI -->
      <div v-else-if="activeTab === 'bot'">
        <div class="container">
          <div v-if="data.error" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ data.error }}
          </div>

          <div class="card-body overflow-auto">
            <RuleChatResult :messages="generatedRuleMessages" />
          </div>
          <div class="card-footer p-3">
            <div class="loading" v-if="data.loads">
                <div class="loader2"></div>
            </div>
          </div>
        </div>
          
      </div>
    </div>
    <div class="chat-input">
      <input
        class="form-control rounded-start"
        type="text"
        v-model="data.userMessage"
        @keydown.enter="run"
        placeholder="무엇이 궁금하신가요?"
        @input="remember('userMessage', data.userMessage)"
      />
      <button @click="run" :style="sendButtonStyle"></button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive,watch } from 'vue';
import { getRoomInfo } from '@/common/api/RoomsAPI';
import { useRoute } from 'vue-router';
import RuleChatResult from '@/views/games/components/ingame/RuleChatResult.vue';
import {
  createCompletion,
  createClient,
  ROLE_ASSISTANT,
  ROLE_SYSTEM,
  ROLE_USER,
} from '@/common/api/openaiApi';

// 탭 설정
const tabs = [
  { key: 'all', label: '전체' },
  { key: 'chat', label: '채팅' },
  { key: 'whisper', label: '귓속말' },
  { key: 'bot', label: '챗봇AI' },
];

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

const route = useRoute();
const roomID = ref(Number(route.params.roomId));

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
  backgroundRepeat: "no-repeat",
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
      roomID: roomInfo.value ? roomInfo.value.id : roomID, // 가져온 방 정보에서 roomID 사용
      memberID: 1, // 사용자 ID, 실제 값으로 설정
      content: message.value, // 메시지 내용
      type: selectedUser.value ? 'whisper' : 'chat', // 선택된 사용자가 있으면 귓속말, 없으면 일반 채팅
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
  }
};

// 탭 선택 함수
const selectTab = (key) => {
  activeTab.value = key;
  if (key !== 'whisper') {
    selectedUser.value = null;
  }
};

/* 룰AI 코드 */

const DEFAULT_SYSTEM_MESSAGE = '안녕하세요! 던전월드 룰에 대해 궁금한 것이 있으면 뭐든지 물어보세요!'
const DEFAULT_USER_MESSAGE = '';

// 로컬 스토리지에서 `generatedRuleMessages`만 불러오는 함수
const loadgeneratedRuleMessagesFromLocalStorage = () => {
  const storedgeneratedRuleMessages = JSON.parse(localStorage.getItem('generatedRuleMessages')) || [];
  return storedgeneratedRuleMessages.map(msg => new Message(msg.role, msg.content));
};

// 데이터 저장 함수 (`generatedRuleMessages`만 저장)
const savegeneratedRuleMessagesToLocalStorage = () => {
  localStorage.setItem('generatedRuleMessages', JSON.stringify(data.generatedRuleMessages));
};

const data = reactive({
  error: '',
  key: 'sec_ksARBi5NqYI2LMk1PXzFF2uupbMFZfmc',
  systemRuleMessage: localStorage.getItem('systemRuleMessage') || DEFAULT_SYSTEM_MESSAGE,
  userMessage: DEFAULT_USER_MESSAGE,
  generatedRuleMessages: loadgeneratedRuleMessagesFromLocalStorage(),
  loads: false,
});

function Message(role, content) {
  this.role = role;
  this.content = content.trim();
}

const initMessages = computed(() => [
  new Message(ROLE_SYSTEM, data.systemRuleMessage),
]);

const generatedRuleMessages = computed(() => initMessages.value.concat(data.generatedRuleMessages));

const remember = (key, value) => localStorage.setItem(key, value);

const rememberKey = () => localStorage.setItem('key', window.btoa(data.key));

// `generatedRuleMessages` 변경 시 로컬 스토리지에 저장
watch(() => data.generatedRuleMessages, savegeneratedRuleMessagesToLocalStorage, { deep: true });


const run = async () => {
  data.loads = true;
  const client = createClient(data.key);
  try {

    const userMessage = data.userMessage.trim();
    
     if (userMessage) {
      // 사용자 메시지 배열에 새 메시지 추가
      data.generatedRuleMessages.push(new Message(ROLE_USER, userMessage));
      data.userMessage = ''; // 입력 필드 비우기
       
        const result = await createCompletion(client)({
          messages: [{ role: ROLE_USER, content: userMessage }],
      });

      const content = result.content; // Adjust based on actual response structure
      console.log(content);
      data.generatedRuleMessages.push(new Message(ROLE_ASSISTANT, content));
    }

  } catch (err) {
    data.error = err.message;
    data.loads = false;
  }
};
</script>

<style scoped>
.chatting-container {
  display: flex;
  flex-direction: column;
  height: 100%; 
  border: 2px solid #000;
  background-size: 100% 100% !important; /* 배경 이미지가 컨테이너에 맞게 늘어나도록 설정 */
  background-repeat: no-repeat !important;
  background-position: center !important;
}
.chat-input button {
  background-color: transparent; /* 버튼의 배경색을 투명하게 설정 */
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
  color: rgb(214, 205, 170);
  background: linear-gradient(to bottom, #444, #222);
}

.tabs button.active {
  background-color: #555;
  color: rgb(214, 205, 170);
}

.tabs button.all {
  background: linear-gradient(to bottom, #1A4E23, #102F12);
}

.tabs button.chat {
  background: linear-gradient(to bottom, #805500, #5A3B00);
}

.tabs button.whisper {
  background: linear-gradient(to bottom, #8C2A0F, #601A0A);
}

.tabs button.bot {
  background: linear-gradient(to bottom, #0B3A73, #062048);
}

.chat-content {
  flex: 1;
  padding: 10px;
  overflow-y: auto;
  border: 1px solid #444;
  color: rgb(214, 205, 170);
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
  color: rgba(255, 255, 255, 0.7);
}

.chat-input input::placeholder {
  color: rgba(255, 255, 255, 0.7);
}

.whisper-dropdown {
  width: 100%;
  margin: 10px 0;
  padding: 5px;
  border: 1px solid #444;
  background-color: #333;
  color: rgba(255, 255, 255, 0.7);
}

.whisper-chat {
  padding: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.7);
}

.chat-content > div {
  color: rgba(255, 255, 255, 0.7);
}

.container{
  max-height: 100px;
}

.custom-input:focus {
  border-color: #eceff1 !important;
  box-shadow: none !important;
  background-color: #eceff1;
}

.custom-input:hover {
  border-color: #eceff1 !important;
  background-color: #eceff1;
}

.custom-input {
  border: #eceff1;
  border-radius: 15px 0px 0px 15px;
  background-color: #eceff1;
}

</style>
