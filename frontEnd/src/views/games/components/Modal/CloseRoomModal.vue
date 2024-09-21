<template>
  <div class="modal-overlay" @click.self="closeModal">
    <div class="modal-content" :style="modalContentStyle">
      <div class="modal-header">
        <div class="modal-title-container">
          <img src="@/assets/images/character_sheet/title.png" alt="제목" class="modal-title-image">
          <h2 class="modal-title-text">게임 종료</h2>
        </div>
      </div>
      <div class="modal-body" :style="modalBodyStyle">
        <p>정말 방을 폭파하시겠습니까?</p>
      </div>
      <div class="modal-footer" :style="modalFooterStyle">
        <button class="footer-button delete-button" @click="confirmClose">게임 종료</button>
        <button class="footer-button" @click="closeModal">취소</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, defineEmits } from 'vue';

const emit = defineEmits(['confirm', 'close']);

const closeModal = () => {
  emit('close');
};

const confirmClose = () => {
  emit('confirm');
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
    height: '100px',
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
  width: 300px; /* 크기 줄임 */
  max-width: 90%;
  height: 200px; /* 크기 줄임 */
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
  font-size: 1rem; /* 폰트 크기 줄임 */
}

.modal-body {
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 0.9rem; /* 폰트 크기 줄임 */
}

.modal-footer {
  display: flex;
  justify-content: center;
  padding: 10px;
  border-radius: 0 0 10px 10px;
}

.footer-button {
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  background: #3a3a3a;
  color: #fff;
  cursor: pointer;
  margin: 0 5px;
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
