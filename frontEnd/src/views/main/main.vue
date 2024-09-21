<template>
  <div class="main-wrapper">
    <MainHeader />
    <div class="main-container">
      <main class="main-content">
        <router-view></router-view>
      </main>
    </div>
    <MainFooter v-if="!isHomePage" />
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import MainHeader from './components/main-header.vue';
import MainFooter from './components/main-footer.vue';

const route = useRoute();
const isHomePage = computed(() => route.name === 'home');
</script>

<style scoped>
.main-header {
  position: fixed;
  top: 10px;
  left: 0;
  width: 100%;
  height: var(--header-height, 70px);
  z-index: 1000; /* 헤더가 다른 콘텐츠 위에 있도록 z-index 설정 */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); /* 선택 사항: 헤더에 그림자 추가 */
}


.main-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: radial-gradient(50% 50% at 50% 50%, rgba(38, 38, 38, 0.87) 0%, #232428 100%);
}

.main-content {
  flex: 1;
  padding: 0;
}
</style>