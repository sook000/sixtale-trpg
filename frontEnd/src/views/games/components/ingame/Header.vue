<template>
  <div class="header-container" v-bind="$attrs">
    <div class="icon-container">
      <img src="@/assets/images/ingame/Rulebook.png" alt="Rulebook" class="icon" @click="openRulebookModal" />
      <img v-if="isGM" src="@/assets/images/ingame/Scenario.png" @click="openScenarioModal" class="icon" alt="Scenario" />
      <img v-if="isGM" src="@/assets/images/ingame/Map.png" @click="openMapModal" class="icon" alt="Map" />
      <img v-if="isGM" src="@/assets/images/ingame/Grid.png" @click="toggleGrid" class="icon" alt="Grid" />
      <img v-if="isGM" src="@/assets/images/ingame/MuteAll.png" class="icon" alt="Mute" />
      <img v-if="isGM" src="@/assets/images/ingame/Drawing.png" class="icon" alt="Paint" />
    </div>
    <div class="title-container">
      <h1>{{ scenarioTitle }}</h1>
    </div>
    <div class="header-right">
      <div class="timer-container">
        <p>{{ formattedTimer }}</p>
      </div>
      <template v-if="isGM">
        <div class="close-icon-container">
          <img src="@/assets/images/ingame/Close.png" alt="Close" class="close-icon" @click="openGameEndModal" />
        </div>
      </template>
    </div>
  </div>

  <!-- Modals -->
  <RuleBookModal :isOpen="showRulebookModal" @close="closeRulebookModal" />
  <ScenarioModal v-if="showScenarioModal" @close="closeScenarioModal" />
  <MapModal v-if="showMapModal" @close="closeMapModal" :showGrid="showGrid" />
  <GameEndModal v-if="showGameEndModal" @close="closeGameEndModal" />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getRoomInfo } from '@/common/api/RoomsAPI.js'; // 방 정보 조회 함수 임포트
import RuleBookModal from '@/views/games/components/Modal/RulebookModal.vue'; 
import ScenarioModal from '@/views/games/components/Modal/ScenarioModal.vue';
import MapModal from '@/views/games/components/Modal/MapModal.vue';
import GameEndModal from '@/views/games/components/Modal/GameEndModal.vue';

const props = defineProps({
  isGM: {
    type: Boolean,
    required: true,
  },
});

const scenarioTitle = ref('로딩 중...');
const timer = ref(0); // 타이머를 초 단위로 저장
const showRulebookModal = ref(false);
const showScenarioModal = ref(false);
const showMapModal = ref(false);
const showGameEndModal = ref(false);
const showGrid = ref(true);

const route = useRoute();

const fetchScenarioTitle = async () => {
  try {
    const roomId = route.params.roomId; // 라우트에서 roomId를 가져옴
    const roomInfo = await getRoomInfo(roomId);
    scenarioTitle.value = roomInfo.scenarioTitle; // roomInfo에서 시나리오 제목을 가져옴
  } catch (error) {
    console.error('Failed to fetch scenario title:', error);
    scenarioTitle.value = '시나리오 제목을 불러올 수 없습니다.';
  }
};

// 타이머 시작
const startTimer = () => {
  setInterval(() => {
    timer.value++;
  }, 1000);
};

// 타이머를 HH:MM:SS 형식으로 포맷팅
const formattedTimer = computed(() => {
  const hours = String(Math.floor(timer.value / 3600)).padStart(2, '0');
  const minutes = String(Math.floor((timer.value % 3600) / 60)).padStart(2, '0');
  const seconds = String(timer.value % 60).padStart(2, '0');
  return `${hours}:${minutes}:${seconds}`;
});

onMounted(() => {
  fetchScenarioTitle(); // 컴포넌트가 마운트되면 시나리오 제목을 가져옴
  startTimer(); // 타이머 시작
});

const openRulebookModal = () => {
  showRulebookModal.value = true;
};

const closeRulebookModal = () => {
  showRulebookModal.value = false;
};

const openScenarioModal = () => {
  showScenarioModal.value = true;
};

const closeScenarioModal = () => {
  showScenarioModal.value = false;
};

const openMapModal = () => {
  showMapModal.value = true;
};

const closeMapModal = () => {
  showMapModal.value = false;
};

const openGameEndModal = () => {
  showGameEndModal.value = true;
};

const closeGameEndModal = () => {
  showGameEndModal.value = false;
};

const toggleGrid = () => {
  showGrid.value = !showGrid.value;
  window.dispatchEvent(new CustomEvent('toggle-grid', { detail: showGrid.value }));
};
</script>


<style scoped>
.header {
  background-color: #382A20;
  color: rgb(191, 176, 142);
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 100%;
}
.header-title, .header-center h1 {
  font-family: 'Abhaya Libre', sans-serif;
  font-weight: 800;
  font-style: normal;
}

.icon-container {
  display: flex;
  gap: 10px;
}

.icon {
  width: 30px;
  height: 30px;
  cursor: pointer;
}

.title-container {
  flex-grow: 1;
  text-align: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.timer-container {
  background-color: #4A3B31; /* 타이머 배경색 */
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 24px;
  margin-right: 20px;
}

.timer-container p {
  margin: 0;
}

.close-icon-container {
  background-color: transparent; /* 하얀색 배경 */
  padding: 3px;
  border-radius: 5px;
}

.close-icon {
  width: 30px;
  height: 30px;
  cursor: pointer;
}
</style>
