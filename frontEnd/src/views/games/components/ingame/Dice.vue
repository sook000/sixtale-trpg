<template>
  <div class="dice-area" :style="backgroundStyle" @mousemove="handleMouseMove">
    <div class="dice-grid">
      <div class="dice-container-wrapper">
        <div v-for="dice in diceList" :key="dice.diceId" class="dice-container">
          <button
            @click="increaseCount(dice.diceId)"
            :style="increaseButtonStyle"
            class="control-button increase-button"
          ></button>
          <div
            class="dice-image-container"
            @mouseenter="showHoverText(dice.name)"
            @mouseleave="hideHoverText"
          >
            <img :src="dice.image" :alt="dice.name" class="dice" />
            <span
              class="dice-count"
              :class="{
                animate: dice.animate,
                'animate-on-action': dice.animateOnAction,
              }"
              >{{ dice.count }}</span
            >
          </div>
          <button
            @click="decreaseCount(dice.diceId)"
            :style="decreaseButtonStyle"
            class="control-button decrease-button"
          ></button>
        </div>
      </div>
      <div class="roll-container">
        <button
          @click="handleRoll"
          :style="RollbackgroundStyle"
          class="roll-button"
          :class="{ animateButton: rollAnimate }"
        >
          Roll
        </button>
        <button
          @click="handleReset"
          :style="ResetbackgroundStyle"
          class="reset-button"
          :class="{ animateButton: resetAnimate }"
        >
          Reset
        </button>
      </div>
    </div>
    <teleport to="body">
      <div
        class="hover-overlay"
        v-if="hoverText"
        :style="{ top: `${hoverPosition.y}px`, left: `${hoverPosition.x}px` }"
      >
        {{ hoverText }}
      </div>
    </teleport>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onBeforeUnmount } from "vue";
import eventBus from "@/common/lib/eventBus.js";
// 액션 인포모달에서 액션 선택 시, 주사위가 자동으로 세팅되게끔
import { setSelectedDice, selectedDice } from "@/store/state.js";
import { watch } from "vue";
import { useRoute } from "vue-router";
import GameLogWebSocketService from "@/store/websocket/gameLog"; // WebSocket 서비스 가져오기

const route = useRoute();

const diceList = reactive([
  {
    diceId: 4,
    name: "D4",
    image: require("@/assets/images/ingame/Dice4_2.png"),
    count: 0,
    animate: false,
  },
  {
    diceId: 6,
    name: "D6",
    image: require("@/assets/images/ingame/Dice6_2.png"),
    count: 0,
    animate: false,
  },
  {
    diceId: 8,
    name: "D8",
    image: require("@/assets/images/ingame/Dice8_2.png"),
    count: 0,
    animate: false,
  },
  {
    diceId: 10,
    name: "D10",
    image: require("@/assets/images/ingame/Dice10_2.png"),
    count: 0,
    animate: false,
  },
  {
    diceId: 12,
    name: "D12",
    image: require("@/assets/images/ingame/Dice12_2.png"),
    count: 0,
    animate: false,
  },
  {
    diceId: 20,
    name: "D20",
    image: require("@/assets/images/ingame/Dice20_2.png"),
    count: 0,
    animate: false,
  },
  {
    diceId: 100,
    name: "D100",
    image: require("@/assets/images/ingame/Dice100_2.png"),
    count: 0,
    animate: false,
  },
]);

watch(
  selectedDice,
  (newDice) => {
    if (newDice.type && newDice.count) {
      const selectedDiceItem = diceList.find(
        (dice) =>
          dice.diceId === parseInt(newDice.type.toString().replace("D", ""))
      );
      if (selectedDiceItem) {
        selectedDiceItem.count = newDice.count;
      }
    }
  },
  { immediate: true, deep: true }
);

onMounted(() => {
  GameLogWebSocketService.connect(route.params.roomId);

  GameLogWebSocketService.onMessageReceived("DICE_SETTING", async (message) => {
    console.log("Dice Setting message received", message);

    message.diceRolls.forEach((m) => {
      const newType = m.type.replace("D", "");
      setSelectedDice(parseInt(newType, 10), m.count);
    });
  });
});

onBeforeUnmount(() => {
  GameLogWebSocketService.disconnect();
});

const hoverPosition = ref({ x: 0, y: 0 });
const hoverText = ref("");
const rollAnimate = ref(false);
const resetAnimate = ref(false);

const handleMouseMove = (event) => {
  const overlayWidth = 100; // 호버 오버레이의 예상 너비 (px 단위)
  const overlayHeight = 50; // 호버 오버레이의 예상 높이 (px 단위)
  const offsetX = 10; // 커서 위치에서 약간 오른쪽으로 오프셋
  const offsetY = -20; // 커서 위치에서 약간 위쪽으로 오프셋
  let x = event.clientX + offsetX;
  let y = event.clientY + offsetY;

  // 화면 경계를 벗어나지 않도록 조정
  if (x + overlayWidth > window.innerWidth) {
    x = window.innerWidth - overlayWidth;
  }
  if (y + overlayHeight > window.innerHeight) {
    y = window.innerHeight - overlayHeight;
  }

  hoverPosition.value = { x, y };
};

const showHoverText = (text) => {
  hoverText.value = text;
};

const hideHoverText = () => {
  hoverText.value = "";
};

const emitRollDice = () => {
  const diceTypesToRoll = [];
  diceList.forEach((dice) => {
    for (let i = 0; i < dice.count; i++) {
      diceTypesToRoll.push({ type: dice.diceId, id: dice.diceId });
    }
  });
  eventBus.emit("roll-dice", diceTypesToRoll);
};

const resetDiceCounts = () => {
  diceList.forEach((dice) => {
    dice.count = 0;
  });

  // 주사위를 초기화할 때, selectedDice를 초기화
  selectedDice.value = {
    type: null,
    count: 0,
  };

  eventBus.emit("reset-dice");
};

const increaseCount = (diceId) => {
  const dice = diceList.find((dice) => dice.diceId === diceId);
  if (dice) {
    dice.count++;
    dice.animate = true;
    setTimeout(() => {
      dice.animate = false;
    }, 130);
  }
};

const decreaseCount = (diceId) => {
  const dice = diceList.find((dice) => dice.diceId === diceId);
  if (dice && dice.count > 0) {
    dice.count--;
    dice.animate = true;
    setTimeout(() => {
      dice.animate = false;
    }, 130);
  }
};

const handleRoll = () => {
  rollAnimate.value = true;
  animateDiceCounts();
  emitRollDice();
  setTimeout(() => {
    rollAnimate.value = false;
    resetDiceCounts();
  }, 300);
};

const handleReset = () => {
  resetAnimate.value = true;
  animateDiceCounts();
  setTimeout(() => {
    resetAnimate.value = false;
    resetDiceCounts();
  }, 300);
};

const animateDiceCounts = () => {
  diceList.forEach((dice) => {
    if (dice.count > 0) {
      dice.animateOnAction = true;
      setTimeout(() => {
        dice.animateOnAction = false;
      }, 300);
    }
  });
};

const backgroundStyle = {
  backgroundSize: "cover",
  borderRadius: "5px",
  width: "100%",
  height: "100%",
  padding: "1vh",
  boxSizing: "border-box",
  background: "rgba(37, 28, 21, 0.5)",
  border: "2px solid #89724D",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
};

const buttonStyle = {
  backgroundSize: "contain",
  backgroundPosition: "center",
  backgroundRepeat: "no-repeat",
  width: "100%",
  fontSize: "1.5vh",
  cursor: "pointer",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  color: "rgb(214, 205, 170)",
  backgroundColor: "transparent",
  border: "none",
};

const RollbackgroundStyle = {
  ...buttonStyle,
  backgroundImage: `url(${require("@/assets/images/ingame/Roll_Button.png")})`,
};

const ResetbackgroundStyle = {
  ...buttonStyle,
  backgroundImage: `url(${require("@/assets/images/ingame/Reset_Button.png")})`,
};

const arrowButtonStyle = {
  backgroundSize: "contain",
  backgroundRepeat: "no-repeat",
  width: "2vh",
  height: "2vh",
  border: "none",
  cursor: "pointer",
};

const increaseButtonStyle = {
  ...arrowButtonStyle,
  backgroundImage: `url(${require("@/assets/images/ingame/Arrow_Up.png")})`,
};

const decreaseButtonStyle = {
  ...arrowButtonStyle,
  backgroundImage: `url(${require("@/assets/images/ingame/Arrow_Down.png")})`,
};
</script>

<style scoped>
body {
  margin: 0;
  overflow: hidden;
}

.dice-area {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  padding: 0.5vh;
  overflow: hidden;
}

.dice-grid {
  display: grid;
  grid-template-columns: 9.2fr 0.8fr;
  gap: 0.5vh;
  width: 100%;
  max-width: 98%;
  max-height: 10vh;
  align-items: center;
  overflow: hidden;
}

.dice-container-wrapper {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.7vh;
}

.dice-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0;
  margin: 0;
  position: relative;
}

.increase-button {
  position: absolute;
  top: -1.4vh;
  left: 50%;
  transform: translateX(-50%);
  transition: transform 0.2s ease;
}

.decrease-button {
  position: absolute;
  bottom: -2.5vh;
  left: 50%;
  transform: translateX(-50%);
  transition: transform 0.2s ease;
}

.increase-button:hover,
.decrease-button:hover {
  transform: translateX(-50%) scale(1.2);
}

.dice-image-container {
  position: relative;
  width: 4vh;
  height: 4vh;
}

.hover-overlay {
  display: none;
  position: fixed; /* position: fixed를 사용하여 마우스 위치에 따라 움직임 */
  background: rgba(0, 0, 0, 0.5);
  color: white;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: 12px;
  font-weight: bold;
  border-radius: 5px;
  z-index: 1000; /* 높은 z-index 설정 */
  padding: 5px;
}

.hover-overlay {
  display: flex;
  position: fixed;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: 12px;
  font-weight: bold;
  border-radius: 5px;
  z-index: 1000; /* 높은 z-index 설정 */
  padding: 5px;
}

.dice {
  width: 100%;
  height: 100%;
}

.dice-count {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: rgb(214, 205, 170);
  text-shadow: -0.07vh -0.07vh 0 #8b602b, 0.07vh -0.07vh 0 #8b602b,
    -0.07vh 0.07vh 0 #8b602b, 0.07vh 0.07vh 0 #8b602b;
  font-weight: bold;
  font-size: 2vh;
  z-index: 1;
  transition: transform 0.2s ease;
}

.control-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  margin: 0;
  width: 1.5vh;
  height: 1.5vh;
}

.roll-container {
  display: flex;
  flex-direction: column;
  gap: 0.4vh;
  height: 110%;
  justify-content: center;
}

.roll-button,
.reset-button {
  height: 100%;
  width: 100%;
  font-size: 0.8vh;
  padding: 0.5vh;
  border: none;
  border-radius: 0.3vh;
  cursor: pointer;
  margin: 0 auto;
}

.dice-count.animate {
  animation: pulse 0.3s ease;
}

.dice-count.animate-on-action {
  animation: shrinkAndGrow 0.3s ease;
}

@keyframes pulse {
  0% {
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    transform: translate(-50%, -50%) scale(1.5);
  }
  100% {
    transform: translate(-50%, -50%) scale(1);
  }
}

@keyframes shrinkAndGrow {
  0% {
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    transform: translate(-50%, -50%) scale(0.2);
  }
  100% {
    transform: translate(-50%, -50%) scale(1);
  }
}

.animateButton {
  animation: buttonPulse 0.3s ease;
}

@keyframes buttonPulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(0.8);
  }
  100% {
    transform: scale(1);
  }
}

.roll-button:hover,
.reset-button:hover {
  filter: contrast(120%);
}

@media (max-width: 768px) {
  .dice-container-wrapper {
    grid-template-columns: repeat(4, 1fr);
  }

  .dice-grid {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto;
    max-height: none;
  }

  .roll-container {
    flex-direction: row;
    justify-content: space-between;
  }

  .roll-button,
  .reset-button {
    width: 40%;
  }
}

@media (max-width: 480px) {
  .dice-container-wrapper {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
