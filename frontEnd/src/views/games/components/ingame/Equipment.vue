<template>
  <div class="equipment-display-container">
    <div class="equipment-grid">
      <div
        class="equipment-item-card"
        v-for="(item, index) in equipment"
        :key="index"
        @click="selectItem(item)"
        :class="{ active: selectedItem === item }"
      >
        <img
          :src="item.imageURL || require('@/assets/images/ingame/Gold.png')"
          alt="아이템 이미지"
          class="item-image"
        />
        <div class="item-details">
          <div class="item-name">{{ item.name }}</div>
        </div>
      </div>
    </div>
    <div class="equipment-details-container">
      <div class="equipment-details">
        <div class="details-header">장비 설명</div>
        <div class="details-content">
          <div class="details-text">
            {{ selectedItem ? selectedItem.description : "장비를 선택하세요." }}
          </div>
        </div>
      </div>
    </div>
    <div class="gold-display">
      <div class="gold-amount">소지 골드: {{ gold }}G</div>
    </div>
    <div class="weight-info">
      현재 장비 하중: {{ currentWeight }} / 한계 장비 하중: {{ maxWeight }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps } from "vue";

const props = defineProps({
  equipmentData: Array,
  gold: Number,
  currentWeight: Number,
  maxWeight: Number,
});

const equipment = computed(() => props.equipmentData || []);
const gold = computed(() => props.gold || 0);
const currentWeight = computed(() => props.currentWeight || 0);
const maxWeight = computed(() => props.maxWeight || 0);
const selectedItem = ref(null);

const selectItem = (item) => {
  selectedItem.value = item;
};
</script>

<style scoped>
.equipment-display-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.equipment-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}

.equipment-item-card {
  background-color: #333;
  border-radius: 10px;
  overflow: hidden;
  padding: 10px;
  transition: transform 0.3s;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.item-image {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.item-details {
  background-color: rgba(0, 0, 0, 0.7);
  width: 100%;
  padding: 10px;
  box-sizing: border-box;
  border-top: 1px solid #444;
}

.item-name {
  font-weight: bold;
  color: #fff;
}

.equipment-item-card.active,
.equipment-item-card:hover {
  transform: scale(1.05);
}

.equipment-details-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.equipment-details {
  flex: 2;
  display: flex;
  flex-direction: column;
  margin-right: 20px;
  padding: 20px;
  overflow-x: hidden;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #855e2fee #201805;
}

.details-header {
  font-weight: bold;
  color: #fff;
  margin-bottom: 10px;
}

.details-content {
  background-color: #222;
  padding: 10px;
  border-radius: 5px;
  color: #fff;
  flex: 1;
}

.gold-display {
  flex: 1;
  text-align: right;
}

.gold-amount {
  font-size: 1.2rem;
  color: gold;
  margin-top: 5px;
  padding: 20px;
}

.weight-info {
  font-size: 1.8rem;
  text-align: center;
}
</style>
