<template>
  <div class="modal-overlay">
    <div class="modal-content" :style="modalContentStyle">
      <button class="close-button" @click="$emit('close')" :style="closeButtonStyle" aria-label="닫기">&times;</button>
      <div class="modal-header">
        <div class="modal-title-container">
          <img src="@/assets/images/character_sheet/title.png" alt="제목" class="modal-title-image">
          <h2 class="modal-title-text">상세 설정</h2>
        </div>
      </div>
      <div class="modal-tabs">
        <button
          class="tab-button"
          v-for="tab in tabs"
          :key="tab"
          :class="{ active: activeTab === tab }"
          @click="activeTab = tab"
          :style="getTabButtonStyle(tab)"
        >{{ tabLabels[tab] }}</button>
      </div>
      <div class="modal-body" :style="modalBodyStyle">
        <component 
          :is="activeComponent"
          :form-data="formData"
          :current-options="currentTabOptions"
          :job-id="jobID"
          :rule-id="ruleID"
          v-bind="appearanceProps"
          @update:job-id="(newJobId) => formData.jobId = newJobId"
          @update:race-id="(newRaceId) => formData.raceId = newRaceId"
          @update:belief-id="(newBeliefId) => formData.beliefId = newBeliefId"
          @update:name="(newName) => formData.name = newName"
          @update:appearance="(newAppearance) => formData.appearance = newAppearance"
          @update:background="(newBackground) => formData.background = newBackground"
          @update:stats="(newStats) => formData.stat = newStats"
          @update:character-action="(newCharacterAction) => formData.characterAction = newCharacterAction"
          @update:character-equipment="(newEquipment) => formData.characterEquipment = newEquipment"
          @update:candidate-images="updateCandidateImages"
          @update:selected-image-url="(newImageUrl) => formData.imageURL = newImageUrl"
        ></component>
      </div>
      <div class="modal-footer" :style="modalFooterStyle">
        <button class="footer-button" :style="closeButtonStyle" @click="$emit('close')">닫기</button>
        <button 
          class="footer-button save-button" 
          :style="saveButtonStyle" 
          @click="saveForm" 
          :disabled="isSaving"
        >
          <div v-if="isSaving" class="loader"></div>
          <span>{{ isSaving ? '저장 중...' : '저장' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineEmits } from 'vue';
import { useRoute } from 'vue-router';
import { getJobSelectOption } from '@/common/api/JobAPI.js';
import { getRoomInfo } from '@/common/api/RoomsAPI.js';
import { createCharacterSheet } from '@/common/api/CharacterSheetAPI.js';
import Appearance from './Appearance.vue';
import CharacterInfo from './CharacterInfo.vue';
import Values from './Values.vue';
import Equipment from './Equipment.vue';
import Stats from './Stats.vue';
import Actions from './Actions.vue';

import closeButtonImage from '@/assets/images/character_sheet/close.png';
import saveButtonImage from '@/assets/images/character_sheet/save.png';

const route = useRoute();
const emit = defineEmits(['close', 'save-success']);

const roomID = ref(Number(route.params.roomId));
const props = defineProps({
  job: {
    type: Object,
    required: true,
  }
});
const jobID = ref(Number(props.job.id));
const ruleID = ref(null);
const jobOptions = ref({
  jobBeliefList: [],
  jobRaceList: [],
  jobActionList: [],
  jobEquipmentList: []
});
const isLoading = ref(true);
const isSaving = ref(false); // 저장 중임을 나타내는 상태

const fetchRuleID = async () => {
  try {
    const roomInfo = await getRoomInfo(roomID.value);
    ruleID.value = roomInfo.ruleID;
    await fetchJobOptions();
  } catch (error) {
    console.error('Error fetching room info:', error);
  }
};

const fetchJobOptions = async () => {
  try {
    if (ruleID.value && jobID.value) {
      jobOptions.value = await getJobSelectOption(ruleID.value, jobID.value);
    } else {
      console.error('Cannot fetch job options, ruleID or jobID is undefined');
    }
  } catch (error) {
    console.error('Error fetching job options:', error);
  } finally {
    isLoading.value = false;
  }
};

const activeTab = ref('character');
const tabs = ['character', 'values', 'stats', 'equipment', 'actions', 'appearance'];
const tabLabels = {
  character: '캐릭터 정보',
  values: '가치관',
  stats: '능력치',
  equipment: '장비',
  actions: '액션',
  appearance: '외모'
};

const formData = ref({
  raceId: null,
  beliefId: null,
  name: '',
  appearance: '',
  background: '',
  stat: [],
  characterAction: [],
  characterEquipment: [],
  currentWeight: 0,
  currentHp: 0,
  currentMoney: 0,
  limitWeight: 0,
  limitHp: 0,
  glove: 0,
  inspirationScore: 0,
  level: 1,
  exp: 0,
  imageURL: ''
});

const candidateImages = ref([null, null, null]);

const updateCandidateImages = (newCandidateImages) => {
  candidateImages.value = newCandidateImages;
  console.log('후보 이미지 리스트 업데이트:', candidateImages.value);
};

const raceOptions = computed(() => jobOptions.value.jobRaceList || []);
const beliefOptions = computed(() => jobOptions.value.jobBeliefList || []);
const equipmentOptions = computed(() => jobOptions.value.jobEquipmentList || []);
const actionOptions = computed(() => jobOptions.value.jobActionList || []);

const appearanceProps = computed(() => {
  return activeTab.value === 'appearance' ? { candidateImages: candidateImages.value } : {};
});

const activeComponent = computed(() => {
  if (isLoading.value) return null;
  switch (activeTab.value) {
    case 'character': return CharacterInfo;
    case 'values': return Values;
    case 'equipment': return Equipment;
    case 'stats': return Stats;
    case 'actions': return Actions;
    case 'appearance': return Appearance;
    default: return CharacterInfo;
  }
});

const currentTabOptions = computed(() => {
  if (isLoading.value) return [];
  switch (activeTab.value) {
    case 'character': return jobOptions.value.jobRaceList;
    case 'values': return jobOptions.value.jobBeliefList;
    case 'equipment': return jobOptions.value.jobEquipmentList;
    case 'actions': return jobOptions.value.jobActionList;
    default: return [];
  }
});

function getTabButtonStyle(tab) {
  const active = activeTab.value === tab;
  const imagePath = active
    ? require('@/assets/images/character_sheet/clicked_tab.png')
    : require('@/assets/images/character_sheet/tabButton.png');
  return {
    background: `url(${imagePath}) no-repeat center center`,
    backgroundSize: 'cover',
  };
}

const closeButtonStyle = computed(() => ({
  background: `url(${closeButtonImage}) no-repeat center center`,
  backgroundSize: 'cover',
  width: '80px',
  height: '40px',
  border: 'none',
  cursor: 'pointer',
}));

const saveButtonStyle = computed(() => ({
  background: `url(${saveButtonImage}) no-repeat center center`,
  backgroundSize: 'cover',
  width: '80px',
  height: '40px',
  border: 'none',
  cursor: 'pointer',
}));

const modalContentStyle = computed(() => ({
  background: `url(${require('@/assets/images/character_sheet/main_background.png')}) no-repeat center center`,
  backgroundSize: 'cover',
}));

const modalBodyStyle = computed(() => ({
  background: `url(${require('@/assets/images/character_sheet/tab_background.png')}) no-repeat center center`,
  backgroundSize: 'cover',
  marginTop: '10px',
}));

const modalFooterStyle = computed(() => ({
  background: `url(${require('@/assets/images/character_sheet/main_background.png')}) no-repeat center center`,
  backgroundSize: 'cover',
}));

onMounted(() => {
  fetchRuleID();
  formData.value.jobId = jobID.value;
});

function saveForm() {
  isSaving.value = true; // 저장 시작
  const characterSheetRequestData = {
    jobId: formData.value.jobId,
    raceId: formData.value.raceId,
    beliefId: formData.value.beliefId,
    name: formData.value.name,
    appearance: formData.value.appearance,
    background: formData.value.background,
    stat: formData.value.stat.map(stat => ({
      statID: stat.statID,
      statValue: Number(stat.statValue),
      statWeight: Number(stat.statWeight),
    })),
    characterAction: formData.value.characterAction.map(action => ({
      actionID: action.actionID,
      actionOptionId: action.actionOptionId || null,
    })),
    characterEquipment: formData.value.characterEquipment.map(item => ({
      equipmentId: item.equipmentId,
      currentCount: item.currentCount,
      weight: item.weight,
    })),
    currentWeight: formData.value.currentWeight,
    currentHp: formData.value.currentHp,
    currentMoney: formData.value.currentMoney,
    limitWeight: formData.value.limitWeight,
    limitHp: formData.value.limitHp,
    glove: formData.value.glove,
    inspirationScore: formData.value.inspirationScore,
    level: formData.value.level,
    exp: formData.value.exp,
    imageURL: formData.value.imageURL,
  };

  createCharacterSheet(roomID.value, characterSheetRequestData)
    .then(response => {
      console.log('캐릭터 시트 생성 성공:', response);
      alert('캐릭터 시트가 성공적으로 생성되었습니다.');
      emit('save-success', jobID.value);
    })
    .catch(error => {
      console.error('캐릭터 시트 생성 중 오류 발생:', error);
      alert('캐릭터 시트 생성 중 오류가 발생했습니다.');
    })
    .finally(() => {
      isSaving.value = false; // 저장 완료
    });
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: transparent;
  padding: 0;
  border-radius: 10px;
  width: 1300px;
  max-width: 90%;
  height: 1200px;
  max-height: 90%;
  position: relative;
  color: #fff;
  overflow: hidden;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #fff;
}

.modal-header {
  text-align: center;
  margin-bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  width: 100%;
  overflow: hidden;
}

.modal-title-container {
  position: relative;
  width: 100%;
}

.modal-title-image {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.modal-title-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-600%, -50%);
  color: white;
  font-size: 1.5rem;
}

.modal-tabs {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0px; /* Reduce bottom margin */
}

.tab-button {
  flex: 1;
  padding: 10px;
  background-size: cover;
  border: none;
  color: #fff;
  cursor: pointer;
  margin-right: 5px;
  position: relative;
}

.tab-button.active {
  background-size: cover;
  color: white;
}

.modal-body {
  padding: 20px;
  border-radius: 0 0 10px 10px;
  flex: 1;
  overflow-y: auto;
  margin-top: 10px; /* Reduce top margin */
  overflow-x: hidden;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #855e2fee #201805;
}

.input-group {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.input-group label {
  display: block;
  margin-bottom: 5px;
}

.input-group input,
.input-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #555;
  border-radius: 5px;
  background: #333;
  color: #fff;
}

.input-group textarea {
  height: 100px;
}

.race-buttons {
  display: flex;
}

.race-button {
  flex: 1;
  padding: 10px;
  margin-right: 5px;
  border: none;
  border-radius: 5px;
  background: #555;
  color: #fff;
  cursor: pointer;
}

.race-button:hover {
  background: #777;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.action-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  background: #007bff;
  color: #fff;
  cursor: pointer;
}

.action-button:hover {
  background: #0056b3;
}

.modal-footer {
  display: flex;
  justify-content: center; /* Center-align buttons */
  padding: 20px;
  border-radius: 0 0 10px 10px;
}

.footer-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  background: #3a3a3a;
  color: #fff;
  cursor: pointer;
  margin: 0 10px; /* Add margin between buttons */
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
}

.footer-button {
  margin-right: 10px;
  padding: 10px 20px;
  font-size: 1rem;
  color: white;
}

.save-button {
  margin-left: 10px;
}

.loader {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
