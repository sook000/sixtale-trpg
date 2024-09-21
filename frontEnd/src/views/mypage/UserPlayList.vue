<template>
  <div><h3 class="menu-title">플레이 기록</h3></div>
  <div class="content-box right-box">
    <!-- 버튼과 본문 감싸는 div -->
    <div class="pagination-wrapper">
      <!-- 왼쪽버튼 -->
      <button
        class="pagination-button h-100"
        @click="prevPage"
        :disabled="currentPage === 1"
      >
        <img
          src="@/assets/images/mypage/play_list/left-arrow.png"
          alt="Previous"
          class="pagination-arrow"
        />
      </button>
      <!-- 플레이 카드 -->
      <div class="play-item-container">
        <div
          class="play-item"
          v-for="(room, index) in paginatedRooms"
          :key="index"
        >
          <div class="item-header">
            <div>
              <p class="play-item-title">
                {{ formatRoomId(room.id)
                }}{{
                  room.title.length > 15
                    ? room.title.substring(0, 15) + "..."
                    : room.title
                }}
              </p>
            </div>
            <div
              :class="[
                'players-box',
                { 'full-room': room.current_count === room.max_count },
              ]"
            >
              <span>{{ room.currentCount }} / {{ room.maxCount }}</span>
            </div>
          </div>
          <img
            :src="room.scenarioImageURL || placeholderImage"
            alt="플레이 이미지"
            class="play-item-image"
          />

          <div class="item-main">
            <div class="play-item-details">
              S#
              {{
                room.scenarioTitle.length > 7
                  ? room.scenarioTitle.substring(0, 7) + "..."
                  : room.scenarioTitle
              }}
            </div>
            <div class="icon-detail">
              <div class="room-icons">
                <img
                  :src="getStatusImage(room.isShortStory)"
                  alt="Room Status"
                />
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
            </div>
          </div>

          <div class="play-item-actions">
            <button class="log-button" @click="openLogModal(room)">
              로그보기
            </button>
            <button
              v-if="room.status == 'UPCOMING'"
              class="play-button"
              @click="enterRoom(room.id)"
            >
              이어하기
            </button>
            <div v-else class="spacer"></div>
          </div>
        </div>
      </div>
      <!-- 오른쪽 버튼 -->
      <button
        class="pagination-button"
        @click="nextPage"
        :disabled="currentPage === totalPages"
      >
        <img
          src="@/assets/images/mypage/play_list/right-arrow.png"
          alt="Next"
          class="pagination-arrow"
        />
      </button>
    </div>
    <LogModal
      v-if="isLogModalOpen"
      @close="closeLogModal"
      :room="selectedRoom"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { fetchRooms } from "@/common/api/mypagePlayListApi";
import LogModal from "@/views/mypage/components/LogModal.vue";

const router = useRouter();
const rooms = ref([]);
const placeholderImage = require("@/assets/images/placeholder.png"); //기본 이미지 설정

// 페이징 처리
const currentPage = ref(1);
const roomsPerPage = 6;
const totalPages = ref(0);

// 방 목록 가져오기
const loadRooms = async () => {
  try {
    const response = await fetchRooms(currentPage.value - 1, roomsPerPage);
    rooms.value = response.data.content;
    totalPages.value = response.data.totalPages;
    console.log(response.data);
  } catch (error) {
    console.error("Error loading rooms: ", error);
  }
};

onMounted(loadRooms);

const paginatedRooms = computed(() => rooms.value);

const nextPage = async () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value += 1;
    await loadRooms();
  }
};
const prevPage = async () => {
  if (currentPage.value > 1) {
    currentPage.value -= 1;
    await loadRooms();
  }
};

// 단편 장편 구분
const getStatusImage = (isShortStory) => {
  if (isShortStory) {
    return require("@/assets/images/mypage/play_list/short_story.png");
  } else {
    return require("@/assets/images/mypage/play_list/long_story.png");
  }
};

// 방 ID를 포맷팅하는 함수
const formatRoomId = (id) => {
  if (id < 10) {
    return `00${id}`;
  } else if (id < 100) {
    return `0${id}`;
  } else {
    return `${id}`;
  }
};

//모달
const isLogModalOpen = ref(false);
const selectedRoom = ref(null);

const openLogModal = (room) => {
  selectedRoom.value = room;
  isLogModalOpen.value = true;
};

const closeLogModal = () => {
  isLogModalOpen.value = false;
};

// 방 입장
const enterRoom = (roomId) => {
  router.push({ name: "Waiting", params: { roomId } });
};
</script>

<style scoped>
.menu-title {
  color: white;
  margin: 0 0 20px 20px;
}
/* .content-box {
  background-color: #3a3a3c;
  border-radius: 18px;
  padding: 10px;
} */
.right-box {
  height: 695px; /*왼쪽 박스와 높이 끝 같게 */
  padding: 20px 10px;
  /* border: 1px solid #3a3a3c; */
}
.pagination-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  /* justify-content: space-between; */
  align-items: center;
}
/* .pagination-arrow {
  color: white;
} */
.play-item-container {
  display: grid;
  /* display: flex; */
  /* 2열로 설정 */
  /* grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); */
  /* 3열로 설정 */
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  justify-content: center;
  /* 가운데 정렬 이거 하면 play-item의 width 필수 */
  /* flex-wrap: wrap; */
  width: 100%;
  height: 100%;
}

.play-item {
  background-color: #3c3d41;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  box-shadow: inset 1px 1px 4px rgba(158, 158, 158, 0.6);
  /* width: calc(33.333% - 20px); */
  max-width: 300px;
}
.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1px;
}
.play-item-title {
  margin: 8px;
  color: #bfbfc0;
}
.play-item-image {
  width: 100%;
  margin-bottom: 10px;
}

.item-main {
  display: flex;
  justify-content: space-between;
}
.play-item-details {
  margin: 0 10px 0 10px;
  color: #bfbfc0;
  font-size: 1.1rem;
}
.room-icons img {
  margin-right: 5px; /* 각 이미지 사이의 간격을 8px로 설정 */
}

.play-item-actions {
  margin-top: 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: auto;
  gap: 5px;
  padding: 5px;
}
.spacer {
  width: 75px; /* 두 번째 버튼이 차지하는 공간만큼의 너비 설정 */
}
.play-item-actions button {
  color: #cccccc;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.log-button {
  background-color: #343434;
}
.play-button {
  background-color: #38435f;
}

.play-item-actions button:hover {
  background-color: #041321;
}
.players-box {
  background: rgba(40, 34, 30, 0.31);
  box-shadow: inset 4px 4px 4px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  color: white;
  width: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.players-box span {
  font-size: 0.8rem;
  color: #bfbfc0;
}

.pagination {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transform: translateY(-50%);
  padding: 0 10px;
}
.pagination-button {
  background: none;
  border: none;
  cursor: pointer;
}
.pagination-button:first-of-type {
  left: 0; /* 왼쪽 끝에 위치 */
}
.pagination-arrow {
  width: 24px;
  height: 24px;
}
</style>
