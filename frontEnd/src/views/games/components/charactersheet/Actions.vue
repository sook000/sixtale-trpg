<template>
  <div class="action-container">
    <div class="description-bar">
      <img src="@/assets/images/character_sheet/name_box.png" alt="설명 배경" class="description-bar-image">
      <div class="description-text">
        핵심 액션 중 1개를 골라야 합니다. 액션 옵션이 있다면 그 옵션도 1개를 고르세요.
      </div>
    </div>
    <div class="action-buttons" v-if="coreActions.length">
      <div 
        v-for="(action, index) in coreActions" 
        :key="index" 
        class="action-button"
        :style="getActionStyle(action)"
        :class="{ active: selectedActionIndex === index }"
        @click="selectAction(index, action)"
      >
        <span class="action-title">{{ action.name }}</span>
        <div class="action-content" v-html="formatDescription(action.description)"></div>
        <div v-if="action.actionOptions && action.actionOptions.length > 0" class="options-section">
          <div 
            v-for="option in action.actionOptions" 
            :key="option.id" 
            class="option-item"
            :class="{ selected: selectedOptionId === option.id }"
            @click="selectOption(option.id)"
          >
            {{ option.content }}
          </div>
        </div>
      </div>
    </div>
    <div v-if="showWarning" class="warning-text">핵심 액션 및 옵션을 선택하세요!</div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';

const props = defineProps({
  formData: {
    type: Object,
    required: true
  },
  currentOptions: {
    type: Array,
    required: true
  },
  jobId: Number,  
  ruleId: Number
});

const emit = defineEmits(['update:character-action']);

const selectedActionIndex = ref(null);
const selectedOptionId = ref(null);
const showWarning = ref(false);

// 핵심 액션만 필터링
const coreActions = computed(() => {
  return Array.isArray(props.currentOptions) ? props.currentOptions.filter(action => action.isCore) : [];
});

const selectAction = (index, action) => {
  if (action && typeof index === 'number') {
    selectedActionIndex.value = index;
    selectedOptionId.value = null; // 옵션 초기화
    updateCharacterAction(action.id, null);
    console.log('선택된 액션:', action.id);
  }
};

const selectOption = (optionId) => {
  if (selectedActionIndex.value !== null && coreActions.value[selectedActionIndex.value]) {
    selectedOptionId.value = optionId;
    const selectedAction = coreActions.value[selectedActionIndex.value];
    if (selectedAction) {
      updateCharacterAction(selectedAction.id, selectedOptionId.value);
      console.log('선택된 옵션 ID:', optionId);
    }
  }
};

const updateCharacterAction = (actionId, optionId) => {
  if (actionId) {
    props.formData.characterAction = [{ actionID: actionId, actionOptionId: optionId }];
    emit('update:character-action', props.formData.characterAction);
    showWarning.value = false;
  }
};

// formData가 업데이트될 때 선택한 액션과 옵션을 유지
watch(() => props.formData.characterAction, (newActions) => {
  if (newActions && newActions.length > 0) {
    const selectedAction = newActions[0];
    if (coreActions.value && Array.isArray(coreActions.value)) {
      selectedActionIndex.value = coreActions.value.findIndex(a => a.id === selectedAction.actionID);
      selectedOptionId.value = selectedAction.actionOptionId;
    }
  }
}, { immediate: true });

// onMounted에 저장된 액션과 옵션 로드
onMounted(() => {
  if (props.formData.characterAction && props.formData.characterAction.length > 0) {
    const selectedAction = props.formData.characterAction[0];
    if (coreActions.value && Array.isArray(coreActions.value)) {
      selectedActionIndex.value = coreActions.value.findIndex(a => a.id === selectedAction.actionID);
      selectedOptionId.value = selectedAction.actionOptionId;
    }
  }
});

const getActionStyle = (action) => {
  if (!action || selectedActionIndex.value === null || !coreActions.value[selectedActionIndex.value]) {
    return {};  // action 또는 선택된 액션이 undefined인 경우 빈 객체 반환
  }
  return {
    backgroundImage: `url(${require('@/assets/images/character_sheet/action_box.png')})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    padding: '20px',
    color: '#fff',
    borderRadius: '8px',
    overflow: 'hidden',
    border: coreActions.value[selectedActionIndex.value].id === action.id ? '2px solid white' : 'none',
  };
};

const formatDescription = (description) => {
  if (description) {
    return description
      .replace(/•/g, '\n•');    // • 기호 앞에 개행 문자 추가
  } else {
    return '';  // 기본값으로 빈 문자열 설정
  }
};
</script>

<style scoped>
.action-container {
  padding: 20px;
}

.description-bar {
  position: relative;
  margin-bottom: 20px;
}

.description-bar-image {
  margin-left: 10%;
  width: 80%;
  height: 100px;
  display: block;
}

.description-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.5rem;
  font-weight: bold;
  color: #fff;
  text-align: center;
  width: 90%;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.action-button {
  position: relative;
  padding: 20px;
  transition: transform 0.3s, box-shadow 0.3s;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  max-width: 100%; /* Ensure the action button does not exceed the container width */
}

.action-button.active {
  transform: scale(1.05);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
}

.action-content {
  margin: 10px 0;
  font-size: 0.9rem;
  line-height: 1.4;
  word-wrap: break-word;
  white-space: pre-wrap; /* Preserve formatting including line breaks */
}

.options-section {
  margin-top: 10px;
}

.option-item {
  padding: 10px;
  background-color: #0a0603; /* 황갈색 계열로 변경 */
  color: #fff;
  margin-top: 5px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
  border: 2px solid transparent; /* Transparent border for smooth selection transition */
  max-width: 100%; /* Prevent overflow */
}

.option-item.selected {
  background-color: #8B5A2B; 
  border-color: white; /* 선택 시 하얀 테두리 */
}

.option-item:hover {
  background-color: #8B5A2B; /* Hover 효과 */
}

.selected-option {
  margin-top: 10px;
  font-weight: bold;
  color: #fff;
}

.warning-text {
  color: red;
  font-size: 1.2rem;
  margin-top: 20px;
  text-align: center;
}
</style>
