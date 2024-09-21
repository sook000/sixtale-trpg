<template>
  <div class="gm-section-container" :style="backgroundStyle">
    <div class="gm-info-box">
      <img :src="gmImage" alt="Game Master" class="gm-image" />
      <p class="gm-name">{{ gm.nickname }}</p>
      <VoiceChatButton :userId="gm.id" /> <!-- GM의 음성 채팅 버튼 -->
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import VoiceChatButton from '../../VoiceChatButton.vue'; // VoiceChatButton 컴포넌트 임포트

const props = defineProps({
  isGM: {
    type: Boolean,
    required: true,
  },
  canStartGame: {
    type: Boolean,
    default: false,
  },
});

// 더미 데이터로 GM 정보 설정
const gm = ref({
  id: 9, // GM의 고유 ID를 하드코딩으로 설정
  nickname: '미카엘', // GM의 닉네임
});

// 배경 이미지와 GM 이미지 설정
const backgroundImage = require('@/assets/images/ingame/Border2.png');
const gmImage = require('@/assets/images/ingame/GM.png');

const backgroundStyle = {
  backgroundImage: `url(${backgroundImage})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  padding: '2px',
  boxSizing: 'border-box',
  height: '5%',
  display: 'flex',
  alignItems: 'center',
};

console.log('GM 정보:', gm.value); // GM 정보 로그 출력
</script>

<style scoped>
.gm-section-container {
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  border: 1px solid #5a4d41;
}

.gm-info-box {
  width: 90%;
  height: 100%;
  text-align: center;
  padding: 5px;
  color: #e6ddc4;
  overflow: hidden;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.gm-image {
  max-width: 50%;
  margin-right: 10px;
}

.gm-name {
  margin: 0;
  color: #E1D3A8;
  white-space: nowrap;
  font-size: 1.1rem;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
