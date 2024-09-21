<template>
  <div class="right-grid-container">
    <Token class="token-area" />
    <Action class="action-area" />
    <Inventory class="inventory-area" />
    <Dice class="dice-area" />
  </div>
</template>

<script setup>
import Token from "@/views/games/components/ingame/Token.vue";
import Action from "@/views/games/components/ingame/Action.vue";
import Dice from "@/views/games/components/ingame/Dice.vue";
import Inventory from "@/views/games/components/ingame/Inventory.vue";
</script>

<style scoped>
.right-grid-container {
  display: grid;
  grid-template-columns: 1fr 2fr;
  grid-template-rows: 7fr 4fr 2fr;
  gap: 10px;
  height: 100%;
}

.token-area,
.action-area,
.inventory-area,
.dice-area {
  box-sizing: border-box;
  padding: 10px;
  margin: 0; /* 여백 제거 */
  overflow-y: auto;
}

.token-area {
  grid-column: 1 / 2;
  grid-row: 1 / 2;
}

.action-area {
  grid-column: 2 / 3;
  grid-row: 1 / 2;
}

.inventory-area,
.dice-area {
  display: flex;
  flex-direction: column;
  padding: 10px; /* 내부 패딩을 통일 */
  box-sizing: border-box; /* 박스 사이징 */
  width: 100%;
  height: 100%;
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  gap: 10px; /* 요소 간 간격 */
}

.inventory-area {
  grid-column: 1 / 3;
  grid-row: 2 / 3;
}

.dice-area {
  grid-column: 1 / 3;
  grid-row: 3 / 4;
}
</style>
