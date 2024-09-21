<template>
  <div class="map-section-container" @dragover.prevent @drop="onDrop">
    <!-- NPC 리스트 패널 -->
    <div :class="['npc-list', { 'npc-list-closed': !isNpcListOpen }]">
      <div class="npc-list-header" @click="toggleNpcList">
        <h3>NPC List</h3>
        <span v-if="!isNpcListOpen">▶</span>
        <span v-else>◀</span>
      </div>
      <div v-if="isNpcListOpen" class="npc-list-content">
        <div v-for="npc in npcList" :key="npc.id" class="npc-item">
          <p>{{ npc.description }}</p>
          <div class="npc-hp-bar">
            <div class="npc-hp-fill" :style="{ width: npc.currentHp * 5 + '%' }"></div>
          </div>
          <p v-if="isGM">
            <input
              type="number"
              v-model.number="npc.currentHp"
              @input="updateNpcHp(npc.id, npc.currentHp)"
              class="npc-hp-input"
            />
            HP
          </p>
          <p v-else>{{ npc.currentHp }} HP</p>
        </div>
      </div>
    </div>

    <!-- 맵 이미지 -->
    <img v-if="mapImage" :src="mapImage" alt="Map" class="map-image" />
    <div ref="rendererContainer" class="renderer-container"></div>

    <!-- 토큰들 -->
    <div
      class="token"
      v-for="token in placedTokens"
      :key="token.id"
      :style="{
        left: token.x + 'px',
        top: token.y + 'px',
        zIndex: token.zIndex || 2,
      }"
      @mousedown="startDrag(token, $event)"
      @dblclick="returnToken(token)"
    >
      <img :src="tokenImage" alt="Token" />
    </div>

    <!-- 그리드 오버레이 -->
    <div v-if="showGrid" class="grid-overlay">
      <div v-for="row in gridRows" :key="row" class="grid-row">
        <div
          v-for="col in gridCols"
          :key="col"
          class="grid-cell"
          @mouseenter="showDescription(row, col, $event)"
          @mouseleave="hideDescription"
        >
          <div
            v-if="isLaserActive(row, col)"
            class="laser-effect"
            @mouseenter="onLaserMouseEnter(row, col, $event)"
            @mouseleave="onLaserMouseLeave"
            @click="openModal(row, col)"
          ></div>
        </div>
      </div>
    </div>

    <!-- 정보 패널 -->
    <div
      class="info-panel"
      v-if="hoveredLaserDescription"
      :style="{ left: tooltipPosition.x + 'px', top: tooltipPosition.y + 'px' }"
    >
      <img class="info-background" :src="infoBackground" alt="Information Background" />
      <div class="info-content">
        <h3>{{ hoveredLaserDescription.details }}</h3>
      </div>
    </div>

    <!-- 모달 -->
    <div
      class="modal fade"
      id="eventModal"
      tabindex="-1"
      aria-labelledby="eventModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content custom-modal" :style="modalStyle">
          <div class="modal-header custom-modal-header">
            <img :src="titleImage" alt="Background" class="modal-title-image" />
            <div class="overlay-text">맵 변경</div>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div class="modal-body">
            <p>현재 맵을 새 맵으로 교체하시겠습니까?</p>
          </div>
          <div class="modal-footer custom-modal-footer">
            <button type="button" class="btn footer-button" data-bs-dismiss="modal">
              <img :src="cancelImage" alt="닫기" />
              <span class="button-text">아니요</span>
            </button>
            <button
              type="button"
              class="btn footer-button"
              @click="changeMap(hoveredLaserDescription)"
              data-bs-dismiss="modal"
            >
              <img :src="okImage" alt="저장" />
              <span class="button-text">예</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, onBeforeUnmount } from "vue";
import ThreeJSManager from "@/common/lib/ThreeJSManager";
import eventBus from "@/common/lib/eventBus.js";
import { useMapStore } from "@/store/map/mapStore";
import { getMapPlace, getMapNpcs, getMapList } from "@/common/api/RoomsAPI";
import { useRoute } from "vue-router";
import GameLogWebSocketService from "@/store/websocket/gameLog"; // WebSocket 서비스 가져오기

import scheduleModal from "@/assets/images/ingame/map/background.png";
import titleImage from "@/assets/images/ingame/map/title.png";
import cancelImage from "@/assets/images/ingame/map/cancel.png";
import okImage from "@/assets/images/ingame/map/ok.png";
import backButtonImage from "@/assets/images/ingame/map/back_button.png";

const props = defineProps({
  selectedMap: {
    type: Object,
    default: () => ({}),
  },
});

const rendererContainer = ref(null);
let threeJSManager = null;

const mapStore = useMapStore();
const route = useRoute();
const roomId = ref(route.params.roomId);

const mapImage = ref(null); // mapImage 초기 값 null로 설정
const tokenImage = require("@/assets/images/ingame/Token.png");
const infoBackground = ref(require("@/assets/images/hover/token_hover.png"));
const placedTokens = ref([]);
const showGrid = ref(true);
const gridSize = 50;

const npcData = ref({}); // NPC 정보를 저장할 객체
const selectedMapIndex = ref(0);

const npcList = ref([]); // NPC 리스트를 위한 ref
const npcInfo = ref([]); // NPC 리스트를 위한 ref
const isGM = ref(false); // GM 여부를 확인하는 변수
const isNpcListOpen = ref(true); // NPC 리스트 열림 여부

const cellDescriptions = ref({});

const messages = ref([]); // 모든 메시지를 저장하는 배열
const newMessage = ref("");
const { selectedMap, selectedToken, setSelectedMap, currentTokenX, currentTokenY } = mapStore;

// selectedMap prop의 변경 사항 감시
const tooltipPosition = ref({ x: 0, y: 0 });
let tooltipTimeout = null;

const activeLasers = ref(new Set());
const hoveredDescription = ref({ title: "", details: "" });
const hoveredLaserDescription = ref(null);

const pToken = ref({});
const mapData = ref([]);

watch(
  () => props.selectedMap,
  async (newMap) => {
    console.log("뉴맵" + newMap.id);
    if (newMap && newMap.imageURL) {
      mapImage.value = newMap.imageURL;
      setSelectedMap(newMap);

      try {
        await loadMapData(newMap.id); // 맵과 관련된 이벤트와 NPC 데이터를 로드
      } catch (error) {
        console.error("Error loading map or NPC data:", error);
      }
    } else {
      await loadDefaultMapImage(); // 맵 선택이 없을 경우 기본 맵 이미지를 로드
    }
  },
  { immediate: true }
);

const loadDefaultMapImage = async () => {
  try {
    const response = await getMapList(roomId.value);
    const mapList = response.mapList || [];

    if (mapList.length > 0) {
      const firstMap = mapList[0];
      mapImage.value = firstMap.imageURL; // 맵 목록의 첫 번째 이미지를 설정
      console.log("첫 번째 맵 이미지 URL:", mapImage.value);

      await loadMapData(firstMap.id); // 첫 번째 맵의 이벤트와 NPC 데이터를 로드
    } else {
      mapImage.value = require("@/assets/images/maps/map1.png"); // 맵 목록이 비어있으면 기본 이미지를 설정
      console.warn("맵 목록이 비어 있습니다.");
    }
  } catch (error) {
    mapImage.value = require("@/assets/images/maps/map1.png"); // 오류 발생 시 기본 이미지를 설정
    console.error("맵 목록을 가져오는 중 오류 발생:", error);
  }
};

const loadMapData = async (mapId) => {
  try {
    const roomId2 = roomId.value;

    const mapInfo = await getMapPlace(roomId2, mapId);
    const npcData = await getMapNpcs(roomId2, mapId); // NPC 정보 불러오기

    npcList.value = npcData.npcEvents || [];

    // 콘솔에 NPC 정보를 출력
    console.log("NPC List:", npcList.value);

    if (mapInfo && Array.isArray(mapInfo.placeEvents)) {
      activeLasers.value = new Set(
        mapInfo.placeEvents.map((event) => {
          const coord = `${event.row}-${event.col}`;
          return coord;
        })
      );

      cellDescriptions.value = {};
      mapInfo.placeEvents.forEach((event) => {
        cellDescriptions.value[`${event.row}-${event.col}`] = {
          title: `Event at (${event.row}, ${event.col})`,
          details: event.description,
          nextMapId: event.nextMapId,
          nextMapUrl: event.nextMapUrl,
        };
      });
    }
  } catch (error) {
    console.error("Error loading map or NPC data:", error);
  }
};

// onBeforeUnmount(() => {
//   GameLogWebSocketService.disconnect();
// });

const gridRows = computed(() => Array.from({ length: 10 }, (_, i) => i));
const gridCols = computed(() => Array.from({ length: 15 }, (_, i) => i));

const onLaserMouseEnter = (row, col, event) => {
  if (tooltipTimeout) clearTimeout(tooltipTimeout);
  const description = getDescription(row, col);
  hoveredLaserDescription.value = { ...description, position: { row, col } };

  const mapRect = document.querySelector(".map-section-container").getBoundingClientRect();
  const tooltipWidth = 300; // 툴팁의 예상 너비
  const tooltipHeight = 340; // 툴팁의 예상 높이

  let tooltipX = event.clientX + 10;
  let tooltipY = event.clientY + 10;

  // 툴팁 위치 조정 로직
  if (tooltipX + tooltipWidth > window.innerWidth) {
    tooltipX = window.innerWidth - tooltipWidth - 10;
  }

  if (tooltipY + tooltipHeight > window.innerHeight) {
    tooltipY = window.innerHeight - tooltipHeight - 10;
  }

  if (tooltipX < 0) {
    tooltipX = 10;
  }

  if (tooltipY < 0) {
    tooltipY = 10;
  }

  tooltipPosition.value = { x: tooltipX, y: tooltipY };

  if (hoveredLaserDescription.value.nextMapUrl) {
    infoBackground.value = hoveredLaserDescription.value.nextMapUrl;
  } else {
    infoBackground.value = require("@/assets/images/hover/token_hover.png");
  }
};

const onLaserMouseLeave = () => {
  tooltipTimeout = setTimeout(() => {
    hoveredLaserDescription.value = null;
  }, 1000);
};

const showDescription = (row, col, event) => {
  const description = getDescription(row, col);
  hoveredDescription.value = description;

  // 툴팁 위치 업데이트
  tooltipPosition.value = {
    x: event.clientX + 10,
    y: event.clientY + 10,
  };
};

const hideDescription = () => {
  hoveredDescription.value = { title: "", details: "" };
  if (tooltipTimeout) clearTimeout(tooltipTimeout);
  tooltipTimeout = setTimeout(() => {
    hoveredLaserDescription.value = null;
  }, 1000);
};

const getDescription = (row, col) => {
  const descriptionKey = `${row}-${col}`;
  const description = cellDescriptions.value[descriptionKey];
  if (description) {
    return description;
  }
  return { title: "Unknown Location", details: "No description available." };
};

const onDrop = (event) => {
  event.preventDefault();

  const tokenData = event.dataTransfer.getData("text/plain");
  try {
    const parsedToken = JSON.parse(tokenData);
    if (!placedTokens.value.find((t) => t.id === parsedToken.id)) {
      const mapRect = event.currentTarget.getBoundingClientRect();
      const tokenX = event.clientX - mapRect.left;
      const tokenY = event.clientY - mapRect.top;

      placedTokens.value.push({
        ...parsedToken,
        x: tokenX,
        y: tokenY,
      });

      console.log(`Token placed at: (${tokenX.toFixed(1)}, ${tokenY.toFixed(1)})`);

      // 토큰 좌표 이동 로그 전송
      sendMessage(tokenX.toFixed(1), tokenY.toFixed(1));

      const removeEvent = new CustomEvent("remove-token-from-list", {
        detail: parsedToken,
      });
      window.dispatchEvent(removeEvent);
    }
  } catch (error) {
    console.error("유효하지 않은 JSON 데이터:", tokenData);
  }
};

// 토큰 드랍 시 메시지 전송
const sendMessage = (x, y) => {
  const currentX = parseInt(currentTokenX, 10);
  const currentY = parseInt(currentTokenY, 10);

  console.log("selectedToken.value", selectedToken.value);

  const nextX = parseInt(x, 10);
  const nextY = parseInt(y, 10);

  const messageData = {
    gameType: "TOKEN_MOVE",
    roomID: parseInt(roomId.value, 10),
    tokens: [
      {
        playMemberID: selectedToken.value.id,
        currentPosition: { x: currentX, y: currentY },
        updatePosition: { x: nextX, y: nextY },
      },
    ],
  };

  GameLogWebSocketService.sendMessage(messageData); // 서버로 메시지 전송
};

const deleteTokenFromMap = (token) => {
  placedTokens.value = placedTokens.value.filter((t) => t.id !== token.id);
};

const returnToken = (token) => {
  deleteTokenFromMap(token);
  window.dispatchEvent(new CustomEvent("add-token-to-list", { detail: token }));
};

let draggingToken = null;
let offsetX = 0;
let offsetY = 0;

const startDrag = (token, event) => {
  event.preventDefault(); // 기본 동작 방지
  draggingToken = token;
  draggingToken.zIndexBackup = token.zIndex || 2; // 기존 z-index 저장
  token.zIndex = 9999; // z-index를 가장 높게 설정

  const tokenRect = event.target.getBoundingClientRect();
  offsetX = event.clientX - tokenRect.left;
  offsetY = event.clientY - tokenRect.top;
  document.addEventListener("mousemove", onDrag);
  document.addEventListener("mouseup", stopDrag);
};

const onDrag = (event) => {
  if (draggingToken) {
    const mapRect = document.querySelector(".map-section-container").getBoundingClientRect();
    const newX = event.clientX - mapRect.left - offsetX;
    const newY = event.clientY - mapRect.top - offsetY;

    draggingToken.x = newX;
    draggingToken.y = newY;
  }
};

const stopDrag = () => {
  if (draggingToken) {
    console.log(`Token dropped at: (${draggingToken.x.toFixed(1)}, ${draggingToken.y.toFixed(1)})`);
  }
  draggingToken = null;
  document.removeEventListener("mousemove", onDrag);
  document.removeEventListener("mouseup", stopDrag);
};

const handleDiceRolled = (results) => {
  results.forEach((result) => console.log(`${result.type}면체 주사위 결과: ${result.value}`));
};

const isLaserActive = (row, col) => {
  return activeLasers.value.has(`${row}-${col}`);
};

const openModal = (row, col) => {
  const description = getDescription(row, col);
  if (description) {
    hoveredLaserDescription.value = description;
    const modal = new bootstrap.Modal(document.getElementById("eventModal"), {
      backdrop: false,
    });
    modal.show();
  }
};

const changeMap = async (description) => {
  GameLogWebSocketService.connect(roomId.value);

  console.log(description);
  if (description && description.nextMapUrl && description.nextMapId) {
    mapImage.value = description.nextMapUrl;

    try {
      const newMapId = description.nextMapId;
      const mapInfo = await getMapPlace(roomId.value, newMapId);
      const npcData = await getMapNpcs(roomId.value, newMapId); // 새로운 맵의 NPC 데이터 로드

      // 웹소켓 메시지 전송
      // Log the map change event
      const messageData = {
        gameType: "MAP_CHANGE",
        roomID: parseInt(roomId.value, 10),
        currentMapID: 0, // 현재 선택된 맵 ID
        nextMapID: description.nextMapId, // 실제 다음 맵 ID로 업데이트 필요
      };

      GameLogWebSocketService.sendMessage(messageData);

      npcList.value = npcData.npcEvents || []; // NPC 리스트 업데이트

      if (mapInfo && Array.isArray(mapInfo.placeEvents)) {
        activeLasers.value = new Set(
          mapInfo.placeEvents.map((event) => {
            const coord = `${event.row}-${event.col}`;
            return coord;
          })
        );

        cellDescriptions.value = {};
        mapInfo.placeEvents.forEach((event) => {
          cellDescriptions.value[`${event.row}-${event.col}`] = {
            title: `Event at (${event.row}, ${event.col})`,
            details: event.description,
            nextMapId: event.nextMapId,
            nextMapUrl: event.nextMapUrl,
          };
        });

        console.log("Updated Map Info:", mapInfo);
        console.log("Map Events on this map:", mapInfo.placeEvents); // 여기서 맵 이벤트 콘솔 출력
      } else {
        console.warn("Invalid map data or placeEvents is undefined");
      }

      if (npcInfo && Array.isArray(npcInfo.npcEventList)) {
        npcData.value = {}; // 기존 NPC 데이터를 초기화합니다.
        npcInfo.npcEventList.forEach((npcEvent) => {
          npcData.value[`${npcEvent.row}-${npcEvent.col}`] = {
            description: npcEvent.description,
            currentHp: npcEvent.currentHp,
            imageUrl: backButtonImage, // 여기에 실제 NPC 이미지 경로를 설정하세요
          };
        });

        console.log("Updated NPC Info:", npcInfo);
      } else {
        console.warn("Invalid NPC data or npcEventList is undefined");
      }
    } catch (error) {
      console.error("Error loading new map or NPC data:", error);
    }
  }
};

const modalStyle = computed(() => ({
  backgroundImage: `url(${scheduleModal})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  backgroundRepeat: "no-repeat",
  zIndex: 1070,
}));
// GM 여부 설정 (임의로 true로 설정, 실제로는 인증된 GM 사용자만 true가 되도록 구현 필요)
isGM.value = true;

const updateNpcHp = (id, newHp) => {
  const npcIndex = npcList.value.findIndex((npc) => npc.id === id);
  if (npcIndex !== -1) {
    const currentTmp = npcList.value[npcIndex].currentHp;
    npcList.value[npcIndex].currentHp = newHp;
    console.log(`Updated NPC ${id} HP to ${newHp}`);

    const eventList = ref([]);

    GameLogWebSocketService.sendMessage({
      gameType: "EVENT_HP_CHANGE",
      roomID: roomId.value,
      events: [
        {
          npcEventID: id,
          currentHP: currentTmp,
          updateHP: newHp,
        },
      ],
    });
  }
};

const toggleNpcList = () => {
  isNpcListOpen.value = !isNpcListOpen.value;
};

onMounted(async () => {
  // GameLogWebSocketService.connect(roomId.value);

  const response = await getMapList(roomId.value);
  mapData.value = response.mapList || [];

  GameLogWebSocketService.onMessageReceived("EVNET_HP_CHANGE", (message) => {
    console.log("Event Hp Change received:", message);
  });

  GameLogWebSocketService.onMessageReceived("MAP_CHANGE", (message) => {
    console.log("Map Change received:", message);
    let selectedMap = mapData.value[message.nextMapID - 1];
    mapStore.setSelectedMap(selectedMap);
  });

  // 서버로부터 메시지를 수신할 때마다 콜백 실행
  GameLogWebSocketService.onMessageReceived("TOKEN_MOVE", (message) => {
    console.log("Token Move received:", message);
    messages.value.push(message); // 메시지 목록에 추가
    console.log("메시지 목록에 추가");

    // 수신한 메시지에서 토큰 정보 추출
    const { tokens } = message;

    // 각각의 토큰에 대해 처리
    tokens.forEach((token) => {
      const { playMemberID, updatePosition } = token;

      // 현재 맵에 있는 토큰 중에서 해당 ID를 가진 토큰을 찾기
      const movedToken = placedTokens.value.find((t) => t.id === playMemberID);

      if (movedToken) {
        // 토큰 위치 업데이트
        movedToken.x = updatePosition.x;
        movedToken.y = updatePosition.y;
        console.log(
          `Token with ID ${playMemberID} moved to (${updatePosition.x}, ${updatePosition.y})`
        );
      } else {
        // 토큰 없으면 새로 추가
        placedTokens.value.push({
          id: playMemberID,
          x: updatePosition.x,
          y: updatePosition.y,
          zIndex: 2,
        });
        console.log(
          `Token with ID ${playMemberID} added at (${updatePosition.x}, ${updatePosition.y})`
        );
      }
    });
  });
});

onMounted(() => {
  loadDefaultMapImage(); // 컴포넌트가 마운트될 때 기본 맵 이미지를 로드

  threeJSManager = new ThreeJSManager(rendererContainer.value);
  eventBus.on("dice-rolled", handleDiceRolled);
  window.addEventListener("toggle-grid", (event) => {
    showGrid.value = event.detail;
  });

  window.addEventListener("delete-token", (event) => {
    deleteTokenFromMap(event.detail);
  });
});

onUnmounted(() => {
  eventBus.off("dice-rolled", handleDiceRolled);
  window.removeEventListener("toggle-grid", () => {});
  window.removeEventListener("delete-token", () => {});
});
</script>

<style scoped>
.map-section-container {
  width: 100%;
  height: 100%;
  margin: 5px;
  position: relative;
  overflow: visible;
  z-index: 10;
}

.npc-list {
  position: absolute;
  top: 0;
  left: 0;
  width: 200px;
  background-color: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 10px;
  z-index: 100;
  overflow-y: auto;
  height: 100%;
  transition: transform 0.3s ease;
}

.npc-list-closed {
  transform: translateX(-190px); /* 리스트를 숨길 때 왼쪽으로 이동 */
}

.npc-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.npc-list-content {
  max-height: calc(100% - 40px); /* 헤더 부분 제외한 공간 활용 */
  overflow-y: auto;
}

.npc-item {
  margin-bottom: 10px;
}

.npc-hp-bar {
  width: 100%;
  background-color: #444;
  height: 10px;
  border-radius: 5px;
  overflow: hidden;
}

.npc-hp-fill {
  height: 100%;
  background-color: #f00;
}

.npc-hp-input {
  width: 50px;
  margin-top: 5px;
  padding: 2px;
  border: none;
  border-radius: 3px;
  background-color: #333;
  color: #fff;
  text-align: center;
}

.renderer-container {
  width: 100%;
  height: 80vh;
  background-color: transparent;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
}

.map-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  image-rendering: auto;
  z-index: 1;
}

.token {
  position: absolute;
  width: 40px;
  height: 40px;
  cursor: move;
  z-index: 2;
}

.token img {
  width: 100%;
  height: 100%;
}

.grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%; /* 그리드가 밀리지 않도록 너비를 100%로 설정 */
  height: 100%;
  display: grid;
  pointer-events: none;
  z-index: 3;
}

.grid-row {
  display: flex;
}

.grid-cell {
  width: 100%;
  height: 100%;
  border: 1px solid rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.6);
  position: relative;
  overflow: visible;
  z-index: 2;
}

.laser-effect {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: red;
  box-shadow: 0 0 10px 5px red;
  animation: pulse 1s infinite;
  z-index: 1;
  pointer-events: auto;
}

.info-panel {
  position: absolute;
  width: 30%;
  height: "auto";
  background-color: rgba(0, 0, 0, 0.9);
  color: white;
  border-radius: 8px;
  padding: 10px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.7);
  z-index: 5;
  transition: opacity 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.info-background {
  width: 100%;
  height: 100%;
  border-radius: 5px;
  margin-bottom: 5px;
  object-fit: cover;
}

.info-content h3 {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.info-content p {
  margin: 0;
  font-size: 12px;
  line-height: 1.4;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 0 10px 5px red;
  }
  50% {
    transform: scale(1.2);
    box-shadow: 0 0 15px 10px red;
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 10px 5px red;
  }
}

.tooltip {
  position: absolute;
  background-color: rgba(0, 0, 0, 0.75);
  color: #fff;
  padding: 3px; /* 패딩 크기 줄이기 */
  border-radius: 4px;
  pointer-events: none;
  z-index: 1000;
  max-width: 120px; /* 툴팁의 최대 너비 줄이기 */
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.3);
  font-size: 10px; /* 글씨 크기 줄이기 */
}

.tooltip-image {
  width: 70%; /* 이미지 크기를 줄이기 */
  height: auto;
  border-radius: 3px;
  margin-bottom: 3px; /* 이미지 아래의 간격 줄이기 */
  background-color: transparent;
  z-index: 1001;
  position: relative;
  display: block;
}

.tooltip h4 {
  margin: 0;
  font-size: 12px; /* 제목 글씨 크기 줄이기 */
  font-weight: bold;
}

.tooltip p {
  margin: 3px 0 0;
  font-size: 10px; /* 본문 글씨 크기 줄이기 */
  line-height: 1.1; /* 줄 간격 줄이기 */
}

.modal-content {
  border-radius: 10px;
  padding: 20px;
  z-index: 1070;
  background-color: #1e1e1e;
  color: #fff;
}

.modal-header {
  border-bottom: none;
}

.custom-modal-header {
  position: relative;
}

.modal-body {
  text-align: center;
}

.modal-title-image {
  width: 100%;
  height: auto;
  position: relative;
  display: block;
  margin-top: -14%;
}

.overlay-text {
  position: absolute;
  top: -33%;
  left: 50%;
  transform: translateX(-50%);
  color: #ffffff;
  font-size: 20px;
  font-weight: bolder;
}

.modal-footer {
  border-top: none;
}

.custom-modal-footer {
  display: flex;
  justify-content: space-between;
}

.footer-button {
  position: relative;
  margin: 0 10px;
}

.footer-button img {
  display: block;
}

.button-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  pointer-events: none;
  font-size: 13px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 20px;
}

.modal-backdrop {
  display: none !important;
}
</style>
