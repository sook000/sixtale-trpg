<template>
  <div class="main-content-inner">
    <div class="map-container">
      <!-- Map에 selectedMap을 prop으로 전달 -->
      <Map class="map" :selectedMap="selectedMap" />
    </div>
    <RightGrid class="right-grid" />
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import Map from "@/views/games/components/ingame/Map.vue";
import RightGrid from "@/views/games/components/ingame/RightGrid.vue";

// Props 정의
const props = defineProps({
  selectedMap: {
    type: Object,
    default: () => ({}),
  },
});
</script>

<style scoped>
.main-content-inner {
  display: flex;
  height: 100%;
  box-sizing: border-box;
}

.map-container {
  width: 66.67%;
  height: 100%;
  display: flex;
}

.map {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.right-grid {
  width: 33.33%;
  height: 100%;
}
</style>
