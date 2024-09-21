<!-- UserInfo.vue -->
<template>
  <div class="modal-overlay" v-if="isVisible">
    <div class="modal-content" :style="modalContentStyle">
      <button class="close-button" @click="closeModal" aria-label="닫기">
        &times;
      </button>
      <div class="modal-header">
        <div class="modal-title-container">
          <img :src="titleImage" alt="제목" class="modal-title-image" />
          <h2 class="modal-title-text">프로필</h2>
        </div>
      </div>
      <div class="modal-body" :style="modalBodyStyle">
        <div class="character-info-container">
          <div class="info-section">
            <div class="left-section">
              <div class="image-container">
                <div class="image-wrapper">
                  <img
                    :src="characterData.image || defaultImage"
                    alt="캐릭터 이미지"
                    class="character-image"
                  />
                  <img :src="avatarFrame" alt="프레임" class="frame-image" />
                </div>
              </div>
            </div>
            <div class="middle-section">
              <div class="title-container">
                <div class="image-with-text">
                  <img
                    :src="nicknameLight"
                    alt="닉네임"
                    class="title-image"
                  />
                  <span class="title-text-overlay">닉네임</span>
                </div>
              </div>
            </div>
            <div class="right-section">
              <div class="nickname-box">
                <img
                  :src="nameBox"
                  alt="닉네임 박스"
                  class="nickname-box-image"
                />
                <span class="nickname-text" :title="characterData.name">
                  {{ characterData.name }}
                </span>
              </div>
              <!-- 추가 정보 항목은 여기 추가 -->
            </div>
          </div>
          <div class="tendency-section">
            <div class="tendency-title-container">
              <img
                :src="tendencyVector"
                alt="성향 배경"
                class="tendency-title-image"
              />
              <span class="tendency-title-text">성향</span>
            </div>
            <div class="tendency-content" :style="tendencyContentStyle">
              <p v-html="characterData.background"></p>
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
import { ref, computed, defineProps, defineEmits, watch } from 'vue';
import defaultImage from '@/assets/images/users/default.png'; // 기본 이미지 경로 확인
import titleImage from '@/assets/images/character_sheet/title.png';
import nicknameLight from '@/assets/images/character_sheet/nickname_light.png';
import avatarFrame from '@/assets/images/room/profile_modal/avatar_frame.png';
import nameBox from '@/assets/images/room/profile_modal/name_box.png';
import tendencyVector from '@/assets/images/character_sheet/Vector.png';
import tendencyBox from '@/assets/images/room/profile_modal/tendency_box.png';
import mainBackground from '@/assets/images/character_sheet/main_background.png';
import tabBackground from '@/assets/images/character_sheet/tab_background.png';

const props = defineProps({
  user: Object, // 이 부분이 정확하게 전달되고 있는지 확인
});

const emit = defineEmits(['close']);

const characterData = ref({
  image: '',
  name: '',
  race: '',
  job: '',
  background: '',
});

const isVisible = ref(true);

const closeModal = () => {
  isVisible.value = false;
  emit('close');
};

const modalContentStyle = computed(() => ({
  background: `url(${mainBackground}) no-repeat center center`,
  backgroundSize: 'cover',
}));

const modalBodyStyle = computed(() => ({
  background: `url(${tabBackground}) no-repeat center center`,
  backgroundSize: 'cover',
  marginTop: '10px',
}));

const modalFooterStyle = computed(() => ({
  background: `url(${mainBackground}) no-repeat center center`,
  backgroundSize: 'cover',
}));

const tendencyContentStyle = computed(() => ({
  background: `url(${tendencyBox}) no-repeat center center`,
  backgroundSize: 'cover',
  color: '#fff',
  padding: '20px',
  borderRadius: '5px',
  width: '100%',
  height: '250px',
  boxSizing: 'border-box',
  textAlign: 'left',
}));

// 사용자 정보가 변경될 때마다 characterData를 업데이트합니다.
watch(
  () => props.user, // props.user의 변경을 감지
  (newUser) => {
    if (newUser) {
      characterData.value = {
        image: newUser.profileImage || defaultImage, // props.user.profileImage 경로 확인
        name: newUser.name || '',
        race: newUser.race || '인간',
        job: newUser.job || '전사',
        background: newUser.background || '',
      };
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.character-info-container {
  width: 100%;
  padding: 20px;
}

.image-container {
  border-radius: 50%;
  overflow: hidden;
  width: 200px;
  height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.character-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.frame-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.info-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.middle-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.title-container {
  display: flex;
  align-items: center;
}

.image-with-text {
  position: relative;
  display: inline-flex; /* 내용에 맞게 동적 크기 조절을 위해 inline-flex로 변경 */
  align-items: center; /* 수직 중앙 정렬 */
  justify-content: center; /* 수평 중앙 정렬 */
}

.title-image {
  width: auto; /* 컨테이너에 맞게 이미지 크기 조절 */
  height: 50px; /* 고정 높이 설정 */
  display: block;
}

.title-text-overlay {
  position: absolute;
  white-space: nowrap; /* 텍스트 줄바꿈 방지 */
  padding: 0 10px; /* 텍스트에 여백 추가 */
  color: white;
  font-size: 1.2rem;
  pointer-events: none; /* 텍스트가 포인터 이벤트를 차단하지 않도록 설정 */
}

.right-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 20px;
}

.nickname-box {
  display: inline-flex; /* 박스를 텍스트 길이에 따라 자동 조절 */
  align-items: center;
  justify-content: center;
  position: relative;
}

.nickname-box-image {
  width: auto; /* 이미지의 너비를 텍스트 길이에 맞게 자동 조절 */
  height: 55px;
}

.nickname-text {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  font-size: 1.0rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 80%; /* 말줄임 효과를 위해 최대 너비 설정 */
  cursor: pointer; /* 툴팁을 위해 커서 변경 */
}

.nickname-text:hover::after {
  content: attr(title); /* title 속성을 툴팁으로 표시 */
  position: absolute;
  background: rgba(0, 0, 0, 0.75);
  color: #fff;
  padding: 5px 10px;
  border-radius: 5px;
  white-space: nowrap;
  z-index: 10;
  top: -25%; /* 필요에 따라 위치 조정 */
  left: 50%;
  transform: translateX(-50%);
}

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
}

.modal-footer {
  display: flex;
  justify-content: center;
  padding: 10px;
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

.tendency-section {
  width: 100%;
  margin-top: 20px;
}

.tendency-title-container {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  position: relative;
}

.tendency-title-image {
  width: auto;
  height: 40px; /* 제목 이미지 크기 조절 */
}

.tendency-title-text {
  position: absolute;
  top: 50%;
  left: 20px;
  transform: translateY(-50%);
  color: #fff;
  font-size: 1.2rem;
  white-space: nowrap;
}

.tendency-content {
  color: #fff;
  padding: 20px;
  border-radius: 5px;
  width: 100%;
  height: 245px;
  box-sizing: border-box;
  text-align: left;
}
</style>
