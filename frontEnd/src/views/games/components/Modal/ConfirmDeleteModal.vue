<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content" :style="modalContentStyle">
      <div class="modal-header">
        <div class="modal-title-container">
          <img
            src="@/assets/images/character_sheet/title.png"
            alt="제목"
            class="modal-title-image"
          />
          <h2 class="modal-title-text">아이템 삭제</h2>
        </div>
      </div>
      <div class="modal-body" :style="modalBodyStyle">
        <p>정말로 이 아이템을 삭제하시겠습니까?</p>
      </div>
      <div class="modal-footer" :style="modalFooterStyle">
        <button class="footer-button delete-button" @click="handleDelete">
          삭제
        </button>
        <button class="footer-button" @click="close">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, defineProps, defineEmits, onMounted } from "vue";
import { useRoute } from "vue-router";
import { deleteEquipment } from "@/common/api/InventoryAPI.js";
import { selectedPlayMemberID } from "@/store/state.js";
import GameLogWebSocketService from "@/store/websocket/gameLog"; // WebSocket 서비스 가져오기

const props = defineProps({
  item: Object, // 삭제할 아이템 정보를 받아옴
});

const emit = defineEmits(["close", "confirm"]);

const route = useRoute();

const close = () => {
  emit("close");
};

const handleDelete = async () => {
  if (props.item) {
    try {
      const roomId = route.params.roomId;
      const playMemberID = selectedPlayMemberID.value;

      console.log(
        "Deleting equipment with equipmentID:",
        props.item.equipmentID
      ); // 로그 추가

      // 서버에 삭제 요청 (equipmentID를 사용)
      const response = await deleteEquipment(
        roomId,
        playMemberID,
        props.item.equipmentID
      );
      console.log("delete response", response.data);
      // 성공 시 confirm 이벤트 emit
      emit("confirm");

      //웹소켓 메시지 데이터
      const messageData = {
        gameType: "WEIGHT",
        roomID: parseInt(response.data.roomID, 10),
        playMemberID: selectedPlayMemberID.value,
        currentWeight: parseInt(response.data.currentWeight, 10),
        updateWeight: parseInt(response.data.updateWeight, 10),
      };

      // 웹소켓 전달
      GameLogWebSocketService.sendMessage(messageData);

      close();
    } catch (error) {
      console.error("Failed to delete item from the server:", error);
      // 에러 처리 (필요시 사용자에게 알림)
    }
  }
};
//웹소켓 마운트
onMounted(() => {
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
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  padding: "20px",
  height: "100px",
}));

const modalFooterStyle = computed(() => ({
  background: `url(${require("@/assets/images/character_sheet/main_background.png")}) no-repeat center center`,
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
  width: 300px; /* 크기 줄임 */
  max-width: 90%;
  height: 200px; /* 크기 줄임 */
  max-height: 90%;
  position: relative;
  color: #fff;
  overflow: hidden;
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
  font-size: 1rem; /* 폰트 크기 줄임 */
}

.modal-body {
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 0.9rem; /* 폰트 크기 줄임 */
}

.modal-footer {
  display: flex;
  justify-content: center;
  padding: 10px;
  border-radius: 0 0 10px 10px;
}

.footer-button {
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  background: #3a3a3a;
  color: #fff;
  cursor: pointer;
  margin: 0 5px;
}

.footer-button:hover {
  background: #555;
}

.delete-button {
  background: #d9534f;
}

.delete-button:hover {
  background: #c9302c;
}
</style>
