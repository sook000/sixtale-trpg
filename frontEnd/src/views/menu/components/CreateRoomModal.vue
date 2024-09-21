<template>
  <div v-if="isLoading">로딩 중...</div>
  <form v-else @submit.prevent="handleCreateRoom">
    <div class="create-room-modal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <img
            src="@/assets/images/lobby/Title_Light.png"
            alt="Title Background"
            class="title-background"
          />
          <h4>방 만들기</h4>
        </div>
        <div class="modal-body">
          <form @submit.prevent="createRoom">
            <div class="form-group card">
              <label for="roomName" class="input-label">방제목</label>
              <input id="roomName" v-model="roomName" type="text" />
            </div>

            <div class="form-group card">
              <label for="password" class="input-label">비밀번호</label>
              <div class="password-container">
                <input id="password" v-model="password" type="password" :disabled="!isPrivate" />
                <label for="isPrivate" class="checkbox-label">
                  <input
                    id="isPrivate"
                    v-model="isPrivate"
                    type="checkbox"
                    class="styled-checkbox"
                  />
                  비밀방
                </label>
              </div>
            </div>

            <div class="form-group card">
              <label for="rule" class="input-label">룰</label>
              <select id="rule" v-model="rule">
                <option v-for="ruleOption in rules" :key="ruleOption.id" :value="ruleOption.id">
                  {{ ruleOption.title }}
                </option>
              </select>
            </div>

            <div class="form-group card">
              <label for="scenario" class="input-label">시나리오</label>
              <select id="scenario" v-model="scenario">
                <option
                  v-for="scenarioOption in scenarios"
                  :key="scenarioOption.id"
                  :value="scenarioOption.id"
                >
                  {{ scenarioOption.title }}
                </option>
              </select>
            </div>

            <div class="form-group card">
              <div class="form-group-half">
                <label for="maxPlayers" class="input-label">인원</label>
                <select id="maxPlayers" class="margin-right" v-model="maxPlayers">
                  <option v-for="n in 8" :key="n" :value="n">{{ n }}</option>
                </select>
              </div>
              <div class="form-group-half">
                <label for="playTime" class="input-label">예상 시간</label>
                <select id="playTime" v-model="playTime">
                  <option
                    v-for="n in [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 24, 48, 72]"
                    :key="n"
                    :value="n"
                  >
                    {{ n }}시간
                  </option>
                </select>
              </div>
            </div>

            <div class="form-group card">
              <div class="form-group-half">
                <label for="narrative" class="input-label">서사</label>
                <div class="checkbox-container">
                  <input
                    id="isLongStory"
                    v-model="isLongStory"
                    type="checkbox"
                    class="styled-checkbox"
                    @change="handleLongStoryChange"
                  />
                  <label for="isLongStory" class="checkbox-label no-card">장편</label>
                  <input
                    id="isShortStory"
                    v-model="isShortStory"
                    type="checkbox"
                    class="styled-checkbox"
                    @change="handleShortStoryChange"
                  />
                  <label for="isShortStory" class="checkbox-label no-card">단편</label>
                </div>
              </div>
              <div class="form-group-half">
                <label for="isVoice" class="input-label">음성</label>
                <input id="isVoice" v-model="isVoice" type="checkbox" class="styled-checkbox" />
              </div>
            </div>

            <div class="form-actions">
              <button type="button" @click="closeModal" class="cancel-button">취소</button>
              <button type="submit" class="create-button" @click="handleCreateRoom">생성</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </form>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { getRuleList } from "@/common/api/RuleAPI";
import { getScenarioListForCreateRoom } from "@/common/api/ScenarioAPI";
import { createRoom } from "@/common/api/RoomsAPI";
import { getMemberInfo } from "@/common/api/mypageAPI"; // 사용자 정보 가져오기 API
import WebSocketService from "@/store/websocket/waiting"; // WebSocket 서비스 가져오기

const router = useRouter();
const emit = defineEmits(["close"]);

const roomName = ref("");
const isPrivate = ref(false);
const password = ref("");
const rule = ref("");
const scenario = ref("");
const maxPlayers = ref(6);
const playTime = ref(12);
const isShortStory = ref(false);
const isVoice = ref(false);
const isCamera = ref(false);
const rules = ref([]);
const scenarios = ref([]);
const isLoading = ref(true);

const isLongStory = computed({
  get: () => !isShortStory.value,
  set: (value) => {
    isShortStory.value = !value;
  },
});

watch(isPrivate, (newValue) => {
  if (!newValue) {
    password.value = "";
  }
});

const closeModal = () => {
  emit("close");
};

// const createRoom = () => {
//   const roomData = {
//     roomName: roomName.value,
//     isPrivate: isPrivate.value,
//     password: isPrivate.value ? password.value : '',
//     rule: rule.value,
//     scenario: scenario.value,
//     maxPlayers: maxPlayers.value,
//     playTime: playTime.value,
//     narrative: isShortStory.value ? '단편' : '장편',
//     isVoice: isVoice.value,
//   };

//   console.log(roomData);
//   closeModal();
// };

const userId = ref("");

const handleCreateRoom = async () => {
  const roomData = {
    title: roomName.value,
    scenarioID: parseInt(scenario.value),
    description: `${roomName.value} 방입니다`,
    maxCount: maxPlayers.value,
    isLocked: isPrivate.value,
    password: isPrivate.value ? password.value : null,
    isShortStory: isShortStory.value,
    isVoice: isVoice.value,
  };

  console.log("Room data being sent:", roomData);

  if (isPrivate.value && password.value) {
    console.log("Password for the room:", password.value);
  } else {
    console.log("No password set for the room.");
  }

  if (!validateRoomData(roomData)) {
    alert("방 생성에 필요한 모든 정보를 올바르게 입력해주세요.");
    return;
  }

  try {
    const createdRoom = await createRoom(roomData);
    console.log("Room created successfully:", createdRoom);
    closeModal();

    await getMemberInfo()
      .then((response) => {
        userId.value = response.data.data.id;
      })
      .catch((error) => {
        console.error("Failed to fetch member info:", error);
      });

    if (createdRoom && createdRoom.id) {
      console.log(createdRoom.id, "@@@@@@@@@@@@@@", userId.value);

      await WebSocketService.connect(createdRoom.id, userId.value);

      // enter 메세지 보내기
      const messageData = {
        type: "ENTER", // 메시지 유형
        roomID: createdRoom.id, // 가져온 방 정보에서 roomID 사용
        memberID: userId.value, // 사용자 ID, 실제 값으로 설정
        content: "",
      };

      console.log(createdRoom.id + "---------=--=-" + userId.value);

      WebSocketService.sendMessage(messageData); // 서버로 메시지 전송
      router.push({
        name: "Waiting",
        params: { roomId: createdRoom.id.toString() },
        query: {
          title: createdRoom.title,
          isLocked: createdRoom.isLocked,
          password: createdRoom.isLocked ? password.value : null,
        },
      });
    } else {
      throw new Error("Invalid room data received");
    }
  } catch (error) {
    console.error("Failed to create room:", error);
    alert("방 생성에 실패했습니다. 다시 시도해주세요.");
  }
};

// 3. API 호출 전 데이터 유효성 검사
const validateRoomData = (data) => {
  if (!data.title || data.title.trim() === "") {
    console.error("Title is required");
    return false;
  }
  if (!data.scenarioID || isNaN(data.scenarioID)) {
    console.error("Invalid scenario ID");
    return false;
  }
  if (!data.maxCount || data.maxCount < 1) {
    console.error("Invalid max count");
    return false;
  }
  return true;
};

const handleLongStoryChange = () => {
  if (!isLongStory.value) {
    isShortStory.value = false;
  }
};

const handleShortStoryChange = () => {
  if (!isShortStory.value) {
    isLongStory.value = false;
  }
};

onMounted(async () => {
  isLoading.value = true;
  try {
    const [ruleList, scenarioList] = await Promise.all([
      getRuleList(),
      getScenarioListForCreateRoom(),
    ]);
    rules.value = ruleList;
    scenarios.value = scenarioList;

    if (rules.value.length > 0) {
      rule.value = rules.value[0].id;
    }
    if (scenarios.value.length > 0) {
      scenario.value = scenarios.value[0].id;
    }
  } catch (error) {
    console.error("Failed to fetch rules or scenarios:", error);
    alert("룰과 시나리오 정보를 불러오는데 실패했습니다. 페이지를 새로고침 해주세요.");
  } finally {
    isLoading.value = false;
  }
});
</script>

<style scoped>
.create-room-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.5); /* 배경 어둡게 */
  z-index: 1000;
}

.modal-content {
  background: #19120c;
  padding: 40px;
  padding-top: 0px;
  border-radius: 20px;
  width: 969px;
  max-width: 90%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  color: #e1d3a8;
  border: 1px solid #4a3a2e;
}

.modal-header {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  margin-bottom: 20px;
}

.modal-header h4 {
  position: absolute;
  color: #e1d3a8; /* 제목 텍스트 색상 */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.35);
}

.title-background {
  width: 100%;
  height: 60px; /* 적절한 높이 설정 */
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
}

.modal-body {
  color: #e1d3a8;
}

.card {
  background: #19120c;
  border: 1px solid #4a3a2e;
  border-radius: 10px;
  padding: 10px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.form-group {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-bottom: 20px;
}

.input-label {
  width: 120px;
  margin-right: 10px;
  box-shadow: inset 0px 4px 4px rgba(157, 131, 112, 0.25);
  filter: drop-shadow(0px 4px 4px rgba(0, 0, 0, 0.25));
  height: 44.65px;
  background: #46362a;
  border: 1px solid rgba(0, 0, 0, 0.5);
  border-radius: 5px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #e1d3a8;
  text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.35);
}

.form-group-half {
  width: 50%;
  display: flex;
  align-items: center;
}

.form-group input[type="text"],
.form-group input[type="password"],
.form-group select {
  flex: 1;
  padding: 8px;
  box-sizing: border-box;
  background: #57504a;
  border: 1px solid rgba(0, 0, 0, 0.5);
  border-radius: 5px;
  box-shadow: inset 0px 4px 4px rgba(157, 131, 112, 0.25);
  filter: drop-shadow(0px 4px 4px rgba(0, 0, 0, 0.25));
  color: #e1d3a8; /* 인풋 텍스트 색상 */
}

.form-group-inline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.password-container {
  display: flex;
  align-items: center;
  flex: 1;
}

.password-container input[type="password"] {
  flex: 1;
}

.password-container .checkbox-label {
  margin-left: 10px;
  color: #e1d3a8; /* 레이블 텍스트 색상 */
  text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.35);
}

.form-group input[type="checkbox"] {
  margin-left: 10px;
}

.checkbox-label {
  color: #e1d3a8; /* 체크박스 레이블 텍스트 색상 */
  text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.35);
}

.checkbox-container {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.checkbox-container .styled-checkbox {
  margin: 0 auto;
}

.form-group-half .styled-checkbox {
  margin-left: 0;
}

.no-card {
  background: none;
  box-shadow: none;
  border: none;
  height: auto;
  color: #e1d3a8;
  text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.35);
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}

button[type="button"].cancel-button {
  width: 140px;
  height: 40px;
  background: url("~@/assets/images/lobby/Cancel.png") no-repeat center center;
  background-size: cover;
  border: none;
  cursor: pointer;
  color: #e1d3a8;
}

button[type="submit"].create-button {
  width: 140px;
  height: 40px;
  background: url("~@/assets/images/lobby/Ok.png") no-repeat center center;
  background-size: cover;
  border: none;
  cursor: pointer;
  color: #e1d3a8;
}

.styled-checkbox {
  width: 25px;
  height: 25px;
  border-radius: 5px;
  background-color: #514339;
  border: 2px solid #2f251e;
  appearance: none;
  -webkit-appearance: none;
  cursor: pointer;
  position: relative;
}

.styled-checkbox:checked {
  background-color: #514339;
}

.styled-checkbox:checked::after {
  content: "";
  position: absolute;
  top: 5px;
  left: 5px;
  width: 10px;
  height: 10px;
  border: solid #a59e87;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 5px;
}

.margin-right {
  margin-right: 10px;
}
</style>
