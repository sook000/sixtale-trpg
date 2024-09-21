<template>
  <section>
    <div v-if="data.error" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ data.error }}
    </div>
    <div class="card container shadow rounded-3" style="height: 100%;">
      <div class="card-header d-flex justify-content-between align-items-center"
      style="color: white; background: rgba(255, 255, 255, 0.12);">
        <div class="d-flex align-items-center">
          <span class="ms-2 fs-5 fw-semibold fs-4">Rule Chatbot</span>
        </div>
      </div>
      <div class="card-body p-5 overflow-auto">
        <RuleResult :messages="generatedMessages" />
      </div>
      <div class="card-footer p-3">
        <div class="loading" v-if="data.loads">s
            <div class="loader2"></div>
        </div>
        <div class="input-group">
          <input
            type="text"
            class="form-control rounded-start"
            v-model="data.userMessage"
            placeholder="무엇이 궁금하신가요?"
            @input="remember('userMessage', data.userMessage)"
          />
          <button class="btn btn-outline-secondary rounded-end" type="button" @click="run"
          style="background-color: #4E5E8A;">
            <img src="@/assets/images/chat/send.png" width="30" height="30" />
            <!-- <IconSearch :width="23" :height="23" :color="'#666666'" />  -->
          </button> 
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, reactive,watch } from 'vue';
import RuleResult from './components/RuleResult.vue'; // 수정
import {
  createCompletion,
  createClient,
  ROLE_ASSISTANT,
  ROLE_SYSTEM,
  ROLE_USER,
} from '@/common/api/openaiApi';

const DEFAULT_SYSTEM_MESSAGE = '안녕하세요! 던전월드 룰에 대해 궁금한 것이 있으면 뭐든지 물어보세요!'

const DEFAULT_USER_MESSAGE = '';
const DEFAULT_DELAY_SECONDS = 1;

// 로컬 스토리지에서 `generatedMessages`만 불러오는 함수
const loadGeneratedMessagesFromLocalStorage = () => {
  const storedGeneratedMessages = JSON.parse(localStorage.getItem('generatedMessages')) || [];
  return storedGeneratedMessages.map(msg => new Message(msg.role, msg.content));
};

// 데이터 저장 함수 (`generatedMessages`만 저장)
const saveGeneratedMessagesToLocalStorage = () => {
  localStorage.setItem('generatedMessages', JSON.stringify(data.generatedMessages));
};

const data = reactive({
  error: '',
  key: 'sec_ksARBi5NqYI2LMk1PXzFF2uupbMFZfmc',
  systemMessage: localStorage.getItem('systemMessage') || DEFAULT_SYSTEM_MESSAGE,
  userMessage: DEFAULT_USER_MESSAGE,
  generatedMessages: loadGeneratedMessagesFromLocalStorage(),
  loads: false,
});

function Message(role, content) {
  this.role = role;
  this.content = content.trim();
}

const initMessages = computed(() => [
  new Message(ROLE_SYSTEM, data.systemMessage),
]);

const generatedMessages = computed(() => initMessages.value.concat(data.generatedMessages));

const remember = (key, value) => localStorage.setItem(key, value);

const rememberKey = () => localStorage.setItem('key', window.btoa(data.key));

// `generatedMessages` 변경 시 로컬 스토리지에 저장
watch(() => data.generatedMessages, saveGeneratedMessagesToLocalStorage, { deep: true });


const run = async () => {
  data.loads = true;
  const client = createClient(data.key);
  try {

    const userMessage = data.userMessage.trim();
     if (userMessage) {
    // 사용자 메시지 배열에 새 메시지 추가
    data.generatedMessages.push(new Message(ROLE_USER, userMessage));
    data.userMessage = ''; // 입력 필드 비우기
       
        const result = await createCompletion(client)({
        messages: [{ role: ROLE_USER, content: userMessage }],
      });

      const content = result.content; // Adjust based on actual response structure
      console.log(content);
      data.generatedMessages.push(new Message(ROLE_ASSISTANT, content));
      // await new Promise((resolve) => setTimeout(resolve, data.value.delaySeconds * 1000));
      // data.value.loads = false;
    }
      // }
    // }
    
  } catch (err) {
    data.error = err.message;
    data.loads = false;
  }
};
</script>

<style scoped>



/* 룰북 페이지 스타일 */
.container {
  /* max-width: 1200px; */
  padding: 0 !important;
  margin-top: 150px;
  margin-bottom: 150px;
  background: rgba(255, 255, 255, 0.13);
  mix-blend-mode: exclusion;
  border-radius: 15px;
  min-height: 850px;
  max-height: 1000px;

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
