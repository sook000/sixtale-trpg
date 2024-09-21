<template>
  <div class="modal-overlay" @click.self="close">
      <div class="modal-content" :style="modalContentStyle">
          <div class="modal-header">
              <div class="modal-title-container">
                  <img src="@/assets/images/character_sheet/title.png" alt="제목" class="modal-title-image">
                  <h2 class="modal-title-text">캐릭터 시트 삭제</h2>
              </div>
          </div>
          <div class="modal-body" :style="modalBodyStyle">
              <p>정말로 이 캐릭터 시트를 삭제하시겠습니까?</p>
              <p class="warning"><strong>작성한 모든 캐릭터 정보가 날아갑니다!</strong></p>
          </div>
          <div class="modal-footer" :style="modalFooterStyle">
              <button class="footer-button delete-button" @click="handleDelete">삭제</button>
              <button class="footer-button" @click="close">닫기</button>
          </div>
      </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { deleteCharacterSheet } from '@/common/api/CharacterSheetAPI.js';  // 삭제 API

const emit = defineEmits(['close', 'confirm']);
const props = defineProps({
  roomId: {
      type: Number,
      required: true
  }
});

const close = () => {
  emit('close');
};

const handleDelete = async () => {
  try {
    await deleteCharacterSheet(props.roomId); // 삭제 요청 보내기
    emit('confirm');  // 삭제가 성공하면 부모 컴포넌트로 알림
    close();
  } catch (error) {
    console.error('Error deleting character sheet:', error);
  }
};

const modalContentStyle = computed(() => ({
  background: `url(${require('@/assets/images/character_sheet/main_background.png')}) no-repeat center center`,
  backgroundSize: 'cover',
}));

const modalBodyStyle = computed(() => ({
  background: `url(${require('@/assets/images/character_sheet/tab_background.png')}) no-repeat center center`,
  backgroundSize: 'cover',
  marginTop: '10px',
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  padding: '20px',
  height: '200px',
  textAlign: 'center'
}));

const modalFooterStyle = computed(() => ({
  background: `url(${require('@/assets/images/character_sheet/main_background.png')}) no-repeat center center`,
  backgroundSize: 'cover',
}));
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
  width: 600px; /* 크기 확대 */
  max-width: 90%;
  height: 300px; /* 크기 확대 */
  max-height: 90%;
  position: relative;
  color: #fff;
  overflow: hidden;
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
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 1.2rem;
  text-align: center;
}

.warning {
  font-size: 1.7rem;
  font-weight: bold;
  color: #c9302c;
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
  border-radius: 5px;
  background: #3a3a3a;
  color: #fff;
  cursor: pointer;
  margin: 0 10px;
}

.footer-button:hover {
  background: #555;
}

.delete-button {
  background: #d9534f;
}

.delete-button:hover {
  background: #c9302c;
}
</style>
