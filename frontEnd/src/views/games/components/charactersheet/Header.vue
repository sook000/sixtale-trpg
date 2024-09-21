<template>
  <div class="header">
    <div class="header-left">
      <template v-if="isGM">
        <img src="@/assets/images/ingame/Rulebook.png" alt="Rulebook" @click="openRulebookModal" />
        <img src="@/assets/images/ingame/Scenario.png" alt="Scenario" @click="openScenarioModal" />
        <img src="@/assets/images/ingame/Map.png" alt="Map" @click="openMapModal" />
        <!-- <img 
          src="@/assets/images/ingame/MuteAll.png" 
          alt="Mute All" 
          @click="toggleMuteAll" 
          :class="{ active: isMuteAllActive }" 
        /> -->
      </template>
      <template v-else>
        <h1 class="header-title">{{ scenarioTitle }}</h1>
      </template>
    </div>
    <div class="header-center">
      <h1>{{ scenarioTitle }}</h1>
    </div>
    <div class="header-right" v-if="isGM">
      <div class="close-icon-container">
        <img src="@/assets/images/ingame/Close.png" alt="Close" @click="openCloseModal" />
      </div>
    </div>

    <RuleBookModal :isOpen="showRulebookModal" @close="showRulebookModal = false" />
    <ScenarioModal v-if="showScenarioModal" @close="showScenarioModal = false" />
    <MapModal v-if="showMapModal" @close="showMapModal = false" />
    <CloseRoomModal v-if="showCloseModal" @confirm="closeRoom" @close="showCloseModal = false" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getRoomInfo } from '@/common/api/RoomsAPI.js';
import { defineProps } from 'vue';

import RuleBookModal from '@/views/games/components/Modal/RulebookModal.vue'; 
import ScenarioModal from '@/views/games/components/Modal/ScenarioModal.vue';
import CloseRoomModal from '@/views/games/components/Modal/CloseRoomModal.vue';
import MapModal from '@/views/games/components/Modal/MapModal.vue';

const props = defineProps({
  isGM: {
    type: Boolean,
    default: true
  },
});

const scenarioTitle = ref('Loading...'); // 시나리오 제목

const showRulebookModal = ref(false);
const showScenarioModal = ref(false);
const showCloseModal = ref(false);
const showMapModal = ref(false);

const isMuteAllActive = ref(false);

const router = useRouter();
const route = useRoute();

const fetchScenarioTitle = async () => {
  try {
    const roomId = route.params.roomId; // 라우트에서 roomId를 가져옴
    const roomInfo = await getRoomInfo(roomId); // 방 정보 조회
    scenarioTitle.value = roomInfo.scenarioTitle; // 시나리오 제목 설정
  } catch (error) {
    console.error('Failed to fetch scenario title:', error);
    scenarioTitle.value = 'Unable to load scenario title';
  }
};

const openRulebookModal = () => {
  console.log('Opening Rulebook Modal');
  showRulebookModal.value = true;
};

const openScenarioModal = () => {
  showScenarioModal.value = true;
};

const openMapModal = () => {
  showMapModal.value = true;
};

const toggleMuteAll = () => {
  if (!props.isGM) return;
  isMuteAllActive.value = !isMuteAllActive.value;
  if (isMuteAllActive.value) {
    muteAll();
  } else {
    unmuteAll();
  }
};

const muteAll = () => {
  console.log('Muting all users');
  // 여기에 모든 사용자의 음소거 로직 추가
};

const unmuteAll = () => {
  console.log('Unmuting all users');
  // 여기에 모든 사용자의 음소거 해제 로직 추가
};

const openCloseModal = () => {
  if (!props.isGM) return;
  showCloseModal.value = true;
};

const closeRoom = () => {
  console.log('Closing Room');
  router.push('/lobby');
};

onMounted(() => {
  fetchScenarioTitle(); // 컴포넌트가 마운트되면 시나리오 제목을 가져옴
});
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

.header-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.header-left img {
  width: 32px; 
  height: 32px; 
  margin: 0 10px;
  cursor: pointer;
}

.header-center {
  flex: 2;
  text-align: center;
}

.header-center h1 {
  margin-right: 40%;
  font-size: 2rem;
}

.header-right {
  display: flex;
  align-items: center;
}

.header-right img {
  width: 32px; 
  height: 32px; 
  margin: 0 10px;
  cursor: pointer;
}

/* .close-icon-container {
  background-color: #251C15;
  padding: 10px;
  border-radius: 100%;
} */

.active {
  filter: brightness(1.5);
}

.disabled {
  opacity: 0.5;
  pointer-events: none;
}
</style>
