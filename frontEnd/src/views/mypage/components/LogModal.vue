<template>
  <div class="log-modal" @click.self="closeModal">
    <div class="log-modal-content">
      <h2 class="log-title">플레이 로그</h2>
      <div class="log-details">
        <h3>{{ formatRoomId(room.id) }} {{ room.title }}</h3>
        <p>S# {{ room.scenarioTitle }}</p>
      </div>
      <div class="log-tabs">
        <button
          v-for="(tab, index) in tabs"
          :key="index"
          :class="['log-tab', { active: activeTab === tab }]"
          @click="activeTab = tab"
        >
          {{ tab }}
        </button>
      </div>
      <!-- <div class="log-content"> -->
      <div
        class="log-content"
        :class="{ 'tab-content-active': activeTab === '대기' || activeTab === '플레이' }"
      >
        <!-- 대기 탭 내용 -->
        <div v-if="activeTab === '대기'">
          <div v-for="(entry, index) in logEntriesWait" :key="index" class="log-entry tab-content">
            <span :class="entry.type">{{ entry.user }}:</span> {{ entry.message }}
          </div>
        </div>
        <!-- 플레이 탭 내용 -->
        <div v-if="activeTab === '플레이'">
          <div v-for="(entry, index) in logEntriesPlay" :key="index" class="log-entry tab-content">
            <span :class="entry.type">{{ entry.user }}:</span> {{ entry.message }}
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="close-button" @click="closeModal">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { defineProps, defineEmits } from "vue";

const props = defineProps({
  room: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["close"]);

const closeModal = () => {
  emit("close");
};

// const isOpen = ref(true); // 모달이 열려 있는지 여부
const activeTab = ref("대기"); // 현재 활성화된 탭
const tabs = ref(["대기", "플레이"]); // 탭 목록

const logEntriesWait = ref([
  { type: "GM", user: "GM", message: "이제 던전월드로 떠나볼까요?" },
  {
    type: "플레이어",
    user: "플레이어1",
    message: "여행은 새로운 장소로 떠나는 것 같아요.",
  },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
  { type: "플레이어", user: "플레이어2", message: "동의해요!" },
]);
// 플레이 탭 로그 데이터
const logEntriesPlay = ref([
  { type: "GM", user: "GM", message: "던전에 진입했습니다." },
  { type: "플레이어", user: "플레이어1", message: "탐색을 시작합니다." },
  { type: "플레이어", user: "플레이어2", message: "제가 앞장설게요." },
]);

// 방 ID를 포맷팅하는 함수
const formatRoomId = (id) => {
  if (id < 10) {
    return `00${id}`;
  } else if (id < 100) {
    return `0${id}`;
  } else {
    return `${id}`;
  }
};
</script>

<style scoped>
.log-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.log-modal-content {
  background-color: #3c3d41;
  border-radius: 18px;
  padding: 30px;
  max-width: 900px;
  width: 100%;
  box-shadow: inset 1px 1px 4px rgba(158, 158, 158, 0.6);
}

.log-title {
  color: #ffffff;
  font-size: 1.7rem;
  margin-bottom: 20px;
  text-align: left;
}

.log-details h3 {
  color: #bfbfc0;
  margin: 0 0 0 8px;
  font-size: 1.1rem;
  text-align: left;
}

.log-details p {
  color: #bfbfc0;
  margin: 5px 0 20px 16px;
  text-align: left;
  font-size: 1.1rem;
}

.log-tabs {
  display: flex;
  justify-content: flex-start;
  width: 300px;
  margin: 0 8px;
}

.log-tab {
  background-color: #2e2e2e;
  color: #f0f0f0;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  border-radius: 5px;
  flex: 1;
  text-align: center;
}

.log-tab.active {
  background-color: #4d4e51;
}

.log-content {
  text-align: left;
  max-height: 500px;
  overflow-y: auto;
  background-color: #4d4e51;
  padding: 20px;
  border-radius: 10px;
}
.log-content::-webkit-scrollbar {
  width: 15px;
}

.log-content::-webkit-scrollbar-thumb {
  background-color: #d9d9d9cf;
  border-radius: 4px;
}

.log-content::-webkit-scrollbar-thumb:hover {
  background-color: #888;
}

.tab-content {
  background-color: #d9d9d9; /* 흰색 배경 */
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ddd;
  color: #333333; /* 검정색 텍스트 */
}
.log-entry {
  margin-bottom: 10px;
  /* color: #f0f0f0; */
}

.log-entry span.GM {
  color: #007bff;
}

.log-entry span.플레이어 {
  color: #ff7f50;
}
.modal-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
.close-button {
  background-color: #343434;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 20px;
  display: block;
  width: 100%;
  text-align: center;
  width: 200px;
}

.close-button:hover {
  background-color: #1c1b1b;
}
</style>
