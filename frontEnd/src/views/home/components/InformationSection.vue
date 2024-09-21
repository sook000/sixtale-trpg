<template>
  <section class="information">
    <div class="container">
      <div class="header">
        <img src="@/assets/images/SixtaleLogo.png" alt="Site Logo" class="logo-image" />
        <h1>TRPG in</h1>
        <img
        src="@/assets/images/SixtaleTitle.png"
        alt="Site Title"
        class="title-image"
      />
      </div>
      <p class="sub-description">SIXTALE은 여러분에게 다양한 지원을 제공합니다</p>
      <div class="features-grid">
        <div
          class="feature-item"
          v-for="(feature, index) in features"
          :key="feature.title"
        >
          <div
            class="feature-icon-wrapper"
          >
            <img
              :src="feature.icon"
              :alt="feature.title"
              class="feature-icon"
              :width="feature.width"
              :height="feature.height"
              :style="{ marginTop: feature.marginTop }"
            />
          </div>
          <h4 >{{ feature.title }}</h4>
          <p v-html="feature.description"></p> 
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';

const features = ref([
  {
    title: '로그저장',
    icon: require('@/assets/images/information/log.png'),
    description: '게임 진행 로그를 자동으로 저장하여<br/> 나중에 다시 볼 수 있습니다.',
    width: 30, 
    height: 35,
    marginTop: '12px'
  },
  {
    title: '게임룰북제공',
    icon: require('@/assets/images/information/rule.png'),
    description: '다양한 게임 룰북을 제공하여<br/> 쉽게 접근할 수 있습니다.',
    width: 32, 
    height: 32,
    marginTop: '15px'
  },
  {
    title: '자동화시스템',
    icon: require('@/assets/images/information/automation.png'),
    description: '게임 진행을 도와주는 <br/>다양한 자동화 시스템을 제공합니다.',
    width: 35, 
    height: 25,
    marginTop: '18px'
  },
  {
    title: 'AI이미지 제공',
    icon: require('@/assets/images/information/image.png'),
    description: '게임에 필요한 이미지를 <br/> AI로 생성하여 제공합니다.',
    width: 40, 
    height: 40,
    marginTop: '10px'
  },
  {
    title: '다양한 시나리오',
    icon: require('@/assets/images/information/scenario.png'),
    description: '많은 시나리오를 저장하고 <br/>언제든 플레이할 수 있습니다.',
    width: 45, 
    height: 45,
    marginTop: '10px'
  },
  {
    title: '챗봇도움말',
    icon: require('@/assets/images/information/chatbot.png'),
    description: '게임 진행 중 도움이 필요할 때 <br/>챗봇을 통해 도움말을 제공합니다.',
    width: 40, 
    height: 40,
    marginTop: '10px'
  }
]);
</script>

<style scoped>
.information {
  background: linear-gradient(270deg, rgba(26, 26, 26, 0.45) 65%, #0a0a10 100%), linear-gradient(89.84deg, rgba(60, 60, 60, 0.9) 65.72%, #0a0a10 100%);
  color: white;
  padding: 0;
}

.container {
  padding: 150px 20px 150px !important; 
  border-radius: 0; 
  max-width: 1200px; 
  margin: 0 auto;
  text-align: center;
}

.header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 0px;
  margin-bottom: 10px !important;
}

.logo-image {
  max-width: 80px;
  margin-bottom: 15px;
  margin-right: 10px;
  margin-left: 25px;
}

.title-image{
  margin-bottom: 15px;
}

.header h1 {
  font-size: 2.5rem; 
  margin: 0;
  color: #6981c0;
}

.sub-description {
  font-size: 1.5rem; 
  margin-bottom: 70px;
  color: #c3c5ce;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
}

.feature-item {
  padding: 20px;
  border-radius: 10px;
  text-align: center;
}

.feature-item p {
  color: #9D9EAD;
}

.feature-icon-wrapper {
  background-color: #333E5B;
  border-radius: 50%;
  padding: 10px;
  display: inline-block;
  margin-bottom: 10px;
  min-width: 80px;
  min-height: 80px;
}

.feature-icon {
  max-width: 50px;
  margin-top: 5px;
}

h4 {
  margin-top: 10px;
  font-weight: 400;
  margin-bottom: 15px;
  color: #9D9EAD;
}


</style>
