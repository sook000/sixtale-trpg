<template>
  <div class="modal-overlay" v-if="isVisible">
    <div class="modal-content" :style="modalContentStyle">
      <button class="close-button" @click="closeModal" aria-label="닫기">&times;</button>
      <div class="modal-header">
        <div class="modal-title-container">
          <img src="@/assets/images/character_sheet/title.png" alt="제목" class="modal-title-image">
          <h2 class="modal-title-text">액션 추가</h2>
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
        <div v-if="activeTab === 'core'" class="action-list">
          <div
            v-for="action in coreActions"
            :key="action.id"
            class="action-card"
            @click="selectAction(action)"
            :class="{ selected: selectedAction === action }"
          >
            <h3>{{ action.name }}</h3>
            <p v-html="formatDescription(action.description)"></p>
            <p v-if="action.isDice">주사위 타입: {{ action.diceType }} ({{ action.diceCount }}개)</p>
            <ul v-if="action.actionOption && action.actionOption.length" class="option-list">
              <li
                v-for="option in action.actionOption"
                :key="option.id"
                @click.stop="selectActionOption(option)"
                :class="{ selected: selectedOption === option }"
              >
                {{ option.content }}
              </li>
            </ul>
          </div>
          <div v-if="showInputField" class="input-section">
            <input
              v-model="inputText"
              placeholder="텍스트를 입력하세요"
              maxlength="20"
            />
            <button @click="saveInputText">저장</button>
          </div>
        </div>
        <div v-if="activeTab === 'advanced'" class="action-list">
          <div
            v-for="action in advancedActions"
            :key="action.id"
            class="action-card"
            @click="selectAction(action)"
            :class="{ selected: selectedAction === action }"
          >
            <h3>{{ action.name }}</h3>
            <p v-html="formatDescription(action.description)"></p>
            <p v-if="action.isDice">주사위 타입: {{ action.diceType }} ({{ action.diceCount }}개)</p>
            <ul v-if="action.actionOption && action.actionOption.length" class="option-list">
              <li
                v-for="option in action.actionOption"
                :key="option.id"
                @click.stop="selectActionOption(option)"
                :class="{ selected: selectedOption === option }"
              >
                {{ option.content }}
              </li>
            </ul>
          </div>
          <div v-if="showInputField" class="input-section">
            <input
              v-model="inputText"
              placeholder="텍스트를 입력하세요"
              maxlength="20"
            />
            <button @click="saveInputText">저장</button>
          </div>
        </div>
      </div>
      <div class="modal-footer" :style="modalFooterStyle">
        <button class="footer-button" :style="closeButtonStyle" @click="closeModal">닫기</button>
        <button
          class="footer-button"
          :style="saveButtonStyle"
          @click="saveAction"
          :disabled="!selectedAction"
        >추가</button>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, defineProps, defineEmits, onMounted } from 'vue';
import { getCharacterActions, addCharacterAction } from '@/common/api/ActionAPI.js';
import { getCharacterSheet } from '@/common/api/CharacterSheetAPI.js';

const props = defineProps({
  roomID: {
    type: Number,
    required: true,
  },
  playMemberID: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(['close', 'add-action']);

const isVisible = ref(true);
const activeTab = ref('core');
const tabs = ['core', 'advanced'];
const tabLabels = {
  core: '핵심액션',
  advanced: '고급액션',
};

const coreActions = ref([]);
const advancedActions = ref([]);
const selectedAction = ref(null);
const selectedOption = ref(null);
const showInputField = ref(false); // 텍스트 입력 필드 표시 여부
const inputText = ref(''); // 사용자 입력 텍스트

const fetchCharacterActions = async () => {
  try {
    const actions = await getCharacterActions(props.roomID, props.playMemberID);
    coreActions.value = actions.coreActions;
    advancedActions.value = actions.advancedActions;
  } catch (error) {
    console.error('Error fetching character actions:', error);
  }
};

const selectAction = (action) => {
  selectedAction.value = action;
  selectedOption.value = null;
  showInputField.value = false;
  inputText.value = ''; // 텍스트 초기화
};

const selectActionOption = (option) => {
  selectedOption.value = option;

  // 만약 옵션에 빈칸이 포함되어 있으면 showInputField를 true로 설정
  if (option.content.includes('_________')) {
    showInputField.value = true;
  } else {
    showInputField.value = false;
  }

  // 입력 필드를 초기화
  inputText.value = '';
};

const saveInputText = () => {
  if (selectedOption.value && inputText.value.trim() !== '') {
    // 입력한 텍스트를 반영하여 옵션 내용을 업데이트
    selectedOption.value.content = selectedOption.value.content.replace('_________', inputText.value);
    showInputField.value = false; // 텍스트 저장 후 필드 숨김
  } else {
    console.warn('No text input or selected option.');
  }
};



const isSaving = ref(false);

const saveAction = async () => {
  if (isSaving.value) return; // 이미 저장 중이면 중복 실행 방지

  if (selectedAction.value) {
    isSaving.value = true; // 저장 시작
    try {
      const roomId = Number(props.roomID);
      const actionID = selectedAction.value.id; // selectedAction에서 id를 가져옴

      // 입력된 텍스트가 없는 경우를 방지
      if (selectedOption.value && selectedOption.value.content.includes('_________')) {
        console.warn('Option content still contains placeholders:', selectedOption.value.content);
      }

      // 서버에 전송할 데이터를 준비합니다.
      const actionData = {
        actionID,
        actionOptionId: selectedOption.value ? selectedOption.value.id : null,
        // content 추가
        content: selectedOption.value ? selectedOption.value.content : null,
      };

      console.log('Sending data:', actionData);

      await addCharacterAction(roomId, props.playMemberID, actionID, actionData.actionOptionId);

      // 액션 추가 후 캐릭터 시트를 다시 불러옵니다.
      const updatedCharacterSheet = await getCharacterSheet(roomId, props.playMemberID);

      // 추가된 액션을 다시 찾아서 emit으로 전달
      const updatedAction = updatedCharacterSheet.characterAction.find(action => action.actionID === actionID);

      if (updatedAction) {
        emit('add-action', updatedAction);
      }

      closeModal();
    } catch (error) {
      console.error('Error adding action:', error);
    } finally {
      isSaving.value = false; // 저장 종료
    }
  } else {
    console.warn('No action selected.');
  }
};

const closeModal = () => {
  isVisible.value = false;
  emit('close');
};

onMounted(() => {
  fetchCharacterActions();
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

const getTabButtonStyle = (tab) => {
  const active = activeTab.value === tab;
  const imagePath = active
    ? require('@/assets/images/character_sheet/clicked_tab.png')
    : require('@/assets/images/character_sheet/tabButton.png');
  return {
    background: `url(${imagePath}) no-repeat center center`,
    backgroundSize: 'cover',
  };
};

const formatDescription = (description) => {
  return description.replace(/<br>/g, '');
};
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

.action-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-card {
  background: rgba(0, 0, 0, 0.5);
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.action-card:hover {
  background: rgba(0, 0, 0, 0.7);
}

.action-card.selected {
  background: rgba(0, 0, 0, 0.7);
  border: 1px solid #fff;
}

.action-card h3 {
  margin: 0 0 10px;
}

.action-card p {
  margin: 5px 0;
}

.action-card ul {
  margin: 5px 0;
}

.action-card ul li {
  cursor: pointer;
  margin: 3px 0; /* 리스트 간격을 추가 */
  padding: 5px; /* 클릭 영역을 더 넓게 하기 위해 패딩 추가 */
  border-radius: 3px; /* 선택된 옵션과 다른 옵션을 구분하기 위한 보더 레디우스 추가 */
  transition: background-color 0.3s;
}

.action-card ul li:hover {
  background-color: rgba(255, 255, 255, 0.1); /* 호버 시 살짝 배경 색을 변경 */
}

.action-card ul li.selected {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid #fff; /* 선택된 옵션을 구분하기 위한 보더 추가 */
}

.input-section {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

.input-section input {
  padding: 5px;
  font-size: 1rem;
  flex-grow: 1; /* 입력 필드가 가능한 넓게 차지하도록 */
}

.input-section button {
  padding: 5px 10px;
  cursor: pointer;
  background-color: #6b3d01;
  color: white;
  border: none;
  border-radius: 3px;
}
</style>
