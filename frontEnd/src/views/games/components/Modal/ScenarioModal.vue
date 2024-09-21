<template>
  <div class="modal-overlay" v-if="isVisible">
    <div class="modal-content" :style="modalContentStyle">
      <button class="close-button" @click="closeModal" aria-label="닫기">&times;</button>
      <div class="modal-header">
        <div class="modal-title-container">
          <img src="@/assets/images/character_sheet/title.png" alt="제목" class="modal-title-image">
          <h2 class="modal-title-text">시나리오 정보</h2>
        </div>
      </div>
      <div class="modal-body" :style="modalBodyStyle">
        <div class="scenario-info-container">
          <div class="info-item">
            <div class="title-container">
              <div class="image-with-text">
                <img :src="nicknameLight" alt="작가" class="title-image">
                <span class="title-text-overlay">작가</span>
              </div>
            </div>
            <div class="info-box">
              <img :src="nameBox" alt="작가 박스" class="info-box-image">
              <span class="info-text">{{ scenarioData.nickname }}</span>
            </div>
          </div>
          <div class="info-item">
            <div class="title-container">
              <div class="image-with-text">
                <img :src="nicknameLight" alt="장르" class="title-image">
                <span class="title-text-overlay">장르</span>
              </div>
            </div>
            <div class="info-box">
              <img :src="nameBox" alt="장르 박스" class="info-box-image">
              <span class="info-text">{{ scenarioData.genreList[0].name }}</span>
            </div>
          </div>
          <div class="description-section">
            <div class="description-title-container">
              <img :src="vectorImage" alt="설명 배경" class="description-title-image">
              <span class="description-title-text">설명</span>
            </div>
            <div class="description-content" :style="descriptionContentStyle">
              <p v-html="scenarioData.description"></p>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer" :style="modalFooterStyle">
        <button class="footer-button" @click="closeModal">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineEmits, onMounted } from 'vue';
import axios from 'axios';
import nicknameLight from '@/assets/images/character_sheet/nickname_light.png';
import nameBox from '@/assets/images/room/profile_modal/name_box.png';
import vectorImage from '@/assets/images/character_sheet/Vector.png';
import tendencyBox from '@/assets/images/room/profile_modal/tendency_box.png';

const emit = defineEmits(['close']);

const isVisible = ref(true);

const scenarioData = ref({
  nickname: '작가 이름',
  genreList: '장르 목록',
  description: '시나리오 내용'
});

const closeModal = () => {
  isVisible.value = false;
  emit('close');
};

const modalContentStyle = computed(() => ({
  background: `url(${require('@/assets/images/character_sheet/main_background.png')}) no-repeat center center`,
  backgroundSize: 'cover',
}));

const modalBodyStyle = computed(() => ({
  background: `url(${require('@/assets/images/character_sheet/tab_background.png')}) no-repeat center center`,
  backgroundSize: 'cover',
  marginTop: '10px',
}));

const modalFooterStyle = computed(() => ({
  background: `url(${require('@/assets/images/character_sheet/main_background.png')}) no-repeat center center`,
  backgroundSize: 'cover',
}));

const descriptionContentStyle = computed(() => ({
  background: `url(${tendencyBox}) no-repeat center center`,
  backgroundSize: 'cover',
  color: '#fff',
  padding: '40px',
  borderRadius: '5px',
  width: 'auto',
  height: 'auto',
  boxSizing: 'border-box',
  textAlign: 'left',
}));

const fetchScenarioData = async (scenarioId) => {
  try {
    console.log('Fetching scenario data...'); // 데이터 가져오기 시작 로그
    const response = await axios.get(`/api/v1/scenarios/${scenarioId}`);
    scenarioData.value = {
      nickname: response.data.data.nickName,
      genreList: response.data.data.genreList,
      description: response.data.data.description
    };
    console.log('Scenario data fetched:', scenarioData.value.nickname); // 데이터 가져오기 완료 로그
  } catch (error) {
    console.error('시나리오 데이터를 가져오는 중 오류 발생:', error);
  }
};

onMounted(() => {
  const scenarioId = 1; // 이 값을 실제 시나리오 ID로 변경하세요
  fetchScenarioData(scenarioId);
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: transparent;
  padding: 0;
  border-radius: 10px;
  width: 800px;
  max-width: 90%;
  height: 740px;
  max-height: 90%;
  position: relative;
  color: #fff;
  overflow: hidden;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #fff;
}

.modal-header {
  text-align: center;
  margin-bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  width: 100%;
  overflow: hidden;
}

.modal-title-container {
  position: relative;
  width: 100%;
}

.modal-title-image {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.modal-title-text {
  position: absolute;
  top: 50%;
  left: 15%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 1.5rem;
}

.modal-body {
  padding: 20px;
  border-radius: 0 0 10px 10px;
  flex: 1;
  overflow-y: auto;
  margin-top: 10px;
  scrollbar-color: #855e2fee #201805;
}

.modal-body::-webkit-scrollbar {
  width: 8px;
}

.modal-body::-webkit-scrollbar-track {
  background: #201805;
  border-radius: 5px;
}

.modal-body::-webkit-scrollbar-thumb {
  background-color: #855e2fee;
  border-radius: 5px;
  border: 2px solid #201805;
}

.modal-footer {
  display: flex;
  justify-content: center;
  padding: 20px;
  border-radius: 0 0 10px 10px;
}

.footer-button {
  padding: 10px 20px;
  border: none;
  width: 20%;
  border-radius: 5px;
  background: #3a3a3a;
  color: #fff;
  cursor: pointer;
  margin: 0 10px;
}

.footer-button:hover {
  background: #555;
}

.scenario-info-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-top: 10px; /* 상단 마진 추가 */
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  margin-top: 20px; /* 상단 마진 추가 */
}

.title-container {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

.image-with-text {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.title-image {
  width: auto;
  height: 50px;
  display: block;
}

.title-text-overlay {
  position: absolute;
  white-space: nowrap;
  padding: 0 10px;
  color: white;
  font-size: 1.2rem;
  pointer-events: none;
}

.info-box {
  display: flex;
  align-items: center;
  position: relative;
}

.info-box-image {
  width: auto;
  height: 55px;
}

.info-text {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  font-size: 1.0rem;
  white-space: nowrap;
}

.description-section {
  width: 100%;
  margin-top: 40px; /* 상단 마진 추가 */
}

.description-title-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  position: relative;
}

.description-title-image {
  width: auto;
  height: 50px;
}

.description-title-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #fff;
  font-size: 1.2rem;
  white-space: nowrap;
}

.description-content {
  color: #fff;
  padding: 20px;
  border-radius: 5px;
  width: 100%;
  height: auto; /* 자동 높이 설정 */
  box-sizing: border-box;
  text-align: left;
  background-size: cover;
  overflow-y: auto;
  scrollbar-color: #855e2fee #201805;
}

.description-content::-webkit-scrollbar {
  width: 8px;
}

.description-content::-webkit-scrollbar-track {
  background: #201805;
  border-radius: 5px;
}

.description-content::-webkit-scrollbar-thumb {
  background-color: #855e2fee;
  border-radius: 5px;
  border: 2px solid #201805;
}
</style>
