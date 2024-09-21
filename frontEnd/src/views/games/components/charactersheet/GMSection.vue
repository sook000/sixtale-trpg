<template>
  <div class="gm-section-container">
    <!-- GM 정보 및 게임 시작 버튼 -->
    <div class="gm-info">
      <div class="gm-name-box">
        <img :src="gmBoxImage" alt="GM Box" class="gm-box-image" />
        <img :src="gmMaster" alt="GM Master" class="gm-master-image" />
        <div class="gm-details">
          <img :src="gmMarkImage" alt="GM Mark" class="gm-mark-image" />
          <p class="gm-name">{{ gm.name }}</p>
          <!-- GM 음성 채팅 버튼 추가 (닉네임 옆에) -->
          <VoiceChatButton :userId="gm.id" />
        </div>
      </div>
      <button
        class="start-game-button"
        :disabled="!isGM" 
        @click="startGame"
      >
        <img :src="startButtonImagePath" alt="Start Game" class="start-button-image" />
        <span class="start-button-text">게임 시작</span>
      </button>
    </div>
    <div class="gm-profile">
      <img :src="gm.profileImage" alt="GM Profile" class="profile-image" />
    </div>
  </div>
</template>

<script setup>
import VoiceChatButton from "../../VoiceChatButton.vue"; // VoiceChatButton 컴포넌트 임포트
import GameLogWebSocketService from "@/store/websocket/gameLog"; // WebSocket 서비스 가져오기
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getRoomInfo } from "@/common/api/RoomsAPI"; // 방 정보 API 가져오기

const route = useRoute();
const router = useRouter();
const roomId = ref(null);

const props = defineProps({
  isGM: {
    type: Boolean,
    required: true,
  },
});

const gm = ref({
  id: null, // GM의 고유 ID
  name: "", // GM의 닉네임
  profileImage: "", // GM 프로필 이미지
});

// 게임 로그 소켓을 캐릭터 시트에서 구독
onMounted(async () => {
  try {
    roomId.value = route.params.roomId;
    const roomInfo = await getRoomInfo(roomId.value);

    // GM 정보 설정
    gm.value.id = roomInfo.gmID;
    gm.value.name = roomInfo.gmNickname;
    gm.value.profileImage =
      roomInfo.gmImageURL || require("@/assets/images/users/default.png");

    // 여기서 isGM을 콘솔에 출력하여 올바르게 설정되었는지 확인
    console.log('isGM:', props.isGM);

    GameLogWebSocketService.connect(roomId.value);
  } catch (error) {
    console.error(
      "Error fetching room info or connecting to WebSocket:",
      error
    );
  }

  GameLogWebSocketService.onMessageReceived((message) => {
    switch (message.gameType) {
      case "GAME_START":
        // 게임 시작 시 페이지 이동
        router.push(`/game/${route.params.roomId}/in-game`);
        break;
      default:
        break;
    }
    console.log(message);
  });
});

const emit = defineEmits(["start-game"]);

const startGame = () => {
  const messageData = {
    gameType: "GAME_START",
    roomID: roomId.value,
  };

  // WebSocket을 통해 게임 시작 메시지 전송
  GameLogWebSocketService.sendMessage(messageData);
  emit("start-game");
};


const gmBoxImage = require('@/assets/images/character_sheet/gm_box.png');
const gmMarkImage = require('@/assets/images/character_sheet/gm_mark.png');
const startButtonImagePath = require('@/assets/images/room/start_button.png');
const gmMaster = require('@/assets/images/character_sheet/Game_Master.png');
</script>


<style scoped>
.gm-section-container {
  display: flex;
  height: 100%;
}

.gm-profile {
  flex: 3;
  display: flex;
  justify-content: center;
  align-items: center;
}

.profile-image {
  width: 80%;
  height: auto;
  border-radius: 10px;
}

.gm-info {
  flex: 3;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.gm-name-box {
  position: relative;
  width: 100%;
  text-align: center;
  margin-bottom: 10px;
}

.gm-box-image {
  width: 100%;
}

.gm-master-image {
  width: 60%;
  position: absolute;
  top: 10%;
  left: 10%;
  z-index: 1;
}

.gm-details {
  position: absolute;
  width: 100%;
  top: 70%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px; /* 닉네임과 음성 버튼 사이의 간격 설정 */
}

.gm-mark-image {
  width: 15%;
  height: 15%;
  margin: 3%;
}

.gm-name {
  font-size: 1rem;
  margin: 0;
  font-family: "Abhaya Libre ExtraBold", sans-serif;
  font-style: normal;
  font-weight: 800;
  color: rgb(214, 205, 170);
}

.start-game-button {
  width: 100%;
  padding: 0;
  background: none;
  border: none;
  cursor: pointer;
  position: relative;
  margin-top: 2%;
}

.start-game-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.start-button-image {
  width: 100%;
  height: auto;
}

.start-button-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 0.8rem;
  font-weight: bold;
}
</style>
