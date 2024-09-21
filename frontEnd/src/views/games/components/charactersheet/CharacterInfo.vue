<template>
  <div class="character-info-container">
    <div class="left-section">
      <div class="input-group">
        <div class="title-container">
          <span class="title-text">이름</span>
        </div>
        <div class="name-input-container">
          <input 
            type="text" 
            v-model="localFormData.name" 
            id="name" 
            class="name-input" 
            maxlength="8" 
            placeholder="최대 8자" 
            @input="emitNameChange" 
          />
          <img src="@/assets/images/character_sheet/name_box.png" alt="이름 입력 배경" class="name-input-image">
        </div>
      </div>
    </div>
    <div class="right-section">
      <div class="race-title-container">
        <span class="race-title">선택 가능한 종족</span>
      </div>
      <div class="race-cards">
        <div 
          v-for="race in currentOptions" 
          :key="race.raceID" 
          :class="['action-card', { selected: localFormData.raceId === race.raceID }]"
          @click="selectRace(race.raceID)">
          <div class="race-card-header">
            <span class="race-card-title">{{ race.raceName }}</span>
          </div>
          <div class="race-card-body">
            <p class="race-card-description">{{ race.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- 경고 메시지 -->
  <div>
    <p class="sheet-info-description">캐릭터 시트는 1사람당 1개만 작성할 수 있습니다. <br> 이미 시트를 작성한 상태라면 시트를 추가로 작성할 수 없습니다. </p>
  </div>
  <div v-if="showWarning" class="warning-text">입력하지 않은 값이 있습니다!</div>
  <div class="history-section">
    <div class="history-title-container">
      <img src="@/assets/images/character_sheet/Vector.png" alt="히스토리" class="history-title-image">
      <span class="history-title-text">히스토리</span>
    </div>
    <div class="history-input-container">
      <input 
        type="text" 
        v-model="localFormData.background" 
        id="history" 
        class="history-input" 
        maxlength="255" 
        placeholder="최대 255자" 
        @input="emitBackgroundChange" 
      />
      <img src="@/assets/images/character_sheet/history_box.png" alt="히스토리 입력 배경" class="history-input-image">
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, toRefs, watch } from 'vue';

const props = defineProps({
  formData: Object,
  currentOptions: Array,
  jobId: Number,  
  ruleId: Number,
});

const { formData } = toRefs(props);

const emit = defineEmits(['update:name', 'update:background', 'update:race-id']);

// 로컬 상태를 만들어 formData의 변경을 감지하고 부모에 반영
const localFormData = reactive({ ...formData.value });

// 경고 메시지 표시 여부
const showWarning = ref(false);

// 부모 컴포넌트에서 넘어온 값이 변경될 경우 로컬 상태를 동기화
watch(formData, (newValue) => {
  localFormData.name = newValue.name;
  localFormData.background = newValue.background;
  localFormData.raceId = newValue.raceId;
});

watch(localFormData, (newValue) => {
  emit('update:name', newValue.name);
  emit('update:background', newValue.background);
  emit('update:race-id', newValue.raceId);
  console.log('이름:', newValue.name); // 이름을 콘솔에 출력
  console.log('히스토리:', newValue.background); // 히스토리를 콘솔에 출력
  console.log('선택된 종족 ID:', newValue.raceId); // 선택된 종족을 콘솔에 출력
});

function emitNameChange() {
  emit('update:name', localFormData.name);
  checkForEmptyFields();
  console.log('이름:', localFormData.name); // 이름을 콘솔에 출력
}

function emitBackgroundChange() {
  emit('update:background', localFormData.background);
  checkForEmptyFields();
  console.log('히스토리:', localFormData.background); // 히스토리를 콘솔에 출력
}

function selectRace(raceId) {
  localFormData.raceId = raceId;
  emit('update:race-id', raceId);
  checkForEmptyFields();
  console.log('선택된 종족 ID:', raceId); // 선택된 종족을 콘솔에 출력
}

function checkForEmptyFields() {
  showWarning.value = !localFormData.name || !localFormData.raceId || !localFormData.background;
}
</script>

<style scoped>
.character-info-container {
  display: flex;
  width: 100%;
  margin-bottom: 20px; /* 히스토리와의 간격 추가 */
}

.left-section {
  width: 33%; /* 왼쪽 1/3 영역 */
  padding-right: 20px;
}

.right-section {
  width: 67%; /* 오른쪽 2/3 영역 */
  overflow-x: hidden;
}

.title-text {
  column-rule-color: white;
  font-size: 1.5rem;
  align-items: center;
  justify-content: center;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.name-input-container {
  position: relative;
  width: 100%;
  height: 50px; /* 배경 이미지와 같은 높이로 설정 */
}

.name-input-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.name-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  padding: 10px;
  border: none;
  background: transparent;
  color: #fff;
  text-align: center;
  z-index: 2; /* 배경 이미지 위에 텍스트가 오도록 설정 */
  box-sizing: border-box;
}

.race-title-container {
  text-align: center;
  margin-bottom: 10px;
}

.race-title {
  font-size: 1.5rem;
  color: #fff;
}

.race-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  overflow-x: hidden;
}

.action-card {
  flex: 0 0 calc(50% - 10px); /* 두 개의 카드가 한 줄에 나타나도록 설정 */
  background: rgba(0, 0, 0, 0.5);
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background 0.3s;
}

.sheet-info-description {
  font-size: 2rem;
  color: red;
  font-weight: bold;
  margin-bottom: 0;
  text-align: center;
  justify-content: center;
}

.action-card:hover {
  background: rgba(0, 0, 0, 0.7);
}

.action-card.selected {
  background: rgba(0, 0, 0, 0.7);
  border: 1px solid #fff;
}

.race-card-header {
  font-weight: bold;
  font-size: 1.2rem;
  margin-bottom: 5px;
  color: #ecf0f1;
}

.race-card-body {
  color: #bdc3c7;
}

.history-section {
  margin-top: 70px; /* 이름 및 종족과의 간격 추가 */
  position: relative;
}

.history-title-container, .title-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  position: relative;
}

.history-title-text {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 1.5rem;
  color: #fff;
}

.history-input-container {
  position: relative;
  width: 90%; /* 히스토리 입력 필드의 너비를 90%로 설정 */
  margin: 0 auto; /* 중앙 정렬 */
  height: 200px; /* 배경 이미지와 같은 높이로 설정 */
}

.history-input-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.history-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  padding: 10px;
  border: none;
  background: transparent;
  color: #fff;
  z-index: 2; /* 배경 이미지 위에 텍스트가 오도록 설정 */
  box-sizing: border-box;
}

.warning-text {
  font-size: 1.5rem;
  margin-top: 5%;
  margin-bottom: 20px;
  text-align: center;
  color: red;
  margin: 1rem;
  font-weight: bold;
}
</style>
