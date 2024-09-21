<template>
  <div class="modal-overlay" v-if="isVisible" @click.self="closeModal">
    <!-- 모달 콘텐츠 -->
    <div class="modal-content" :style="modalContentStyle">
      <!-- 닫기 버튼 -->
      <button class="close-button" @click="closeModal" aria-label="닫기">
        &times;
      </button>
      <div class="modal-header">
        <div class="modal-title-container">
          <img :src="titleImage" alt="제목" class="modal-title-image" />
          <h2 class="modal-title-text">맵 선택</h2>
        </div>
      </div>
      <div class="modal-body" :style="modalBodyStyle">
        <div v-if="isLoading">로딩 중...</div>
        <div v-else-if="error">{{ error.message }}</div>
        <div class="map-grid">
          <div
            v-for="(map, index) in mapData"
            :key="map.id"
            class="map-item"
            @click="selectMap(index)"
            :class="{ selected: index === currentMapIndex }"
          >
            <img :src="map.imageURL" :alt="map.name" class="map-image" />
            <div class="map-title" :style="getMapTitleStyle">
              <span>{{ map.name }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer" :style="modalFooterStyle">
        <!-- 닫기 및 저장 버튼 -->
        <button
          class="footer-button"
          @click="closeModal"
          :style="closeButtonStyle"
        >
          닫기
        </button>
        <button
          class="footer-button"
          @click="saveSelection"
          :style="saveButtonStyle"
        >
          저장
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRoute } from "vue-router";
import { getMapList } from "@/common/api/RoomsAPI";
import { useMapStore } from "@/store/map/mapStore";
import titleImage from "@/assets/images/maps/background/title.png";
import mainBackground from "@/assets/images/maps/background/main_background.png";
import tabBackground from "@/assets/images/maps/background/tab_background.png";
import titleBackground from "@/assets/images/maps/background/nickname_light.png"; // 맵 제목 배경 이미지
import closeButtonImage from "@/assets/images/maps/background/close.png"; // "닫기" 버튼 이미지
import saveButtonImage from "@/assets/images/maps/background/save.png"; // "저장" 버튼 이미지
import GameLogWebSocketService from "@/store/websocket/gameLog"; // WebSocket 서비스 가져오기

// 이벤트 발신 설정
const emit = defineEmits(["close", "save"]);

// 모달 가시성 및 현재 맵 인덱스
const isVisible = ref(true);
const currentMapIndex = ref(0);
const previousMapIndex = ref(null); // 이전 맵 인덱스를 저장할 변수

// 상태 변수
const mapData = ref([]);
const isLoading = ref(true);
const error = ref(null);
const mapStore = useMapStore();
const { selectedMap, setSelectedMap } = mapStore;

const route = useRoute();
const roomId = ref(route.params.roomId);

// 컴포넌트가 마운트되면 API를 통해 맵 데이터를 로드
onMounted(async () => {
  try {
    // roomId는 prop이나 외부에서 제공받는다고 가정
    const response = await getMapList(roomId.value);
    mapData.value = response.mapList || [];
    isLoading.value = false;
  } catch (err) {
    error.value = err;
    isLoading.value = false;
  }

  // GameLogWebSocketService.connect(roomId.value);
});

// onBeforeUnmount(() => {
//   GameLogWebSocketService.disconnect();
// });

// 모달 콘텐츠 스타일
const modalContentStyle = computed(() => ({
  background: `url(${mainBackground}) no-repeat center center`,
  backgroundSize: "cover",
}));

// 모달 바디 스타일
const modalBodyStyle = computed(() => ({
  background: `url(${tabBackground}) no-repeat center center`,
  backgroundSize: "cover",
  marginTop: "10px",
}));

// 모달 푸터 스타일
const modalFooterStyle = computed(() => ({
  background: `url(${mainBackground}) no-repeat center center`,
  backgroundSize: "cover",
}));

// 맵 제목 스타일에 대한 계산 속성
const getMapTitleStyle = computed(() => ({
  backgroundImage: `url(${titleBackground})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  width: "100%", // 이미지의 너비에 맞춤
  height: "35px", // 이미지의 높이에 맞춤
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  color: "#fff",
  fontSize: "1rem",
  textAlign: "center",
}));

// 닫기 버튼 스타일
const closeButtonStyle = computed(() => ({
  background: `url(${closeButtonImage}) no-repeat center center`,
  backgroundSize: "cover",
}));

// 저장 버튼 스타일
const saveButtonStyle = computed(() => ({
  background: `url(${saveButtonImage}) no-repeat center center`,
  backgroundSize: "cover",
}));

// 모달 닫기
const closeModal = () => {
  isVisible.value = false;
  emit("close");
};

// 맵 선택
const selectMap = (index) => {
  // 현재 선택된 맵 인덱스를 이전 맵 인덱스로 저장
  previousMapIndex.value = currentMapIndex.value;
  currentMapIndex.value = index;
};

// 맵 선택 저장
const saveSelection = () => {
  const selectedMap = mapData.value[currentMapIndex.value];
  const previousMap =
    previousMapIndex.value !== null
      ? mapData.value[previousMapIndex.value]
      : null;
  console.log("선택된 맵:", selectedMap);
  mapStore.setSelectedMap(selectedMap); // 저장 버튼을 눌렀을 때 선택한 맵과 이미지를 저장

  // Log the map change event
  const messageData = {
    gameType: "MAP_CHANGE",
    roomID: parseInt(roomId.value, 10),
    currentMapID: previousMap ? previousMap.id : null, // 현재 선택된 맵 ID
    nextMapID: selectedMap.id, // 실제 다음 맵 ID로 업데이트 필요
  };

  console.log(messageData);

  // Send the log message via WebSocket
  GameLogWebSocketService.sendMessage(messageData);

  emit("save", selectedMap);
};
</script>

<style scoped>
/* 기존 스타일 코드 유지 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: transparent;
  padding: 0;
  border-radius: 10px;
  width: 800px;
  max-width: 90%;
  height: 740px;
  max-height: 90%;
  position: relative;
  color: #fff;
  overflow: hidden;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #fff;
}

.modal-header {
  text-align: center;
  margin-bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  width: 100%;
  overflow: hidden;
}

.modal-title-container {
  position: relative;
  width: 100%;
}

.modal-title-image {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.modal-title-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
  text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.5); /* 텍스트 가독성을 위한 그림자 */
}

.modal-body {
  padding: 20px;
  border-radius: 0 0 10px 10px;
  flex: 1;
  overflow-y: auto;
  margin-top: 10px;
  scrollbar-color: #855e2fee #201805;
}

.modal-body::-webkit-scrollbar {
  width: 8px;
}

.modal-body::-webkit-scrollbar-track {
  background: #201805;
  border-radius: 5px;
}

.modal-body::-webkit-scrollbar-thumb {
  background-color: #855e2fee;
  border-radius: 5px;
  border: 2px solid #201805;
}

.map-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); /* 열 수 조정 */
  gap: 20px;
  grid-auto-rows: minmax(150px, auto); /* 자동으로 행 높이 설정 */
  max-height: 600px; /* 그리드 높이를 제한하기 위해 추가 */
}

.map-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.3s;
}

.map-item.selected {
  border-color: #cd853f; /* 페루 색상 (Peru) */
}

.map-image {
  width: 170px; /* 모든 이미지의 동일한 너비 */
  height: 100px; /* 모든 이미지의 동일한 높이 */
  object-fit: cover;
  /* border-radius: 8px; */
}

.map-title {
  /* margin-top: 2px; */
  width: 100%;
  text-align: center;
  padding: 5px 0;
  border-radius: 5px;
  /* background-color: rgba(0, 0, 0, 0.6); */
}

.modal-footer {
  display: flex;
  justify-content: center;
  padding: 10px;
  border-radius: 0 0 10px 10px;
}

.footer-button {
  padding: 10px 20px;
  border: none;
  width: 20%;
  border-radius: 5px;
  color: #fff;
  cursor: pointer;
  margin: 0 10px;
  background-size: cover;
  transition: background-color 0.3s, transform 0.3s; /* 부드러운 전환 효과 추가 */
}

.footer-button:hover {
  background-color: #777;
  transform: translateY(-2px); /* 버튼 호버 시 살짝 위로 움직이는 효과 */
}
</style>
