<template>
  <div class="attributes-content">
    <div class="description-bar">
      <img src="@/assets/images/character_sheet/stats_description.png" alt="설명 배경" class="description-bar-image">
      <div class="description-text" v-html="formattedDescription"></div>
    </div>
    <div class="top-description">
      <img src="@/assets/images/character_sheet/stat_description.png" alt="책갈피 배경" class="top-description-image">
      <div class="top-description-text">16, 15, 13, 12, 9, 8의 여섯 수치를 원하는 능력치에 배정합니다.</div>
    </div>
    <div class="stats-content">
      <div v-if="showWarning" class="warning-text">선택하지 않은 능력치가 있습니다!</div>
      <div class="attribute-rows">
        <div class="attribute-row" v-for="(row, rowIndex) in groupedAttributes" :key="rowIndex">
          <div class="attribute" v-for="(attribute, index) in row" :key="index">
            <span class="attribute-name">{{ attribute.name }}</span>
            <div class="attribute-value-container">
              <img src="@/assets/images/character_sheet/stats_select.png" alt="값 배경" class="attribute-value-background">
              <select class="attribute-select" v-model="localAttributes[attribute.key]" @change="emitAttributeUpdate(attribute.key)">
                <option value="" disabled>선택</option>
                <option v-for="option in availableOptions(attribute.key)" :value="option" :key="option">{{ option }}</option>
                <option value="">선택 취소</option>
              </select>
              <div class="attribute-circle">
                <img src="@/assets/images/character_sheet/stats_circle.png" alt="보정치 배경" class="circle-background">
                <span class="modifier-value">{{ getModifier(localAttributes[attribute.key]) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="reset-container">
        <button class="reset-button" @click="resetAttributes">리셋</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue';

const props = defineProps({
  formData: {
    type: Object,
    required: true
  }
});

// formData의 stat 속성을 로컬 상태로 설정
const localAttributes = reactive({ 
  strength: props.formData.stat.find(attr => attr.statID === 1) ? props.formData.stat.find(attr => attr.statID === 1).statValue : '',
  dexterity: props.formData.stat.find(attr => attr.statID === 2) ? props.formData.stat.find(attr => attr.statID === 2).statValue : '',
  constitution: props.formData.stat.find(attr => attr.statID === 3) ? props.formData.stat.find(attr => attr.statID === 3).statValue : '',
  intelligence: props.formData.stat.find(attr => attr.statID === 4) ? props.formData.stat.find(attr => attr.statID === 4).statValue : '',
  wisdom: props.formData.stat.find(attr => attr.statID === 5) ? props.formData.stat.find(attr => attr.statID === 5).statValue : '',
  charisma: props.formData.stat.find(attr => attr.statID === 6) ? props.formData.stat.find(attr => attr.statID === 6).statValue : '',
});

const emit = defineEmits(['update:stats']);

// 모든 능력치가 선택되지 않았을 때 경고를 표시하는 상태
const showWarning = ref(false);

// 능력치 정보 배열
const attributes = ref([
  { id: 1, name: '근력', key: 'strength' },
  { id: 2, name: '민첩성', key: 'dexterity' },
  { id: 3, name: '체력', key: 'constitution' },
  { id: 4, name: '지능', key: 'intelligence' },
  { id: 5, name: '지혜', key: 'wisdom' },
  { id: 6, name: '매력', key: 'charisma' }
]);

// 능력치를 두 개씩 그룹으로 묶기
const groupedAttributes = computed(() => {
  const result = [];
  for (let i = 0; i < attributes.value.length; i += 2) {
    result.push(attributes.value.slice(i, i + 2));
  }
  return result;
});

// 초기 선택 옵션
const initialOptions = ['16', '15', '13', '12', '9', '8'];

// 현재 선택된 값
const selectedValues = computed(() => 
  Object.values(localAttributes).filter(val => val)
);

// 선택 가능한 옵션을 반환
const availableOptions = (key) => {
  return initialOptions.filter(option => !selectedValues.value.includes(option) || localAttributes[key] === option);
};

// 선택한 값을 부모로 전달
const emitAttributeUpdate = (key) => {
  const updatedStats = attributes.value.map(attribute => ({
    statID: attribute.id,
    statValue: localAttributes[attribute.key],
    statWeight: getModifier(localAttributes[attribute.key])
  }));
  emit('update:stats', updatedStats);

  console.log('선택한 능력치:', updatedStats); // 선택한 능력치를 콘솔에 출력

  // 선택되지 않은 능력치가 있는지 확인
  checkAllAttributesSelected();
};

// 선택되지 않은 능력치가 있는지 확인
const checkAllAttributesSelected = () => {
  showWarning.value = Object.values(localAttributes).some(val => !val);
};

// 리셋 버튼 클릭 시 모든 능력치를 초기화
const resetAttributes = () => {
  Object.keys(localAttributes).forEach(key => {
    localAttributes[key] = '';
    emitAttributeUpdate(key);
  });
  checkAllAttributesSelected(); // 리셋 후 경고 메시지 표시
  console.log('능력치가 리셋되었습니다.'); // 리셋 후 콘솔에 출력
};

// 부모에서 업데이트된 데이터를 로컬 상태로 동기화
watch(() => props.formData.stat, (newStat) => {
  newStat.forEach(attr => {
    const foundAttribute = attributes.value.find(attribute => attribute.id === attr.statID);
    const key = foundAttribute ? foundAttribute.key : null;
    if (key) localAttributes[key] = attr.statValue;
  });
});

// 컴포넌트가 마운트될 때 선택되지 않은 능력치가 있는지 확인
onMounted(checkAllAttributesSelected);

// 능력치 보정치 계산
const getModifier = (value) => {
  if (!value) return '';
  const num = parseInt(value, 10);
  const modifier = Math.floor((num - 10) / 2);
  return modifier > 0 ? `+${modifier}` : modifier;
};

// 설명 텍스트
const descriptionText = `
  많은 룰에서 플레이어 캐릭터의 능력치와 능력수정치를 사용합니다. 
  능력치는 근력, 체력, 민첩성, 지능, 지혜, 매력의 여섯 가지이고, 3에서 18까지로 정의됩니다. 
  15은 사람으로서 최고 수준임을 가리킵니다. 판정을 할 때는 능력치에서 유래한 능력보정치가 사용됩니다. 
  근력, 체력, 민첩성, 지혜, 매력의 약자를 씁니다. 능력수정치는 -3에서 +3까지이고, 능력치를 따라서 정해집니다.
`;

const formattedDescription = computed(() => {
  return descriptionText.replace(/\.\s/g, '.<br>');
});
</script>



<style scoped>
.attributes-content {
  position: relative;
  padding: 20px;
  background-color: #131010;
  color: #fff;
}

.description-bar {
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.description-bar-image {
  width: 100%;
  max-width: 900px;
  max-height: 385px;
  height: 4;
}

.description-text {
  position: absolute;
  top: 20px;
  left: 16%;
  right: 10%;
  color: white;
  font-size: 1.2rem;
  text-align: left;
  padding: 10px;
  white-space: pre-wrap;
}

.top-description {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.top-description-image {
  width: 100%;
  max-width: 600px;
  height: auto;
}

.top-description-text {
  position: absolute;
  top: 455px;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.1rem;
  font-weight: bold;
  color: #fff;
  text-align: center;
}

.stats-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.attribute-rows {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.attribute-row {
  display: flex;
  justify-content: space-between;
  width: 80%;
  margin-bottom: 20px;
}

.attribute {
  background-color: transparent;
  padding: 5px 10px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
  width: 45%;
}

.attribute-name {
  font-size: 1.5rem;
  margin-bottom: 5px;
}

.attribute-value-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
}

.attribute-value-background {
  width: 140px;
  height: 60px;
}

.attribute-select {
  width: 100%;
  font-size: 1.5rem;
  background-color: transparent;
  color: #fff;
  border: none;
  text-align: center;
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  cursor: pointer;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
}

.attribute-select option {
  background-color: #131010;
  color: #fff;
}

.attribute-circle {
  position: relative;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 10px;
}

.circle-background {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
}

.modifier-value {
  position: absolute;
  top: 35%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.2rem;
  font-weight: bold;
  color: #fff;
  text-align: center;
}

.warning-text {
  font-size: 1.5rem;
  color: red;
  margin: 1rem;
  font-weight: bold;
}

.reset-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.reset-button {
  background-color: #4c4c4c;
  color: #fff;
  font-size: 1.2rem;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.reset-button:hover {
  background-color: #6c6c6c;
}
</style>
