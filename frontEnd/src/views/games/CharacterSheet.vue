<template>
  <div class="character-sheet" :style="backgroundStyle">
    <!-- Header 컴포넌트에 isGM을 props로 전달 -->
    <Header class="header" :isGM="isGM" />
    <div class="main-content">
      <div class="left-section">
        <JobBoard 
          class="job-board" 
          :created-jobs="createdCharacterSheetJobs"
          @job-selected="handleJobSelection"
        />
        <VideoProfile class="video-profile" />
      </div>
      <div class="right-section">
        <GMSection 
          class="gm-section" 
          :gm="gm" 
          :isGM="isGM" 
          @start-game="startGame" 
        />
        <Chatting class="chatting" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch, onBeforeUnmount } from "vue";
import { useRouter, useRoute } from "vue-router";
import Header from "@/views/games/components/charactersheet/Header.vue";
import JobCard from "@/views/games/components/charactersheet/JobCard.vue";
import VideoProfile from "@/views/games/components/charactersheet/VideoProfile.vue";
import GMSection from "@/views/games/components/charactersheet/GMSection.vue";
import Chatting from "@/views/games/components/charactersheet/Chatting.vue";
import JobBoard from "./components/charactersheet/JobBoard.vue";
import GameLogWebSocketService from "@/store/websocket/gameLog";
import { getRoomInfo } from '@/common/api/RoomsAPI';
import { getMemberInfo } from '@/common/api/mypageAPI';

const router = useRouter();
const route = useRoute();
const roomId = ref(null);

const gm = ref({
  name: "GM닉네임입니다",
  profileImage: require("@/assets/images/users/gm.png"),
});

const createdCharacterSheetJobs = ref([]);
const isGM = ref(false);
const players = ref([]);
const canStartGame = ref(false);

const handleJobSelection = (jobId) => {
  if (!createdCharacterSheetJobs.value.includes(jobId)) {
    createdCharacterSheetJobs.value.push(jobId);
  }
};

const fetchRoomDetails = async () => {
  try {
    roomId.value = route.params.roomId;
    const roomInfo = await getRoomInfo(roomId.value);

    gm.value.name = roomInfo.gmNickname;
    gm.value.profileImage = roomInfo.gmImageURL || require('@/assets/images/users/default.png');

    const memberInfo = await getMemberInfo();

    const currentUserId = memberInfo.data.data.id;

    if (roomInfo.gmID === currentUserId) {
      isGM.value = true;
    }
  } catch (error) {
    console.error('Error fetching room info or member info:', error);
  }
};

onMounted(() => {
  fetchRoomDetails();
  GameLogWebSocketService.connect(route.params.roomId);

  GameLogWebSocketService.onMessageReceived("GAME_START", (message) => {
    router.push(`/game/${route.params.roomId}/in-game`);
  });
});

watch(players, (newPlayers) => {
  canStartGame.value = newPlayers.every((player) => player.jobSelected);
});

onBeforeUnmount(() => {
  GameLogWebSocketService.disconnect();
});

const startGame = () => {
  GameLogWebSocketService.sendMessage({
    roomID: route.params.roomId,
    gameType: "GAME_START",
  });
  if (isGM.value) {
    router.push(`/game/${route.params.roomId}/in-game`);
  }
};

const backgroundImage = require("@/assets/images/character_sheet/MainBackground.png");
const backgroundStyle = ref({
  backgroundImage: `url(${backgroundImage})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
});

console.log(isGM);
</script>





<style scoped>
.character-sheet {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

.header {
  height: 10vh;
  width: 100%;
}

.main-content {
  display: flex;
  height: 90vh;
  width: 100%;
}

.left-section {
  display: flex;
  flex-direction: column;
  width: 80%;
  height: 100%;
  padding: 1vh;
  box-sizing: border-box;
}

.right-section {
  display: flex;
  flex-direction: column;
  width: 20%;
  height: 100%;
  padding: 1vh;
  box-sizing: border-box;
}

.job-board {
  height: 73%;
  margin-bottom: 1vh;
}

.video-profile {
  height: 27%;
}

.gm-section {
  height: 25%;
  margin-bottom: 1vh;
}

.chatting {
  height: 75%;
}
</style>
