<template>
  <div class="modal-overlay" v-if="isVisible">
    <div class="modal-content" :style="modalContentStyle">
      <button class="close-button" @click="closeModal" aria-label="닫기">
        &times;
      </button>
      <div class="modal-header">
        <div class="modal-title-container">
          <img
            src="@/assets/images/character_sheet/title.png"
            alt="제목"
            class="modal-title-image"
          />
          <h2 class="modal-title-text">아이템 선택</h2>
        </div>
      </div>
      <div class="modal-tabs">
        <button
          :class="{ active: activeTab === 'job' }"
          @click="activeTab = 'job'"
        >
          직업 아이템
        </button>
        <button
          :class="{ active: activeTab === 'common' }"
          @click="activeTab = 'common'"
        >
          공통 아이템
        </button>
      </div>
      <div class="modal-body" :style="modalBodyStyle">
        <div class="items-grid">
          <div
            v-for="availableItem in filteredItems"
            :key="availableItem.id"
            class="available-item"
            @click="selectItem(availableItem)"
            :class="{
              selected: selectedItem && selectedItem.id === availableItem.id,
            }"
          >
            <img
              :src="availableItem.imageUrl"
              :alt="availableItem.name"
              class="item-image"
            />
            <div class="item-details">
              <span class="item-name">{{ availableItem.name }}</span>
              <span class="item-weight">무게: {{ availableItem.weight }}</span>
              <span class="item-description">{{
                availableItem.description
              }}</span>
              <span
                v-if="availableItem.count > 0 && availableItem.count !== -1"
                class="item-count"
                >{{ availableItem.count }}</span
              >
            </div>
          </div>
        </div>
        <div v-if="showWarning" class="warning-message">
          하중을 넘어섬으로 추가할 수 없습니다.
        </div>
      </div>
      <div class="modal-footer" :style="modalFooterStyle">
        <div class="footer-left">
          <div class="weight-info">
            현재 하중: {{ currentWeight }} / 전체 하중: {{ totalWeight }}
          </div>
        </div>
        <div class="footer-right">
          <button
            class="footer-button"
            :style="closeButtonStyle"
            @click="closeModal"
          >
            닫기
          </button>
          <button
            class="footer-button"
            :style="saveButtonStyle"
            @click="addItem"
            :disabled="!selectedItem"
          >
            추가
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getEquipmentList, addEquipment } from "@/common/api/InventoryAPI.js";
import { selectedPlayMemberID } from "@/store/state.js";
import GameLogWebSocketService from "@/store/websocket/gameLog"; // WebSocket 서비스 가져오기

const route = useRoute();

const props = defineProps({
  currentWeight: Number,
  totalWeight: Number,
  ruleId: Number,
  jobId: Number,
});

const emit = defineEmits(["close", "select-item"]);

const isVisible = ref(true);
const selectedItem = ref(null);
const showWarning = ref(false);
const activeTab = ref("job"); // Default tab is 'job'
const equipmentList = ref([]);

const fetchEquipmentList = async () => {
  try {
    const list = await getEquipmentList(props.ruleId);
    equipmentList.value = list;
  } catch (error) {
    console.error("Error fetching equipment list:", error);
  }
};

const filteredItems = computed(() => {
  if (activeTab.value === "job") {
    return equipmentList.value.filter((item) => item.jobId === props.jobId);
  } else {
    return equipmentList.value.filter((item) => item.jobId === 9);
  }
});

const closeModal = () => {
  emit("close");
};

const selectItem = (item) => {
  selectedItem.value = item;
  showWarning.value = false;
};

const addItem = async () => {
  if (selectedItem.value) {
    const oldWeight = props.currentWeight; // 예전 무게
    const newWeight = props.currentWeight + selectedItem.value.weight;

    if (newWeight > props.totalWeight) {
      showWarning.value = true;
    } else {
      try {
        const equipmentData = {
          equipmentId: selectedItem.value.id,
          currentCount:
            selectedItem.value.count > 0 ? selectedItem.value.count : -1,
          weight: selectedItem.value.weight,
        };

        // 장비 추가하고 웹소켓 위해 응답 저장
        const response = await addEquipment(
          route.params.roomId,
          selectedPlayMemberID.value,
          equipmentData
        );
        // console.log("addItem response", response.data);

        // select-item 이벤트 발생
        emit("select-item", {
          ...selectedItem.value,
          currentCount: selectedItem.value.count,
        });

        //웹소켓 메시지 데이터
        const messageData = {
          gameType: "WEIGHT",
          roomID: parseInt(response.data.roomID, 10),
          playMemberID: selectedPlayMemberID.value,
          currentWeight: parseInt(oldWeight, 10),
          updateWeight: parseInt(newWeight, 10),
        };
        console.log("messageData", messageData);

        //웹소켓 전달
        GameLogWebSocketService.sendMessage(messageData);

        closeModal();
      } catch (error) {
        console.error("Failed to add item to the server:", error);
      }
    }
  }
};

onMounted(() => {
  fetchEquipmentList();
  GameLogWebSocketService.connect(route.params.roomId);
});

const modalContentStyle = computed(() => ({
  background: `url(${require("@/assets/images/character_sheet/main_background.png")}) no-repeat center center`,
  backgroundSize: "cover",
}));

const modalBodyStyle = computed(() => ({
  background: `url(${require("@/assets/images/character_sheet/tab_background.png")}) no-repeat center center`,
  backgroundSize: "cover",
  marginTop: "10px",
  overflowY: "auto",
  scrollbarWidth: "thin",
  scrollbarColor: "#855e2fee #201805",
}));

const modalFooterStyle = computed(() => ({
  background: `url(${require("@/assets/images/character_sheet/main_background.png")}) no-repeat center center`,
  backgroundSize: "cover",
  display: "flex",
  justifyContent: "space-between",
  alignItems: "center",
}));

// 닫기 버튼 스타일
const closeButtonStyle = computed(() => ({
  background: `url(${require("@/assets/images/maps/background/close.png")}) no-repeat center center`,
  backgroundSize: "cover",
}));

// 저장 버튼 스타일
const saveButtonStyle = computed(() => ({
  background: `url(${require("@/assets/images/maps/background/save.png")}) no-repeat center center`,
  backgroundSize: "cover",
}));
</script>

<style scoped>
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
  width: 700px;
  max-width: 90%;
  height: 500px;
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
  left: 15%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 1.5rem;
}

.modal-tabs {
  display: flex;
  justify-content: space-around;
  margin-top: 10px;
}

.modal-tabs button {
  flex: 1;
  padding: 10px;
  cursor: pointer;
  border: none;
  background: rgba(0, 0, 0, 0.2);
  color: white;
  transition: background 0.3s;
}

.modal-tabs button.active {
  background: rgba(0, 0, 0, 0.5);
}

.modal-body {
  padding: 20px;
  border-radius: 0 0 10px 10px;
  flex: 1;
  overflow-y: auto;
  margin-top: 10px;
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

.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-radius: 0 0 10px 10px;
}

.footer-left {
  display: flex;
  align-items: center;
}

.footer-right {
  display: flex;
  gap: 10px;
}

.footer-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  background-size: cover;
  color: #fff;
  cursor: pointer;
}

.footer-button:hover {
  transform: translateY(-2px); /* 버튼 호버 시 살짝 위로 움직이는 효과 */
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  width: 100%;
}

.available-item {
  display: flex;
  align-items: center;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.1);
  padding: 10px;
  border-radius: 5px;
  transition: background 0.3s;
}

.available-item:hover {
  background: rgba(255, 255, 255, 0.2);
}

.available-item.selected {
  background: rgba(0, 123, 255, 0.2);
}

.item-image {
  width: 60px;
  height: 60px;
  margin-right: 10px;
  object-fit: cover;
  display: block; /* ensure it is displayed */
}

.item-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.item-name {
  color: white;
  font-weight: bold;
}

.item-weight {
  color: white;
  font-size: 0.9rem;
}

.item-description {
  color: white;
  font-size: 0.9rem;
}

.item-count {
  color: white;
  font-size: 0.9rem;
}

.weight-info {
  color: white;
  font-weight: bold;
  text-align: left; /* 왼쪽 정렬 */
  margin-right: 20px; /* 테두리와의 거리 조정 */
}

.warning-message {
  margin-top: 10px;
  color: red;
  font-weight: bold;
  text-align: center;
  background: rgba(255, 0, 0, 0.1);
  padding: 10px;
  border-radius: 5px;
}
</style>
