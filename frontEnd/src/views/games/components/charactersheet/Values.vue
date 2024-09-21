<template>
  <div v-if="showWarning" class="warning-text">가치관을 선택하지 않았습니다!</div>
  <div class="values-container">
    <div class="values-cards">
      <div 
        v-for="belief in currentOptions" 
        :key="belief.beliefID"
        :class="['action-card', { selected: localFormData.beliefId === belief.beliefID }]"
        @click="selectBelief(belief.beliefID)"
        :style="getCardStyle(belief.beliefID)"
      >
        <div class="card-content">
          <span class="card-title">{{ belief.beliefName }}</span>
          <span class="card-description">{{ belief.description }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, toRefs, watch, ref, onMounted } from 'vue';

const props = defineProps({
  formData: Object,
  currentOptions: Array,
  jobId: Number,  
  ruleId: Number,
});

const { formData } = toRefs(props);

const emit = defineEmits(['update:belief-id']);

const localFormData = reactive({ ...formData.value });

// Watcher로 localFormData가 변경될 때 부모 컴포넌트로 데이터 전송
watch(localFormData, (newValue) => {
  emit('update:belief-id', newValue.beliefId);
  console.log('부모로 전송한 가치관 ID:', newValue.beliefId); // 선택한 가치관 ID를 콘솔에 출력
});

// 경고 메시지 표시 여부
const showWarning = ref(false);

function selectBelief(beliefID) {
  localFormData.beliefId = beliefID; // beliefId로 데이터 저장
  emit('update:belief-id', beliefID); // 부모에게 변경된 데이터 전송
  console.log('선택한 가치관 ID:', beliefID); // 선택한 가치관 ID를 콘솔에 출력
  showWarning.value = false; // 선택 시 경고 메시지 숨기기
}

function checkForEmptyBelief() {
  // 선택된 가치관이 없을 경우 경고 메시지를 표시합니다.
  showWarning.value = !localFormData.beliefId;
}

// 컴포넌트가 마운트될 때 경고 메시지 표시
onMounted(() => {
  checkForEmptyBelief();
});

const actionListImage = require('@/assets/images/character_sheet/ActionList.png');
const actionListSelectImage = require('@/assets/images/character_sheet/ActionListSelect.png');

// 카드 스타일을 가져오는 함수
function getCardStyle(beliefID) {
  return {
    backgroundImage: `url(${localFormData.beliefId === beliefID ? actionListSelectImage : actionListImage})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    padding: '20px',
    color: '#fff',
    borderRadius: '8px',
    overflow: 'hidden',
    cursor: 'pointer',
    border: localFormData.beliefId === beliefID ? '2px solid white' : 'none',
  };
}
</script>

<style scoped>
.values-container {
  display: flex;
  justify-content: center;
  padding-top: 0px;
  height: 80%; 
  overflow-y: auto; 
}

.values-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center; /* 중앙 정렬 */
  width: 100%;
}

.action-card {
  width: 200px;
  height: auto;
  transition: transform 0.3s, box-shadow 0.3s;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.action-card:hover {
  transform: scale(1.05);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
}

.action-card.selected {
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.card-title {
  font-size: 1.5rem;
  margin-bottom: 10px;
}

.card-description {
  font-size: 1.2rem;
  text-align: left; 
  width: 100%;
  word-wrap: break-word;
  white-space: pre-wrap; /* Preserve formatting including line breaks */
}

.warning-text {
  font-size: 1.5rem;
  color: red;
  text-align: center;
  margin: 1rem;
  font-weight: bold;
}
</style>
