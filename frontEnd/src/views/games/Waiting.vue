<template>
  <div :style="waitingContainerStyle" class="waiting-container">
    <RoomHeader :roomTitle="roomDetails.title" :nextSchedule="nextSchedule" />
    <div class="content">
      <div class="left-section">
        <div class="user-cards">
          <UserCard
            v-for="user in allUsers"
            :key="user.id || user.placeholder"
            :user="user"
            :isGM="isGM"
            :roomId="roomId"
            :memberId="user.memberId"
          />
        </div>
        <Chat class="chat-section" />
      </div>
      <div class="right-section">
        <div :style="topSectionStyle" class="top-section">
          <Map :roomId="roomId" :mapList="mapList" :gmName="gm.name" />
          <div :style="gmCardStyle" class="gm-section">
            <div class="gm-info">
              <div
                :style="profileImageContainerStyle"
                class="profile-image-container"
              >
                <img
                  :src="gm.profileImage || defaultImage"
                  alt="GM 프로필"
                  class="profile-image"
                  @click="showGMModal = true"
                />
                <img
                  :src="avatarFrameImagePath"
                  alt="테두리"
                  class="avatar-frame"
                />
              </div>
              <div :style="gmNameStyle" class="gm-name" :title="gm.name">
                {{ truncatedGMName }}
              </div>
            </div>
            <button
              :disabled="nickName !== gm.name"
              :style="[
                startGameButtonStyle,
                nickName !== gm.name ? disabledButtonStyle : {},
              ]"
              class="start-game-button"
              @click="startGame"
            >
              게임 시작
            </button>
          </div>
        </div>
        <div class="details">
          <div class="rule-scenario">
            <div :style="gameInfoStyle" class="game-info">
              <div class="title">
                <div :style="vectorImage">게임 룰</div>
              </div>
              <div class="content">
                <div
                  :style="gameRuleContainerStyle"
                  class="game-rule-container"
                  @click="openRulebookModal"
                >
                  <div class="game-rule-text">{{ gameRule }}</div>
                </div>
              </div>
            </div>

            <div :style="scenarioInfoStyle" class="game-info">
              <div class="title">
                <div :style="vectorImage">시나리오</div>
              </div>
              <div class="content scenario-content">
                <img :src="scenarioImagePath" class="scenario-image" />
                <div class="scenario-text" @click="openScenarioModal">
                  {{ scenario }}
                </div>
              </div>
            </div>
          </div>
          <Calendar @select="selectDate" :style="calendarContainerStyle" />
        </div>
      </div>
    </div>
    <Userinfo
      v-if="showUserModal"
      :user="selectedUser"
      @close="showUserModal = false"
    />
    <Userinfo v-if="showGMModal" :user="gm" @close="showGMModal = false" />
    <ScenarioModal
      v-if="isScenarioModalOpen"
      :scenario="scenarioDetails"
      @close="closeScenarioModal"
    />
    <RulebookModal :isOpen="isRulebookModalOpen" @close="closeRulebookModal" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useStore } from "vuex";
import RoomHeader from "./components/waiting/RoomHeader.vue";
import UserCard from "./components/waiting/UserCard.vue";
import Chat from "./components/waiting/Chat.vue";
import Map from "./components/waiting/Map.vue";
import Calendar from "./components/Calendar.vue";
import RulebookModal from "./components/Modal/RulebookModal.vue";
import ScenarioModal from "./components/Modal/ScenarioModal.vue";
import Userinfo from "./components/Modal/UserInfo.vue";
import { getRoomInfo, getMapList } from "@/common/api/RoomsAPI";
import defaultImage from "@/assets/images/users/default.png";
import { getMemberInfo } from "@/common/api/mypageAPI"; // 사용자 정보 가져오기 API
import WebSocketService from "@/store/websocket/waiting"; // WebSocket 서비스 가져오기
import GameLogWebSocketService from "@/store/websocket/gameLog"; // GameLogWebSocket 서비스 가져오기

const store = useStore();

const roomDetails = ref({
  title: "",
  currentPlayers: 0,
  totalPlayers: 0,
  status: "",
});

const users = ref([]);
const gm = ref({
  name: "",
  profileImage: "",
});

const isGM = ref(true);
const nextSchedule = ref("");
const gameRule = ref("");
const scenario = ref("");
const scenarioDetails = ref({});
const showScenarioDetails = ref(false);
const roomId = ref(null);

const mapList = ref([]);

const isRulebookModalOpen = ref(false);
const isScenarioModalOpen = ref(false);
const showUserModal = ref(false);
const showGMModal = ref(false);
const selectedUser = ref(null);

const router = useRouter();
const route = useRoute();

const nickName = ref("");
const userId = ref("");

onMounted(async () => {
  await getMemberInfo()
    .then((response) => {
      nickName.value = response.data.data.nickName;
      userId.value = response.data.data.id;
    })
    .catch((error) => {
      console.error("Failed to fetch member info:", error);
    });

  roomId.value = route.params.roomId;
  fetchRoomDetails();
  fetchMapList();

  WebSocketService.disconnect();
  WebSocketService.connect(route.params.roomId, userId.value);

  // "ENTER" 메시지 타입에 대한 콜백 등록
  WebSocketService.onMessageReceived("ENTER", (message) => {
    console.log("Enter message received:", message);
    fetchRoomDetails();
  });

  // 게임 시작
  WebSocketService.onMessageReceived("NEXT", (message) => {
    console.log("Start Sheet message received:", message);

    router.push({
      name: "CharacterSheet",
      params: { roomId: route.params.roomId },
    });
  });

  // // 여기서부터
  // await WebSocketService.connect(route.params.roomId, userId.value);
  // // 서버로부터 메시지를 수신할 때마다 콜백 실행
  // await WebSocketService.onMessageReceived((message) => {
  //   switch (message.type) {
  //     case "ENTER":
  //       console.log(111);
  //       console.log;
  //       fetchRoomDetails();
  //       break;
  //     // case "TALK":
  //     //   console.log("222111")
  //   }
  //   // messages.value.push(message); // 메시지 목록에 추가
  // });
});

onBeforeUnmount(() => {
  // WebSocket 연결 해제
  WebSocketService.disconnect();
});

/*
onMounted(() => {
  getMemberInfo()
    .then((response) => {
      nickName.value = response.data.data.nickName;
      userId.value = response.data.data.id;
    })
    .catch((error) => {
      console.error("Failed to fetch member info:", error);
    });

  roomId.value = route.params.roomId;
  fetchRoomDetails();
  fetchMapList();

  console.log("Hihihihihihihi");
  console.log(userId.value);
  WebSocketService.connect(route.params.roomId, userId.value);
  // 서버로부터 메시지를 수신할 때마다 콜백 실행
  WebSocketService.onMessageReceived((message) => {
    switch (message.type) {
      case "ENTER":
        fetchRoomDetails();
      // case "TALK":
      //   console.log("222111")
    }
    // messages.value.push(message); // 메시지 목록에 추가
  });
});
*/

const fetchRoomDetails = async () => {
  try {
    const roomId = route.params.roomId;
    const roomInfo = await getRoomInfo(roomId);

    roomDetails.value.title = roomInfo.title;
    roomDetails.value.currentPlayers = roomInfo.currentCount;
    roomDetails.value.totalPlayers = roomInfo.maxCount;
    roomDetails.value.status = roomInfo.status;
    nextSchedule.value = roomInfo.nextPlay;
    gameRule.value = roomInfo.ruleTitle;
    scenario.value = roomInfo.scenarioTitle;
    gm.value.name = roomInfo.gmNickname;
    gm.value.profileImage = roomInfo.gmImageURL || defaultImage;
    scenarioImagePath.value = roomInfo.scenarioImageURL;

    users.value = roomInfo.playMemberList.map((member) => ({
      id: member.playMemberID,
      memberId: member.memberID,
      name: member.playMemberNickname,
      profileImage: member.playMemberImageURL || defaultImage,
    }));

    console.log("users", users.value);
  } catch (error) {
    console.error("Error fetching room details:", error);
  }
};

const fetchMapList = async () => {
  try {
    const fetchedMaps = await getMapList(roomId.value);
    if (fetchedMaps.mapList && Array.isArray(fetchedMaps.mapList)) {
      mapList.value = fetchedMaps.mapList;
    } else {
      console.log("No maps found or fetchedMaps.mapList is not an array.");
    }
  } catch (error) {
    console.error("Error fetching map list:", error);
  }
};

const selectDate = (date) => {
  nextSchedule.value = date;
};

const openRulebookModal = () => {
  isRulebookModalOpen.value = true;
};

const closeRulebookModal = () => {
  isRulebookModalOpen.value = false;
};

const openScenarioModal = () => {
  fetchScenarioDetails();
};

const closeScenarioModal = () => {
  isScenarioModalOpen.value = false;
};

const startGame = () => {
  WebSocketService.sendMessage({
    roomID: route.params.roomId,
    memberID: userId.value,
    type: "NEXT",
  });
};

const fetchScenarioDetails = async () => {
  try {
    scenarioDetails.value = {
      title: "왕자와 죽음의 개",
      writer_id: "writer123",
      summary: "이 시나리오는...",
      description: "상세 설명 내용...",
    };
    isScenarioModalOpen.value = true;
  } catch (error) {
    console.error("Error fetching scenario details:", error);
  }
};

const toggleScenarioDetails = () => {
  if (isGM.value) {
    showScenarioDetails.value = !showScenarioDetails.value;
  }
};

// 사용자 카드가 8개 되도록 빈 사용자 카드 추가
const allUsers = computed(() => {
  const placeholders = 8 - users.value.length;
  const placeholderCards = Array.from({ length: placeholders }, (_, index) => ({
    id: `placeholder-${index + 1}`,
    name: "",
    profileImage: "",
  }));
  return [...users.value, ...placeholderCards];
});

// 배경 이미지 경로 설정
const backgroundImagePath = require("@/assets/images/room/main_background.png");
const profileBoxImagePath = require("@/assets/images/room/profile_box.png");
const nameBoxImagePath = require("@/assets/images/room/name_box.png");
const avatarFrameImagePath = require("@/assets/images/room/avatar_frame.png");
const startButtonImagePath = require("@/assets/images/room/start_button.png");
const infoIconPath = require("@/assets/images/room/info.png");
const ruleBoxImagePath = require("@/assets/images/room/rule_box.png");
const ruleBox1ImagePath = require("@/assets/images/room/rule_box1.png");
const scenario_boxPath = require("@/assets/images/room/scenario_box.png");
const vectorImagePath = require("@/assets/images/room/Vector.png");
const scenarioImagePath = ref("");
const calendarBoxImagePath = require("@/assets/images/room/calendar_box.png");

// 배경 이미지 스타일 설정
const waitingContainerStyle = computed(() => ({
  backgroundImage: `url(${backgroundImagePath})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  backgroundRepeat: "no-repeat",
  height: "100vh",
  width: "100vw",
}));

// GM Card 스타일 설정
const gmCardStyle = computed(() => ({
  backgroundImage: `url(${profileBoxImagePath})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  justifyContent: "space-between",
  borderRadius: "10px",
  height: "80%",
  padding: "5%",
  width: "90%",
  overflow: "hidden",
}));

const gmNameStyle = computed(() => ({
  // backgroundImage: `url(${nameBoxImagePath})`,
  backgroundSize: "cover",
  backgroundColor: "#251C15",
  backgroundPosition: "center",
  backgroundRepeat: "no-repeat",
  alignItems: "center",
  padding: "0% 0%",
  borderRadius: "5px",
  color: "#ffffff",
  border: "1px solid #5a4d41",
  marginTop: "8%",
  width: "110%",
  textAlign: "center",
  display: "inline-block",
  height: "15%",
  lineHeight: "1.5",
  overflow: "hidden",
  textOverflow: "ellipsis",
  whiteSpace: "nowrap",
}));

const truncatedGMName = computed(() => {
  return gm.value.name.length > 10
    ? gm.value.name.slice(0, 10) + "..."
    : gm.value.name;
});

const profileImageContainerStyle = {
  position: "relative",
  width: "110%",
  height: "80%",
  overflow: "hidden",
  borderRadius: "50%",
  backgroundColor: "#291707",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
};

const topSectionStyle = {
  display: "flex",
  height: "50%",
};

const startGameButtonStyle = {
  backgroundImage: `url(${startButtonImagePath})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  width: "100%",
  padding: "4%",
  color: "white",
  borderRadius: "5px",
  cursor: "pointer",
  marginTop: "8%",
  border: "none",
  textAlign: "center",
  marginLeft: "6%",
};

const disabledButtonStyle = {
  cursor: "not-allowed",
  opacity: "0.5",
};

const vectorImage = computed(() => ({
  backgroundImage: `url(${vectorImagePath})`,
  width: "40%",
  backgroundPosition: "center",
  backgroundRepeat: "no-repeat",
  fontFamily: "'Abhaya Libre ExtraBold', sans-serif",
  fontStyle: "normal",
  fontWeight: 800,
  paddingLeft: "20px",
  fontSize: "15px",
}));

const gameInfoStyle = computed(() => ({
  // backgroundImage: `url(${ruleBox1ImagePath})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  height: "100%",
  border: "1px solid #4A3A2E",
}));

const scenarioInfoStyle = computed(() => ({
  // backgroundImage: `url(${scenario_boxPath})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  height: "100%",
  border: "1px solid #4A3A2E",
}));

const gameRuleContainerStyle = {
  backgroundImage: `url(${ruleBoxImagePath})`,
  backgroundSize: "contain",
  backgroundRepeat: "no-repeat",
  backgroundPosition: "center",
  borderRadius: "10px",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  width: "80%",
  height: "80%",
  fontFamily: "'Abhaya Libre ExtraBold', sans-serif",
  fontStyle: "normal",
  fontWeight: 800,
  fontSize: "23px",
  lineHeight: "100%",
  color: "rgb(214, 205, 170)",
  background: "rgba(101, 78, 53, 0.49)",
  boxShadow:
    "4px 4px 4px rgba(0, 0, 0, 0.25), inset 4px 4px 4px rgba(255, 255, 255, 0.15)",
  cursor: "pointer",
};

const calendarContainerStyle = {
  // backgroundImage: `url(${calendarBoxImagePath})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  padding: "23px",
  borderRadius: "10px",
  width: "48%",
  height: "100%",
  border: "1px solid #4A3A2E",
};
</script>

<style scoped>
.waiting-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-sizing: border-box;
  min-height: 768px;
}

.content {
  display: flex;
  height: 90%;
}

.left-section {
  width: 49%;
  display: flex;
  flex-direction: column;
  padding: 10px;
  box-sizing: border-box;
}

.user-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 5px;
  height: 55%;
  padding-left: 6px;
}

.chat-section {
  flex: 1;
  margin-top: 20px;
  overflow-y: auto;
}

.right-section {
  width: 50%;
  display: flex;
  flex-direction: column;
  padding-top: 15px;
  box-sizing: border-box;
  overflow: hidden;
}

.top-section {
  display: flex;
  height: 60%;
}

.map-section {
  flex: 6;
  border-radius: 10px;
  height: 100%;
  position: relative;
}

.gm-section {
  flex: 4;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-left: 10px;
  background-color: #291707;
  border-radius: 10px;
  height: 70%;
  width: 100%;
  justify-content: center;
  border: 1px solid #5a4d41;
}

.gm-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  flex: 1;
}

.profile-image-container {
  position: relative;
  width: 100%; /* 너비를 100%로 설정 */
  padding-bottom: 100%; /* 정사각형을 유지 */
  overflow: hidden;
  border-radius: 50%; /* 원형으로 유지 */
  background-color: #291707;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 이미지 비율을 유지하면서 컨테이너를 완전히 채움 */
  border-radius: 50%; /* 이미지를 원형으로 만듦 */
  position: absolute; /* 이미지가 컨테이너 내에서 정렬되도록 함 */
  top: 0;
  left: 0;
}

.avatar-frame {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  border-radius: 50%;
}

.info-icon {
  width: 1.04vw;
  height: 1.04vw;
  cursor: pointer;
  position: absolute;
  top: 12%;
  right: 1%;
  transform: translate(-50%, -50%);
}

.start-game-button.disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.details {
  display: flex;
  justify-content: space-between;
  margin-top: -7%;
  padding-bottom: 20px;
  flex-grow: 1;
}

.rule-scenario {
  display: flex;
  flex-direction: column;
  width: 50%;
  height: 100%;
}

.game-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 10px;
  background-color: #130b02;
  border-radius: 10px;
  margin-bottom: 5px;
  height: 50%;
  background-size: cover;
  background-position: center;
}

.scenario-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
}

.scenario-image {
  width: 60%;
  height: auto;
  margin-right: 10px;
}

.scenario-text {
  color: rgb(214, 205, 170);
  background: rgba(101, 78, 53, 0.49);
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25),
    inset 4px 4px 4px rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  font-size: 13px;
  padding: 10px;
  width: 80%; /* 이미지와 같은 너비로 설정 */
  text-align: center; /* 텍스트 중앙 정렬 */
  white-space: normal; /* 여러 줄 허용 */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  font-weight: 800;
  -webkit-line-clamp: 2; /* 최대 2줄까지 표시 */
  -webkit-box-orient: vertical;
}

.game-info .title {
  border-radius: 10px;
  color: rgb(214, 205, 170);
  padding: 10px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.info-icon-large {
  width: 16px;
  height: 16px;
  cursor: pointer;
  margin-left: 5px;
}

.game-info .content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 10px;
  height: 100%;
}

.game-rule-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 80%;
  height: 80%;
  background: rgba(101, 78, 53, 0.49);
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25),
    inset 4px 4px 4px rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  padding: 10px;
}

.game-rule-text {
  color: rgb(214, 205, 170);
  font-family: "Abhaya Libre ExtraBold", sans-serif;
  font-style: normal;
  font-weight: 800;
  font-size: 20px;
  line-height: 120%;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 최대 2줄까지 표시 */
  -webkit-box-orient: vertical;
}

.calendar {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 23px;
  background-color: #251c15;
  border-radius: 10px;
  width: 48%;
  height: 78%;
}

.map-section .map-controls {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  transform: translateY(-50%);
}

.map-section .map-controls img {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.cursor {
  cursor: pointer;
}

.disabled {
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .content {
    flex-direction: column;
  }

  .left-section,
  .right-section {
    width: 100%;
  }

  .user-cards {
    grid-template-columns: repeat(2, 1fr);
    grid-template-rows: repeat(4, 1fr);
    height: auto;
    border: 1px solid #4a3a2e;
  }

  .chat-section {
    margin-top: 5px;
  }

  .top-section,
  .details {
    height: auto;
  }

  .details {
    flex-direction: column;
  }

  .rule-scenario,
  .calendar {
    width: 100%;
    margin-bottom: 10px;
  }
}

.game-rule-container {
  cursor: pointer;
}

.game-rule-container:hover {
  /* transform: scale(1.05); */
  /* box-shadow: 0 0 15px rgba(255, 255, 255, 0.1);
  background-color: rgba(255, 255, 255, 0.1); */
}

.scenario-content {
  cursor: default; /* 기본 커서로 변경 */
}

/* .game-rule-text, .scenario-text {
  cursor: pointer;
  transition: background-color 0.3s;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
} */

.scenario-text:hover {
  /* background-color: rgba(255, 255, 255, 0.1); */
  cursor: pointer;
}

.scenario-text {
  transform: scale(1); /* 초기 스케일 설정 */
  transition: transform 0.3s, box-shadow 0.3s;
}

/* .scenario-text:hover {
  transform: scale(1.05);
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.3);
} */

.scenario-image {
  pointer-events: none;
}
</style>
