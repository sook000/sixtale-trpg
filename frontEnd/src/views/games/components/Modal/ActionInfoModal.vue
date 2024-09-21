<template>
  <div class="modal-overlay" v-if="isVisible">
    <div class="modal-content" :style="modalContentStyle">
      <button
        class="close-button"
        :style="closeButtonStyle"
        @click="closeModal"
      >
        ✖
      </button>
      <div class="modal-header">
        <div class="modal-title-container">
          <img
            src="@/assets/images/character_sheet/title.png"
            alt="제목"
            class="modal-title-image"
          />
          <h2 class="modal-title-text">액션 설명</h2>
        </div>
      </div>
      <div class="modal-body" :style="modalBodyStyle">
        <div class="action-info">
          <!-- 기존의 액션 정보 표시 -->
          <div class="info-item">
            <div class="title-container">
              <img
                src="@/assets/images/character_sheet/nickname_light.png"
                alt="분류 배경"
                class="title-image"
              />
              <span class="title-text">분류</span>
            </div>
            <div class="info-box">
              <img
                src="@/assets/images/character_sheet/nickname_light.png"
                alt="분류명 배경"
                class="info-box-image"
              />
              <span class="info-text">{{ actionCategory }}</span>
            </div>
          </div>
          <!-- 주사위 정보 표시 -->
          <div v-if="props.action.isDice" class="dice-info">
            <div class="info-item">
              <div class="title-container">
                <img
                  src="@/assets/images/character_sheet/nickname_light.png"
                  alt="주사위 타입 배경"
                  class="title-image"
                />
                <span class="title-text">주사위 타입</span>
              </div>
              <div class="info-box">
                <img
                  src="@/assets/images/character_sheet/nickname_light.png"
                  alt="타입명 배경"
                  class="info-box-image"
                />
                <span class="info-text">D{{ props.action.diceType }}</span>
              </div>
            </div>
            <div class="info-item">
              <div class="title-container">
                <img
                  src="@/assets/images/character_sheet/nickname_light.png"
                  alt="주사위 개수 배경"
                  class="title-image"
                />
                <span class="title-text">주사위 개수</span>
              </div>
              <div class="info-box">
                <img
                  src="@/assets/images/character_sheet/nickname_light.png"
                  alt="개수명 배경"
                  class="info-box-image"
                />
                <span class="info-text">{{ props.action.diceCount }}</span>
              </div>
            </div>
          </div>
        </div>
        <hr class="divider" />
        <div class="description-container">
          <div class="title-container">
            <img
              src="@/assets/images/character_sheet/nickname_light.png"
              alt="설명 배경"
              class="title-image"
            />
            <span class="title-text-large">설명</span>
          </div>
          <div class="description-box">
            <div v-html="formattedDescription"></div>
          </div>
        </div>
      </div>
      <div class="modal-footer" :style="modalFooterStyle">
        <button
          class="footer-button"
          :style="closeButtonStyle"
          @click="closeModal"
        >
          닫기
        </button>
        <button
          class="footer-button"
          :style="selectButtonStyle"
          @click="selectAction"
        >
          선택
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  ref,
  computed,
  defineProps,
  defineEmits,
  onMounted,
  onBeforeUnmount,
} from "vue";
import {
  selectedPlayMemberID,
  selectedDice,
  setSelectedDice,
} from "@/store/state.js";
import { useRoute } from "vue-router";
import GameLogWebSocketService from "@/store/websocket/gameLog"; // WebSocket 서비스 가져오기

const props = defineProps(["action"]);
const emit = defineEmits(["close", "select"]);
const route = useRoute();

const isVisible = ref(true);

const closeModal = () => {
  isVisible.value = false;
  emit("close");
};

const selectAction = () => {
  if (props.action.isDice) {
    // 주사위 정보를 store의 상태에 저장
    setSelectedDice(props.action.diceType, props.action.diceCount);
  }
  console.log(props.action);

  // 메시지 보내는 로직
  const messageData = {
    gameType: "DICE_SETTING",
    roomID: parseInt(route.params.roomId, 10),
    playMemberID: selectedPlayMemberID.value,
    diceRolls: [
      {
        type: selectedDice.value.type,
        count: selectedDice.value.count,
      },
    ],
  };

  GameLogWebSocketService.sendMessage(messageData);

  emit("select", props.action);
  closeModal();
};

const modalContentStyle = computed(() => ({
  background: `url(${require("@/assets/images/character_sheet/main_background.png")}) no-repeat center center`,
  backgroundSize: "cover",
  width: "600px",
  height: "600px",
}));

const modalBodyStyle = computed(() => ({
  background: `url(${require("@/assets/images/character_sheet/tab_background.png")}) no-repeat center center`,
  backgroundSize: "cover",
  marginTop: "10px",
  padding: "20px",
}));

const modalFooterStyle = computed(() => ({
  background: `url(${require("@/assets/images/character_sheet/main_background.png")}) no-repeat center center`,
  backgroundSize: "cover",
  padding: "20px",
  display: "flex",
  justifyContent: "center",
  borderRadius: "0 0 10px 10px",
}));

const closeButtonStyle = computed(() => ({
  backgroundImage: `url(${require("@/assets/images/maps/background/close.png")})`,
  backgroundSize: "cover",
  color: "#fff",
}));

const selectButtonStyle = computed(() => ({
  backgroundImage: `url(${require("@/assets/images/maps/background/save.png")})`,
  backgroundSize: "cover",
  color: "#fff",
}));

const actionCategory = computed(() => props.action.category);
const actionName = computed(() => props.action.name);
const formattedDescription = computed(() => {
  let description = props.action.description.replace(/<br>/g, "<br/>");
  if (props.action.actionOption && props.action.actionOption.length > 0) {
    const optionsList = props.action.actionOption
      .map((option) => `<li>${option.content}</li>`)
      .join("");
    description += `<ul>${optionsList}</ul>`;
  }
  return description;
});
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
  width: 40px;
  height: 40px;
}

.modal-header {
  text-align: left;
  margin-bottom: 0;
  padding: 10px;
}

.modal-title-container {
  position: relative;
  width: 100%;
  text-align: center;
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

.modal-body {
  padding: 20px;
  border-radius: 0 0 10px 10px;
  flex: 1;
  margin-top: 10px;
  overflow: hidden; /* 외부 스크롤바 제거 */
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

.action-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-container {
  position: relative;
  display: inline-block;
  width: 150px;
}

.title-image {
  display: block;
  width: 100%;
  height: auto;
}

.title-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #fff;
  font-size: 1.2rem;
  white-space: nowrap;
}

.title-text-large {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #fff;
  font-size: 1.5rem;
  white-space: nowrap;
}

.info-box {
  position: relative;
  display: flex;
  align-items: center;
  height: 50px;
  flex-grow: 1;
}

.info-box-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #fff;
  font-size: 1.2rem;
  white-space: nowrap;
}

.divider {
  border: 0;
  border-top: 1px solid white;
  margin: 10px 0;
}

.description-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center; /* Center align items */
}

.description-box {
  background-color: rgba(0, 0, 0, 0.5);
  padding: 20px;
  border-radius: 5px;
  width: 100%; /* Adjust based on your needs */
  height: 250px; /* Adjust based on your needs */
  overflow-y: auto; /* Enable scrolling */
}

.description-box p {
  color: white;
  margin: 0;
}

.description-box::-webkit-scrollbar {
  width: 8px;
}

.description-box::-webkit-scrollbar-track {
  background: #201805;
  border-radius: 5px;
}

.description-box::-webkit-scrollbar-thumb {
  background-color: #855e2fee;
  border-radius: 5px;
  border: 2px solid #201805;
}

.modal-footer {
  padding: 20px;
  display: flex;
  justify-content: center;
  border-radius: 0 0 10px 10px;
}

.footer-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  background-size: cover;
  cursor: pointer;
  margin: 0 10px;
}

.footer-button:hover {
  transform: translateY(-2px);
}
</style>
