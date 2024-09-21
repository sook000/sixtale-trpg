<template>
  <div class="attributes-content">
    <div class="description-bar">
      <img src="@/assets/images/character_sheet/stat_description.png" alt="설명 바" class="description-bar-image">
      <span class="description-bar-text" v-html="descriptionText"></span>
    </div>
    <div class="level-container">
      <div class="level-controls">
        <img src="@/assets/images/ingame/Minus.png" alt="감소" class="control-button" @click="decreaseLevel">
        <span class="level-text">Lv. {{ localStatsData.level }}</span>
        <img src="@/assets/images/ingame/Plus.png" alt="증가" class="control-button" @click="increaseLevel">
      </div>
    </div>
    <div class="stats-grid">
      <!-- 개별 속성들 렌더링 -->
      <div class="stat-item" v-for="(attr, index) in individualAttributes" :key="index">
        <div class="stat-name">{{ attr.name }}</div>
        <div class="stat-controls">
          <img src="@/assets/images/ingame/Minus.png" alt="감소" class="control-button" @click="decreaseIndividualValue(attr.key)">
          <div class="stat-value-container">
            <div class="stat-value">{{ localStatsData[attr.key] }}</div>
          </div>
          <img src="@/assets/images/ingame/Plus.png" alt="증가" class="control-button" @click="increaseIndividualValue(attr.key)">
        </div>
      </div>
      <!-- 스탯 속성들 렌더링 -->
      <div class="stat-item" v-for="(attribute, index) in attributes" :key="index">
        <div class="stat-name">{{ attribute.name }}</div>
        <div class="stat-controls">
          <img src="@/assets/images/ingame/Minus.png" alt="감소" class="control-button" @click="decreaseStatValue(attribute.key, attribute.name)">
          <div class="stat-value-container">
            <div class="stat-value">{{ localStatsData.attributes[attribute.key] ? localStatsData.attributes[attribute.key].value : 0 }}</div>
          </div>
          <img src="@/assets/images/ingame/Plus.png" alt="증가" class="control-button" @click="increaseStatValue(attribute.key, attribute.name)">
        </div>
        <div class="stat-weight-controls" v-if="localStatsData.attributes[attribute.key] && localStatsData.attributes[attribute.key].weight !== undefined">
          <img src="@/assets/images/ingame/Minus.png" alt="감소" class="control-button" @click="decreaseWeight(attribute.key, attribute.name)">
          <div class="stat-weight-container">
            <div class="stat-weight">{{ localStatsData.attributes[attribute.key].weight }}</div>
            <div class="weight-label">(가중치)</div>
          </div>
          <img src="@/assets/images/ingame/Plus.png" alt="증가" class="control-button" @click="increaseWeight(attribute.key, attribute.name)">
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, watch } from 'vue';

const props = defineProps({
  statsData: Object,
});

const emit = defineEmits(['update-stats']); // 여기에 update-stats 이벤트를 명시적으로 선언

// deep copy 함수
function deepCopy(obj) {
  return JSON.parse(JSON.stringify(obj));
}

// statsData를 ref로 관리
const localStatsData = ref(deepCopy(props.statsData));

// props로 전달된 데이터가 변경될 때 localStatsData를 업데이트
watch(
  () => props.statsData,
  (newStats) => {
    localStatsData.value = deepCopy(newStats);
  },
  { deep: true }
);

// 설명 텍스트
const descriptionText = `
  캐릭터의 현재 능력치를 확인하고, 필요에 따라 수정할 수 있습니다.<br>
  변경 사항은 저장 버튼을 누를 때만 적용됩니다.
`;

// 개별 속성들의 정의
const individualAttributes = [
  { name: '경험치', key: 'exp' },
  { name: 'HP', key: 'currentHp' },
  { name: '장갑', key: 'glove' },
  { name: '고양감', key: 'inspirationScore' },
];

// 스탯 속성들의 정의
const attributes = [
  { name: '근력', key: 1 },
  { name: '민첩성', key: 2 },
  { name: '체력', key: 3 },
  { name: '지능', key: 4 },
  { name: '지혜', key: 5 },
  { name: '매력', key: 6 },
];

// 변경사항을 부모 컴포넌트로 전달하는 함수
const emitChanges = () => {
  console.log('Emitting updated statsData:', deepCopy(localStatsData.value));
  emit('update-stats', deepCopy(localStatsData.value));  // 이벤트 이름을 'update-stats'로 변경
};


// 개별 속성들에 대한 값 증가 및 감소 함수
const increaseIndividualValue = (key) => {
  if (localStatsData.value[key] !== undefined) {
    localStatsData.value[key]++;
    console.log(`Increased ${key}: ${localStatsData.value[key]}`);
    emitChanges();
  }
};

const decreaseIndividualValue = (key) => {
  if (localStatsData.value[key] > 0) {
    localStatsData.value[key]--;
    console.log(`Decreased ${key}: ${localStatsData.value[key]}`);
    emitChanges();
  }
};

// 스탯 속성들에 대한 값 증가 및 감소 함수
const increaseStatValue = (key) => {
  if (localStatsData.value.attributes[key]) {
    localStatsData.value.attributes[key].value++;
    console.log(`Increased stat ID ${key}: ${localStatsData.value.attributes[key].value}`);
    emitChanges();
  }
};

const decreaseStatValue = (key) => {
  if (localStatsData.value.attributes[key] && localStatsData.value.attributes[key].value > 0) {
    localStatsData.value.attributes[key].value--;
    console.log(`Decreased stat ID ${key}: ${localStatsData.value.attributes[key].value}`);
    emitChanges();
  }
};

// 가중치 증가 및 감소 함수
const increaseWeight = (key) => {
  if (localStatsData.value.attributes[key]) {
    localStatsData.value.attributes[key].weight++;
    console.log(`Increased Weight for stat ID ${key}: ${localStatsData.value.attributes[key].weight}`);
    emitChanges();
  }
};

const decreaseWeight = (key) => {
  if (localStatsData.value.attributes[key] && localStatsData.value.attributes[key].weight > 0) {
    localStatsData.value.attributes[key].weight--;
    console.log(`Decreased Weight for stat ID ${key}: ${localStatsData.value.attributes[key].weight}`);
    emitChanges();
  }
};

// 레벨 증가 및 감소 함수
const increaseLevel = () => {
  localStatsData.value.level++;
  console.log(`Increased Level: ${localStatsData.value.level}`);
  emitChanges();
};

const decreaseLevel = () => {
  if (localStatsData.value.level > 1) {
    localStatsData.value.level--;
    console.log(`Decreased Level: ${localStatsData.value.level}`);
    emitChanges();
  }
};
</script>

<style scoped>
/* 기존 스타일 유지 */
.attributes-content {
  padding: 20px;
  color: #fff;
  text-align: center;
}

.description-bar {
  position: relative;
  width: 100%;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.description-bar-image {
  width: 100%;
  height: 120px;
}

.description-bar-text {
  position: absolute;
  color: white;
  justify-content: center;
  font-size: 1rem;
  text-align: center;
  white-space: pre-wrap;
  line-height: 1.1;
}

.level-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  background-color: #1d1707;
  width: 30%;
  padding: 10px;
  border-radius: 5px;
  gap: 10px;
  margin: 0 auto 20px;
}

.level-controls {
  display: flex;
  align-items: center;
}

.control-button {
  width: 20px;
  height: 20px;
  cursor: pointer;
  margin: 0 5px;
}

.level-text {
  font-size: 1.5rem;
  color: #fff;
  margin: 0 10px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  justify-items: center;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 85%;
  padding: 10px;
  border-radius: 5px;
  background-color: #1d1707;
  position: relative;
}

.stat-name {
  font-size: 1.2rem;
  color: #fff;
  margin-bottom: 10px;
}

.stat-controls, .stat-weight-controls {
  display: flex;
  align-items: center;
  background-color: #1d1707;
  padding: 5px;
  border-radius: 5px;
  gap: 5px;
}

.stat-value-container, .stat-weight-container {
  margin: 0 5px;
  font-size: 1.2rem;
  min-width: 40px;
  text-align: center;
  background-color: #1d1707;
  padding: 5px;
  border-radius: 5px;
}

.stat-value, .stat-weight {
  color: #fff;
}

.weight-label {
  font-size: 0.8rem;
  color: #fff;
  margin-top: 5px;
  text-align: center;
}
</style>
