<template>
  <div class="home">
    <CarouselSection class="carousel-no-padding"/>
    <PopularScenario class="popular-scenario" />
    <NewScenario class="new-scenario"/>
    <IntroductionSection class="introduction-section" />
    <InformationSection class="information-section"/>
  </div>
</template>

<script setup>
import CarouselSection from './components/CarouselSection.vue';
import IntroductionSection from './components/IntroductionSection.vue';
import InformationSection from './components/InformationSection.vue';
import PopularScenario from './components/PopularScenario.vue';
import NewScenario from './components/NewScenario.vue';
import { onMounted, onUnmounted } from 'vue';

let scrollTimeout;

const handleScroll = () => {
  if (scrollTimeout) {
    clearTimeout(scrollTimeout);
  }

  scrollTimeout = setTimeout(() => {
    const sections = document.querySelectorAll('.home > *');
    let currentSectionIndex = 0;

    sections.forEach((section, index) => {
      const rect = section.getBoundingClientRect();
      if (rect.top <= window.innerHeight / 2 && rect.bottom >= window.innerHeight / 2) {
        currentSectionIndex = index;
      }
    });

    window.scrollTo({
      top: sections[currentSectionIndex].offsetTop,
      behavior: 'smooth'
    });
  }, 100); // Adjust the delay as needed
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});


</script>

<style scoped>
.home {
  padding: 0;
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - var(--header-height, 70px));
  scroll-snap-type: y mandatory; /* 수직 스크롤 스냅 설정 */
}

.carousel-no-padding, 
.popular-scenario, 
.new-scenario, 
.introduction-section, 
.information-section {
  min-height: 100vh; /* 전체 화면 높이 */
  scroll-snap-align: start; /* 섹션의 시작 부분이 뷰포트의 시작 부분과 맞춰지도록 설정 */
}

.carousel-no-padding{
  background: linear-gradient(270deg, rgba(26, 26, 26, 0.45) 65%, #0a0a10 100%), linear-gradient(89.84deg, rgba(60, 60, 60, 0.9) 65.72%, #0a0a10 100%);
}
/* 예제 색상으로 구분 */
/* .carousel-no-padding { background: #ff9999; }
.popular-scenario { background: #99ff99; }
.new-scenario { background: #9999ff; }
.introduction-section { background: #ffff99; }
.information-section { background: #ffcc99; } */

/* .home {
  padding: 0;
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - var(--header-height, 70px));
} */


/* .carousel-no-padding {
  padding: 0;
  margin: 0;
} */
</style>