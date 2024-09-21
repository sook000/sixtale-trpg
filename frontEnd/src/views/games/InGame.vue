<template>
  <div class="character-sheet" :style="backgroundStyle">
    <!-- Header 컴포넌트에 isGM을 전달 -->
    <Header class="header" :isGM="isGM" />
    <div class="main-content">
      <div class="left-section">
        <!-- MainContent에 selectedMap을 prop으로 전달 -->
        <MainContent class="main-content-inner" :selectedMap="selectedMap" />
        <VideoProfile class="video-profile" />
      </div>
      <div class="right-section">
        <GMSection class="gm-section" :gm="gm" :isGM="isGM" />
        <div class="log-and-chat">
          <Log class="log-section" />
          <Chatting class="chatting-section" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { getRoomInfo } from "@/common/api/RoomsAPI"; // 방 정보 API
import { getMemberInfo } from "@/common/api/mypageAPI"; // 사용자 정보 API

import { useMapStore } from "@/store/map/mapStore"; // 맵 상태 관리
import Header from "@/views/games/components/ingame/Header.vue";
import MainContent from "@/views/games/components/ingame/MainContent.vue";
import VideoProfile from "@/views/games/components/ingame/VideoProfiles.vue";
import GMSection from "@/views/games/components/ingame/GMSection.vue";
import Chatting from "@/views/games/components/ingame/Chatting.vue";
import Log from "@/views/games/components/ingame/Log.vue";

// 배경 이미지 임포트
import MainBackground from "@/assets/images/maps/background/MainBackground.png";

// Pinia 스토어 인스턴스
const mapStore = useMapStore();

// 선택된 맵을 관리하는 로컬 상태
const selectedMap = ref(mapStore.selectedMap);

// Pinia의 selectedMap이 변경되면 로컬 상태도 업데이트
watch(
  () => mapStore.selectedMap,
  (newMap) => {
    selectedMap.value = newMap;
  }
);

// GM 여부를 저장하는 상태
const isGM = ref(true);
const roomId = ref(null);

// 현재 사용자가 GM인지 확인하는 함수
const fetchRoomDetails = async () => {
  try {
    roomId.value = route.params.roomId;
    const roomInfo = await getRoomInfo(roomId.value);

    const memberInfo = await getMemberInfo();
    const currentUserId = memberInfo.data.data.id;

    if (roomInfo.gmID === currentUserId) {
      isGM.value = true;
    }
  } catch (error) {
    console.error("Error fetching room info or member info:", error);
  }
};

onMounted(() => {
  fetchRoomDetails(); // 컴포넌트가 마운트되면 GM 여부를 확인
});

const gm = ref({
  name: "미카엘",
});

// Computed property for the background style
const backgroundStyle = computed(() => ({
  backgroundImage: `url(${MainBackground})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  backgroundRepeat: "no-repeat",
}));
</script>

<style scoped>
.character-sheet {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 100vh;
}

.header {
  height: 70%;
  width: 100%;
}

.main-content {
  display: flex;
  height: 90%;
  padding: 10px;
  box-sizing: border-box;
}

.left-section {
  display: flex;
  flex-direction: column;
  width: 80%;
  gap: 10px;
  height: 100%;
}

.video-profile {
  height: 25%;
}

.main-content-inner {
  height: 75%;
  display: flex;
  box-sizing: border-box;
}

.right-section {
  width: 20%;
  display: flex;
  flex-direction: column;
  margin-left: 10px;
  padding: 3px;
  justify-content: flex-start;
}

.gm-section {
  height: 6%;
}

.log-and-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.log-section {
  flex: 2; /* 2:3 비율 */
}

.chatting-section {
  flex: 3; /* 2:3 비율 */
}
</style>
