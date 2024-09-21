<template>
  <div class="lobby-container">
    <div class="rectangle-box">
      <div class="button-container">
        <div class="action-buttons">
          <button class="refresh-button" @click="refreshRooms">
            <img
              src="@/assets/images/lobby/refresh.png"
              alt="Refresh"
              class="refresh-icon"
            />
          </button>
          <button class="create-room-button" @click="openCreateRoomModal">
            방 만들기
          </button>
        </div>
        <div class="pagination">
          <button
            class="pagination-button"
            @click="prevPage"
            :disabled="currentPage === 0"
          >
            <img
              src="@/assets/images/lobby/Arrow_Left.png"
              alt="Previous"
              class="pagination-arrow"
            />
          </button>
          <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
          <button
            class="pagination-button"
            @click="nextPage"
            :disabled="currentPage === totalPages - 1"
          >
            <img
              src="@/assets/images/lobby/Arrow_Right.png"
              alt="Next"
              class="pagination-arrow"
            />
          </button>
        </div>
      </div>
      <!-- <button 
          class="view-all-rooms-button" 
          @click="showAllRooms" 
          :class="{ active: isAllRooms }"
          :disabled="isAllRooms"
        >
          전체 방 보기
        </button>
        <button 
          class="view-joined-rooms-button" 
          @click="filterJoinedRooms" 
          :class="{ active: !isAllRooms }"
          :disabled="!isAllRooms"
        >
          참가중인 방 보기
        </button> -->
      <div class="rooms-container-wrapper">
        <div class="rooms-container" v-if="!isLoading && rooms.length > 0">
          <div
            v-for="room in rooms"
            :key="room.id"
            class="room-card"
            :class="{
              disabled:
                room.status === 'PLAYING' ||
                room.currentCount === room.maxCount,
            }"
            @click="handleEnterRoom(room)"
          >
            <div class="room-image">
              <img :src="room.scenarioImageURL" alt="Room Image" />
            </div>
            <div class="room-details">
              <div class="room-title-container">
                <div class="room-number-box">
                  <span class="room-number">{{ room.id }}</span>
                </div>
                <div class="room-title-box">
                  <span class="room-title">{{ room.title }}</span>
                </div>
              </div>
              <div class="room-info">
                <div class="scenario-id-box">
                  <span class="scenario-id">S#{{ room.scenarioID }}</span>
                </div>
                <div class="scenario-title-box">
                  <span class="scenario-title">{{ room.scenarioTitle }}</span>
                </div>
              </div>
              <div class="room-footer">
                <div class="room-status">
                  <img :src="getStatusImage(room.status)" alt="Room Status" />
                </div>
                <div class="room-icons">
                  <img
                    v-if="room.isLocked"
                    src="@/assets/images/lobby/Key.png"
                    alt="Locked Room"
                    class="room-icon"
                  />
                  <img
                    v-if="room.isVoice"
                    src="@/assets/images/lobby/Voice.png"
                    alt="Voice Chat"
                    class="room-icon"
                  />
                </div>
                <div
                  :class="[
                    'players-box',
                    { 'full-room': room.currentCount === room.maxCount },
                  ]"
                >
                  <span>{{ room.currentCount }} / {{ room.maxCount }}</span>
                </div>
                <div class="room-type">
                  <span>{{ room.isShortStory ? "단편" : "장편" }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="isLoading" class="loading-message">
        방 목록을 불러오는 중...
      </div>
      <div
        v-else-if="rooms.length === 0 && !isLoading"
        class="empty-rooms-message"
      >
        현재 이용 가능한 방이 없습니다.
      </div>
    </div>
    <CreateRoomModal
      v-if="isCreateRoomModalOpen"
      @close="closeCreateRoomModal"
    />
  </div>
  <PasswordModal
    v-if="showPasswordModal"
    :show="showPasswordModal"
    :roomId="selectedRoomId"
    @close="closePasswordModal"
    @submit="handlePasswordSubmit"
  />
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import {
  getRoomList,
  getRoomInfo,
  enterRoom,
  getJoinedRooms,
} from "@/common/api/RoomsAPI";
import { getMemberInfo } from "@/common/api/mypageAPI";
import CreateRoomModal from "@/views/menu/components/CreateRoomModal.vue";
import WebSocketService from "@/store/websocket/waiting"; // WebSocket 서비스 가져오기
import PasswordModal from "@/views/menu/components/PasswordModal.vue";

const router = useRouter();

const rooms = ref([]);
const isAllRooms = ref(true);
const isCreateRoomModalOpen = ref(false);
const defaultImage = require("@/assets/images/scenario.png");
const defaultStatusImage = require("@/assets/images/lobby/WAITING.png");
const isLoading = ref(true);

const currentPage = ref(0);
const roomsPerPage = 6;
const totalElements = ref(0);
const totalPages = ref(0);

const user = ref({});
const userId = ref(null);
const showPasswordModal = ref(false);
const selectedRoomId = ref(null);

const joinedRoomIds = ref([]);

const filterJoinedRooms = async () => {
  if (!userId.value) {
    console.error("User ID is not available");
    alert("사용자 정보를 가져올 수 없습니다. 다시 로그인해 주세요.");
    return;
  }

  isAllRooms.value = false; // 참가중인 방 보기 모드로 전환
  currentPage.value = 0;

  try {
    // 사용자 ID로 참가 중인 방 목록 가져오기
    const joinedRoomsResponse = await getJoinedRooms(userId.value);
    joinedRoomIds.value = joinedRoomsResponse.data.map((room) => room.id);
    console.log("Joined Room IDs:", joinedRoomIds.value);

    // 방 목록 갱신
    await fetchRooms();
  } catch (error) {
    console.error("Error fetching joined rooms:", error);
    alert("참가중인 방 목록을 가져오는데 실패했습니다.");
  }
};

const fetchUserInfo = async () => {
  try {
    const response = await getMemberInfo();
    if (response.data && response.data.data) {
      userId.value = response.data.data.id;
      user.value = response.data.data; // user 객체도 설정
      console.log("User ID:", userId.value);
    } else {
      console.error("User data not found in the response");
    }
  } catch (error) {
    console.error("Error fetching user info:", error);
  }
};

const refreshRooms = () => {
  rooms.value = []; // 방 목록을 비우기
  isLoading.value = true; // 로딩 상태를 true로 설정
  setTimeout(fetchRooms, 500); // 0.5초 후 방 목록을 다시 불러옵니다.
};

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++;
    console.log("Next page:", currentPage.value);
    fetchRooms();
  } else {
    console.log("Already on the last page");
  }
};

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--;
    console.log("Previous page:", currentPage.value);
    fetchRooms();
  } else {
    console.log("Already on the first page");
  }
};

const openCreateRoomModal = () => {
  isCreateRoomModalOpen.value = true;
};

const closeCreateRoomModal = () => {
  isCreateRoomModalOpen.value = false;
};

const showAllRooms = () => {
  isAllRooms.value = true;
  console.log("Showing all rooms, isAllRooms:", isAllRooms.value);
  currentPage.value = 0;
  fetchRooms();
};

const getStatusImage = (status) => {
  switch (status) {
    case "WAITING":
      return require("@/assets/images/lobby/WAITING.png");
    case "PLAYING":
      return require("@/assets/images/lobby/PLAYING.png");
    case "UPCOMING":
      return require("@/assets/images/lobby/UPCOMING.png");
    default:
      return defaultStatusImage;
  }
};

const handleEnterRoom = async (room) => {
  try {
    // 방 정보 가져오기
    const roomInfo = await getRoomInfo(room.id);
    console.log("Room info:", roomInfo);

    // 현재 사용자가 해당 방의 GM인지 확인
    if (roomInfo.gmNickname === user.value.nickName) {
      const confirmMessage =
        "현재 방의 GM으로 참여 중입니다. GM으로 다시 입장하시겠습니까?";
      console.log(room.id + "%%%%%%%%%%" + userId.value);

      if (confirm(confirmMessage)) {
        console.log(room.id + "$$$$$$$$$$$" + userId.value);

        // enter 메세지 보내기
        const messageData = {
          type: "ENTER", // 메시지 유형
          roomID: room.id, // 가져온 방 정보에서 roomID 사용
          memberID: userId.value, // 사용자 ID, 실제 값으로 설정
          content: "",
        };

        console.log(room.id + "#########" + userId.value);

        WebSocketService.sendMessage(messageData); // 서버로 메시지 전송

        router.push({
          name: "Waiting",
          params: { roomId: room.id.toString() },
        });
      }
      return;
    }

    if (room.status === "PLAYING" || room.currentCount === room.maxCount) {
      alert("이미 게임이 시작되었거나 방이 가득 찼습니다.");
      return;
    }

    selectedRoomId.value = room.id;

    if (room.isLocked) {
      showPasswordModal.value = true;
    } else {
      const result = await enterRoomWithCheck(room.id);
      if (result) {
         WebSocketService.connect(room.id, userId.value);

        const messageData = {
          type: "ENTER", // 메시지 유형
          roomID: room.id, // 가져온 방 정보에서 roomID 사용
          memberID: userId.value, // 사용자 ID, 실제 값으로 설정
          content: "",
        };

        console.log(room.id + "#########" + userId.value);

        WebSocketService.sendMessage(messageData); // 서버로 메시지 전송

        router.push({
          name: "Waiting",
          params: { roomId: room.id.toString() },
        });
      }
    }
  } catch (error) {
    console.error("Error entering room:", error);
    alert("방에 입장할 수 없습니다.");
  }
};

const handlePasswordSubmit = async ({ roomId, password }) => {
  try {
    const result = await enterRoom(roomId, password);
    if (result.statusCode === 201) {
      // 성공적으로 입장
      router.push({ name: "Waiting", params: { roomId: roomId.toString() } });
    } else {
      alert(result.responseMessage || "방 입장에 실패했습니다.");
    }
  } catch (error) {
    console.error("Failed to enter room:", error);
    let errorMessage = "방 입장에 실패했습니다.";
    if (
      error.response &&
      error.response.data &&
      error.response.data.responseMessage
    ) {
      errorMessage = error.response.data.responseMessage;
    }
    alert(errorMessage);
  }
};

// const handlePasswordSubmit = async ({ roomId, password }) => {
//   try {
//     console.log('Submitting password for room:', roomId);
//     const result = await enterRoomWithCheck(roomId, password);
//     if (result) {
//       closePasswordModal();
//       router.push({ name: 'Waiting', params: { roomId: roomId.toString() } });
//     }
//   } catch (error) {
//     console.error('Failed to enter room:', error);
//     alert(error.message || '방 입장에 실패했습니다. 비밀번호를 확인해 주세요.');
//   }
// };

const enterRoomWithCheck = async (roomId, password = null) => {
  try {
    const roomInfo = await getRoomInfo(roomId);
    console.log("Room info:", roomInfo);

    if (roomInfo.currentCount >= roomInfo.maxCount) {
      throw new Error("방이 가득 찼습니다.");
    }

    if (!user.value.id) {
      throw new Error("사용자 정보를 가져올 수 없습니다.");
    }

    const response = await enterRoom(roomId, user.value.id, password);
    console.log("Enter room response:", response);

    if (response.statusCode === 201) {
      console.log("Successfully entered the room");
      return true;
    } else {
      console.error("Unexpected response:", response);
      throw new Error(response.responseMessage || "방 입장에 실패했습니다.");
    }
  } catch (error) {
    console.error("Error entering room:", error);
    if (error.response && error.response.data) {
      console.error("Server error details:", error.response.data);
    }
    throw error;
  }
};

const closePasswordModal = () => {
  showPasswordModal.value = false;
  selectedRoomId.value = null;
};

const fetchRooms = async () => {
  isLoading.value = true;

  try {
    let response = await getRoomList("", 0, 1000, "");

    if (response && response.data) {
      let allRooms = Array.isArray(response.data)
        ? response.data
        : response.data.content && Array.isArray(response.data.content)
        ? response.data.content
        : [];

      console.log("Total rooms fetched:", allRooms.length);

      // 참가중인 방 필터링
      if (!isAllRooms.value) {
        allRooms = allRooms.filter((room) =>
          joinedRoomIds.value.includes(room.id)
        );
      }

      // 방 정렬: 생성 날짜 최신순으로 정렬
      allRooms.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

      // 페이지네이션 계산
      totalElements.value = allRooms.length;
      totalPages.value = Math.ceil(totalElements.value / roomsPerPage);

      // 현재 페이지에 맞는 방 목록 슬라이스
      const start = currentPage.value * roomsPerPage;
      rooms.value = allRooms.slice(start, start + roomsPerPage).map((room) => ({
        ...room,
        scenarioImageURL: room.scenarioImageURL || defaultImage,
      }));

      console.log("Displayed rooms:", rooms.value);
    } else {
      console.error("Invalid response structure:", response);
      rooms.value = [];
    }
  } catch (error) {
    console.error("Error fetching rooms:", error);
    rooms.value = [];
  } finally {
    isLoading.value = false;
  }
};

const fetchRoomDetails = async (roomId) => {
  try {
    const roomInfo = await getRoomInfo(roomId);
    console.log(`Room Info for room ${roomId}:`, roomInfo);

    if (roomInfo.scenarioID) {
      console.log(`Scenario Info for room ${roomId}:`, {
        scenarioID: roomInfo.scenarioID,
        scenarioTitle: roomInfo.scenarioTitle,
        scenarioImageURL: roomInfo.scenarioImageURL,
      });
    } else {
      console.log(`No scenario information available for room ${roomId}`);
    }
  } catch (error) {
    console.error(`Error fetching details for room ${roomId}:`, error);
  }
};

// const filterJoinedRooms = () => {
//   const userId = '사용자ID'; // 실제 사용자 ID로 교체
//   fetchRooms(true, userId);
// };

const fetchSingleRoomInfo = async (roomId) => {
  try {
    console.log(`Fetching info for room ${roomId}`);
    const roomInfo = await getRoomInfo(roomId);
    console.log("Single room info:", roomInfo);

    // 방 정보의 각 필드를 개별적으로 로깅
    if (roomInfo) {
      console.log("Room ID:", roomInfo.id);
      console.log("Title:", roomInfo.title);
      console.log("Description:", roomInfo.description);
      console.log("Scenario ID:", roomInfo.scenarioID);
      console.log("Scenario Title:", roomInfo.scenarioTitle);
      console.log("Scenario Image URL:", roomInfo.scenarioImageURL);
      console.log("Rule ID:", roomInfo.ruleID);
      console.log("Rule Title:", roomInfo.ruleTitle);
      console.log("GM ID:", roomInfo.gmID);
      console.log("GM Nickname:", roomInfo.gmNickname);
      console.log("Current Count:", roomInfo.currentCount);
      console.log("Max Count:", roomInfo.maxCount);
      console.log("Is Locked:", roomInfo.isLocked);
      console.log("Is Short Story:", roomInfo.isShortStory);
      console.log("Is Voice:", roomInfo.isVoice);
      console.log("Status:", roomInfo.status);
      console.log("Next Play:", roomInfo.nextPlay);
      console.log("Play Time:", roomInfo.playTime);
      console.log("Play Members:");
      roomInfo.playMemberList.forEach((member, index) => {
        console.log(`  Member ${index + 1}:`, member);
      });
    }
  } catch (error) {
    console.error("Error fetching single room info:", error);
  }
};

// onMounted(async () => {
//   await fetchUserInfo();
//   fetchRooms();
//   fetchSingleRoomInfo(1);
//   showAllRooms();
// });
onMounted(async () => {
  await fetchUserInfo();
  await fetchRooms();
  showAllRooms();
  // getMemberInfo()
  //       .then((response) => {
  //         memberID.value = response.data.data.id;
  //         console.log(memberID.value);
  //       })
  //       .catch((error) => {
  //         console.error("Failed to fetch member info:", error);
  //       });
});

onBeforeUnmount(() => {
  // WebSocket 연결 해제
  WebSocketService.disconnect();
});
</script>

<style scoped>
.lobby-container {
  padding: 100px 40px 40px; /* 상단 여백 증가 */
  background: url("~@/assets/images/lobby/Background.png");
  background-size: cover;
  background-position: center;
  color: white;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  min-height: 100vh;
}

.rectangle-box {
  width: 100%;
  max-width: 1471px;
  min-height: 900px;
  background: rgba(91, 78, 71, 0.15);
  box-shadow: inset -4px -4px 4px rgba(0, 0, 0, 0.25),
    inset 4px 4px 4px rgba(0, 0, 0, 0.25);
  border-radius: 39px;
  display: flex;
  flex-direction: column;
  padding: 20px;
  z-index: 1;
}

.button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0 50px;
  margin-bottom: 20px;
}

.rooms-container-wrapper {
  position: relative;
  flex-grow: 1;
  min-height: 600px;
  padding: 0 20px; /* 좌우 패딩 추가 */
}

.rooms-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  width: 100%;
  justify-content: center;
  overflow: visible;
  z-index: 1;
}

.room-card {
  display: flex;
  flex-direction: row; /* 가로 배치 */
  width: calc(50% - 40px);
  background-color: rgba(85, 75, 69, 0.7); /* 방 색깔 및 투명도 설정 */
  border-radius: 39px;
  padding: 20px;
  margin-bottom: 20px;
  position: relative;
  transition: transform 0.3s;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25),
    inset 4px 4px 4px rgba(255, 255, 255, 0.15); /* 드롭 섀도우 및 내부 그림자 설정 */
  cursor: pointer; /* 커서가 포인터로 변경 */

  z-index: 10;
}

.room-card:hover {
  transform: scale(1.05);
  z-index: 11;
}

.room-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.room-card.disabled:hover {
  transform: none;
}

.room-image {
  width: 30%; /* 이미지 크기 조정 */
  padding-right: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.room-image img {
  width: 100%;
  height: auto;
  border-radius: 10px;
}

.room-details {
  width: 70%; /* 이미지 크기 조정에 따른 width 조정 */
  padding: 0 10px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.room-title-container {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(40, 34, 30, 0.31);
  box-shadow: inset 4px 4px 4px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  padding: 5px 10px;
  width: 100%;
  height: 46px;
}

.room-number-box {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.room-number {
  font-family: "Abhaya Libre ExtraBold", sans-serif;
  font-style: normal;
  font-weight: 800;
  font-size: 28px;
  line-height: 100%;
  color: rgb(214, 205, 170);
}

.room-title-box {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.room-title {
  font-family: "Abhaya Libre Medium";
  font-style: normal;
  font-weight: 500;
  font-size: 24px;
  line-height: 100%;
  text-align: center;
  color: rgba(255, 255, 255, 0.67);
}

.room-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.scenario-title-box {
  background: rgba(40, 34, 30, 0.31);
  box-shadow: inset 4px 4px 4px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  padding: 5px 10px;
  display: flex;
  align-items: center;
}

.scenario-id-box {
  background: #554b45;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25),
    inset 4px 4px 4px rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  height: 35px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 15%;
  text-align: center;
  font-family: "Abhaya Libre ExtraBold";
  font-style: normal;
  font-weight: 800;
  font-size: 28px;
  line-height: 100%;
  color: rgba(255, 255, 255, 0.72);
  margin-left: 3%;
}

.scenario-title-box {
  width: 85%;
}

.scenario-id {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.67);
}

.scenario-title {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.67);
}

.room-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.room-status {
  font-size: 1.5rem;
  font-family: "Abhaya Libre ExtraBold";
  color: rgba(212, 222, 213, 1);
}

.status-waiting {
  color: #d4ded5;
  text-shadow: -3px -3px 0 rgba(36, 46, 37, 0.4);
  box-shadow: inset -10px -10px 10px rgba(0, 0, 0, 0.83);
}

.status-playing {
  color: #dedad4;
  text-shadow: -3px -3px 0 rgba(28, 37, 62, 0.86);
  box-shadow: inset -10px -10px 10px rgba(0, 0, 0, 0.83);
}

.room-icons {
  display: flex;
  gap: 10px;
}

.room-icon {
  width: 20px;
  height: 20px;
}

.players-box {
  background: rgba(40, 34, 30, 0.31);
  box-shadow: inset 4px 4px 4px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  padding: 5px 10px;
  color: white;
}

.players-box.full-room {
  background: rgba(22, 17, 12, 0.5); /* 더 어둡게 표시 */
  color: rgba(255, 255, 255, 0.72);
}

.room-type {
  background: #554b45;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25),
    inset 4px 4px 4px rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  padding: 5px 10px;
  color: white;
  font-size: 0.9rem;
  height: 100%;
  display: flex;
  align-items: center;
}
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  width: 100%;
  padding: 0 10%;
}
/* .pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  width: 100%;
  gap: 20px;
  position: relative;
} */

.action-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination {
  display: flex;
  /* justify-content: center; */
  align-items: center;
  gap: 10px;
}

.pagination span {
  font-family: "Abhaya Libre ExtraBold", sans-serif;
  font-style: normal;
  font-weight: 800;
  font-size: 20px;
  line-height: 100%;
  color: rgb(214, 205, 170);
}

.pagination-button,
.refresh-button,
.create-room-button {
  height: 50px;
  background: rgba(101, 78, 53, 0.49);
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25),
    inset 4px 4px 4px rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  color: rgb(214, 205, 170);
  border: none;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}

.pagination-button:active {
  background: rgba(150, 150, 150, 0.49); /* 눌렸을 때 배경색 변경 */
  box-shadow: none; /* 눌렸을 때 그림자 제거 */
  transform: scale(0.95); /* 눌렸을 때 약간 작아지는 효과 */
}
/* .create-room-button {
  width: 200px;
  height: 50px;
  position: absolute;
  right: 10%;
  background-color: rgba(101, 78, 53, 0.49);
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25), inset 4px 4px 4px rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  color: rgb(214, 205, 170);
  padding: 10px 20px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
} */

.create-room-button {
  width: 120px;
  padding: 0 15px;
  font-family: "Abhaya Libre ExtraBold", sans-serif;
  font-style: normal;
  font-weight: 800;
}

.create-room-button:active {
  background: rgba(150, 150, 150, 0.49); /* 눌렸을 때 배경색 변경 */
  box-shadow: none; /* 눌렸을 때 그림자 제거 */
  transform: scale(0.95); /* 눌렸을 때 약간 작아지는 효과 */
}

/* .pagination-button {
  width: 126px;
  height: 50px;
  background: rgba(101, 78, 53, 0.49);
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25), inset 4px 4px 4px rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  border: none;
} */

.pagination-button {
  width: 50px;
}

.pagination-button:disabled {
  background-color: #555;
  cursor: not-allowed;
}

/* .pagination-arrow {
  width: auto; 
  height: auto; 
  max-width: 30px; 
  max-height: 19px; 
} */

/* .button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0 20px;
  margin-bottom: 20px;
} */

.view-all-rooms-button,
.view-joined-rooms-button {
  background: rgba(101, 78, 53, 0.49);
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25),
    inset 4px 4px 4px rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  color: rgb(214, 205, 170);
  padding: 10px 20px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2;
}

.loading-message,
.empty-rooms-message {
  width: 100%;
  text-align: center;
  padding: 20px;
  font-size: 1.2em;
  color: rgba(255, 255, 255, 0.7);
  position: absolute; /* 추가 */
  top: 50%; /* 추가 */
  left: 50%; /* 추가 */
  transform: translate(-50%, -50%); /* 추가 */
}

.view-all-rooms-button.active,
.view-joined-rooms-button.active {
  background: rgba(150, 150, 150, 0.49);
  color: rgba(144, 142, 136, 0.847);
  box-shadow: none; /* 그림자 효과 제거 */
}

.view-all-rooms-button:disabled,
.view-joined-rooms-button:disabled {
  background: rgba(150, 150, 150, 0.3); /* 비활성화 상태의 배경색 */
  cursor: not-allowed; /* 커서 모양을 금지 표시로 변경 */
  box-shadow: none; /* 그림자 효과 제거 */
}

/* .refresh-button {
  background: rgba(101, 78, 53, 0.49);
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25), inset 4px 4px 4px rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  color: rgb(214, 205, 170);
  padding: 10px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
} */
.empty-rooms-message {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.refresh-button {
  width: 50px;
  padding: 0;
}

.refresh-button:active {
  background: rgba(150, 150, 150, 0.49); /* 눌렸을 때 배경색 변경 */
  box-shadow: none; /* 눌렸을 때 그림자 제거 */
  transform: scale(0.95); /* 눌렸을 때 약간 작아지는 효과 */
}

/* .refresh-icon {
  width: 20px; 
  height: 20px;
} */

.pagination-arrow,
.refresh-icon {
  width: auto;
  height: auto;
  max-width: 20px;
  max-height: 20px;
}

@media (max-width: 1200px) {
  .room-card {
    width: calc(100% - 40px); /* 한 줄에 하나의 카드만 표시 */
  }
}

@media (max-width: 768px) {
  .lobby-container {
    padding: 80px 20px 20px;
  }

  .button-container {
    flex-direction: column;
    align-items: stretch;
  }

  .action-buttons {
    justify-content: space-between;
    margin-bottom: 10px;
  }

  .rooms-container-wrapper {
    padding: 0 10px;
  }

  .pagination {
    justify-content: center;
  }

  .action-buttons {
    flex-direction: column;
    gap: 10px;
  }
}

.rectangle-box {
  min-height: calc(100vh - 120px);
}
</style>
