<template>
  <div class="character-stats">
    <img src="@/assets/images/ingame/Border6.png" alt="Stats Background" class="stats-background">
    <div class="stats-content">
      <h3 class="character-name">{{ characterName }}</h3>
      <div class="level-job">
        <span class="level">Lv. {{ level }}</span>
        <span class="job">{{ job }}</span>
      </div>
      <div class="stats-grid">
        <div class="stat-item" v-for="(stat, index) in stats" :key="index">
          <span class="stat-name">{{ stat.name }}</span>
          <span class="stat-value">{{ stat.value }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

const props = defineProps({
  characterName: String,
  level: Number,
  job: String,
  stats: Array,  // stats는 객체 배열입니다. { name: '경험치', value: 16 }와 같은 형태입니다.
});
</script>

<style scoped>
.character-stats {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 0.5em; /* 텍스트 크기 조정 */
  padding: 10px;
  box-sizing: border-box;
  overflow: hidden;
}

.stats-background {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: -1;
  object-fit: contain;
}

.stats-content {
  z-index: 1;
  width: 100%;
  height: auto; /* 높이 자동 조정 */
  text-align: center;
}

.character-name {
  font-size: 1.1em;
  margin-bottom: 5px;
}

.level-job {
  display: flex;
  justify-content: center; /* 중앙에 배치 */
  width: 100%;
  padding: 2px 0;
}

.level, .job {
  margin: 0 10px; /* 양쪽 간격 조정 */
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 3px;
  width: 100%;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  background-color: #3e2723;
  padding: 3px;
  border-radius: 3px;
}

.stat-name, .stat-value {
  flex: 1;
  text-align: center;
}
</style>
