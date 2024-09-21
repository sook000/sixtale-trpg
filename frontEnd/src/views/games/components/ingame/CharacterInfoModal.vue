<template>
  <div class="modal-overlay" v-if="isVisible">
    <div class="modal-content" :style="modalContentStyle">
      <button class="close-button" @click="closeModal" aria-label="닫기">&times;</button>
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
          :formData="characterData" 
          :statsData="statsData" 
          @update-stats="updateStatsData"
        />
      </div>
      <div class="modal-footer" :style="modalFooterStyle">
        <button class="footer-button" :style="closeButtonStyle" @click="closeModal">닫기</button>
        <button class="footer-button" :style="saveButtonStyle" @click="saveForm">저장</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits, onMounted } from 'vue';
import { updateCharacterSheet2 } from '@/common/api/CharacterSheetAPI.js';
import CharacterInfo from './CharacterInfo.vue';
import Equipment from './Equipment.vue';
import Stats from './CharacterStats2.vue';
import ActionTab from './ActionTab.vue';

const props = defineProps({
  roomID: {
    type: Number,
    required: true
  },
  playMemberID: {
    type: Number,
    required: true
  },
  formData: Object
});

const emit = defineEmits(['close']);

const isVisible = ref(true);
const activeTab = ref('character');
const tabs = ['character', 'stats', 'equipment', 'action'];
const tabLabels = {
  character: '캐릭터 정보',
  stats: '능력치',
  equipment: '장비',
  action: '액션',
};

const characterData = ref({ ...props.formData });

const statsData = ref({
  currentHp: 0,
  glove: 0,
  inspirationScore: 0,
  level: 1,
  exp: 0,
  attributes: {},
});

const actionData = ref([]);
const equipmentData = ref([]);

onMounted(() => {
  statsData.value = {
    currentHp: characterData.value.currentHp || 0,
    glove: characterData.value.glove || 0,
    inspirationScore: characterData.value.inspirationScore || 0,
    level: characterData.value.level || 1,
    exp: characterData.value.exp || 0,
    attributes: (characterData.value.stat || []).reduce((acc, stat) => {
      acc[stat.statID] = {
        value: stat.statValue,
        weight: stat.statWeight,
      };
      return acc;
    }, {}),
  };

  // actionData 초기화
  actionData.value = characterData.value.characterAction || [];
  
  equipmentData.value = characterData.value.characterEquipment || [];
});

import { toRaw } from 'vue';

const updateStatsData = (updatedData) => {
  console.log('Parent received updated statsData:', updatedData);

  statsData.value = { ...updatedData };

  // characterData를 업데이트
  Object.assign(characterData.value, {
    level: statsData.value.level,
    currentHp: statsData.value.currentHp,
    glove: statsData.value.glove,
    inspirationScore: statsData.value.inspirationScore,
    exp: statsData.value.exp,
    stat: Object.keys(statsData.value.attributes).map(key => ({
      statID: parseInt(key),
      statValue: statsData.value.attributes[key].value,
      statWeight: statsData.value.attributes[key].weight,
    })),
  });

  console.log('Updated characterData after applying stats:', toRaw(characterData.value));
};


const saveForm = async () => {
  try {
    const rawCharacterData = toRaw(characterData.value);
    const updatedData = {
      jobId: rawCharacterData.jobId,
      raceId: rawCharacterData.raceId,
      beliefId: rawCharacterData.beliefId,
      name: rawCharacterData.name,
      appearance: rawCharacterData.appearance,
      background: rawCharacterData.background,
      stat: rawCharacterData.stat,
      characterAction: rawCharacterData.characterAction,
      characterEquipment: rawCharacterData.characterEquipment,
      currentWeight: rawCharacterData.currentWeight,
      currentHp: rawCharacterData.currentHp,
      currentMoney: rawCharacterData.currentMoney,
      limitWeight: rawCharacterData.limitWeight,
      limitHp: rawCharacterData.limitHp,
      glove: rawCharacterData.glove,
      inspirationScore: rawCharacterData.inspirationScore,
      level: rawCharacterData.level,
      exp: rawCharacterData.exp,
      imageURL: rawCharacterData.imageURL,
    };

    const response = await updateCharacterSheet2(props.roomID, props.playMemberID, updatedData);
    console.log('Save successful:', response);


    closeModal();
  } catch (error) {
    console.error('Error saving form data:', error);
  }
};



const closeModal = () => {
  isVisible.value = false;
  emit('close');
};

const activeComponent = computed(() => {
  switch (activeTab.value) {
    case 'character':
      return CharacterInfo;
    case 'equipment':
      return Equipment;
    case 'stats':
      return Stats;
    case 'action':
      return ActionTab;
    default:
      return CharacterInfo;
  }
});

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

const closeButtonStyle = computed(() => ({
  background: `url(${require('@/assets/images/maps/background/close.png')}) no-repeat center center`,
  backgroundSize: 'cover',
}));

const saveButtonStyle = computed(() => ({
  background: `url(${require('@/assets/images/maps/background/save.png')}) no-repeat center center`,
  backgroundSize: 'cover',
}));

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
</script>


<style scoped>
/* 기존 스타일 유지 */
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
  width: 800px;
  max-width: 90%;
  height: 600px;
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
  left: 15%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 1.5rem;
}

.modal-tabs {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0px;
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
  margin-top: 10px;
  scrollbar-color: #855e2fee #201805;
}

.modal-body::-webkit-scrollbar {
  width: 8px;
}

.modal-body::-webkit-scrollbar-track {
  background: #201805;
  border-radius: 5px;
}

.modal-body::-webkit-scrollbar-thumb {
  background-color: #855e2fee;
  border-radius: 5px;
  border: 2px solid #201805;
}

.modal-footer {
  display: flex;
  justify-content: center;
  padding: 20px;
  border-radius: 0 0 10px 10px;
}

.footer-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  background-size: cover;
  color: #fff;
  cursor: pointer;
  margin: 0 10px;
}

.footer-button:hover {
  transform: translateY(-2px);
}

.footer-button.save-button {
  background-size: cover;
}

.footer-button.save-button:hover {
  background-size: cover;
}
</style>
