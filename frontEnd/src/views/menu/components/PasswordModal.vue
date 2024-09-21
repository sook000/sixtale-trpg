<template>
  <div class="password-modal" v-if="show">
    <div class="modal-content" :style="modalStyle">
      <div class="modal-header">
        <img :src="titleImage" alt="Background" class="modal-title-image" />
        <h5>비밀번호 입력</h5>
      </div>
      <div class="modal-body">
        <div class="input-container">
          <input 
            type="password" 
            v-model="password" 
            placeholder="비밀번호를 입력하세요"
            @keyup.enter="submitPassword"
            :style="inputStyle"
          >
          <!-- <img :src="dropdownArrowBox" alt="Input" class="input-icon" /> -->
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn footer-button" @click="cancelEntry">
          <img :src="cancelImage" alt="닫기" />
          <span class="button-text">닫기</span>
        </button>
        <button type="button" class="btn footer-button" @click="submitPassword">
          <img :src="okImage" alt="입장" />
          <span class="button-text">입장</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

// 이미지 경로를 정확하게 설정해야 합니다.
import scheduleModal from '@/assets/images/room/calendar_modal/background.png';
import titleImage from '@/assets/images/room/calendar_modal/title.png';
import cancelImage from '@/assets/images/room/calendar_modal/cancel.png';
import dropdownArrowBox from '@/assets/images/room/calendar_modal/dropdown_arrow_box.png';
import dropdownBox from '@/assets/images/room/calendar_modal/dropdown_box.png';
import okImage from '@/assets/images/room/calendar_modal/ok.png';

const props = defineProps({
  show: Boolean,
  roomId: Number,
});

const emit = defineEmits(['close', 'submit']);

const password = ref('');

const submitPassword = () => {
  emit('submit', { roomId: props.roomId, password: password.value });
  password.value = '';
};

const cancelEntry = () => {
  emit('close');
  password.value = '';
};

const modalStyle = computed(() => ({
  backgroundImage: `url(${scheduleModal})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundRepeat: 'no-repeat',
}));

const inputStyle = computed(() => ({
  backgroundImage: `url(${dropdownBox})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundRepeat: 'no-repeat',
}));
</script>

<style scoped>
.password-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #291707;
  color: #ffffff;
  border-radius: 10px;
  width: 400px;
  padding: 0;
}

.modal-header {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  margin-bottom: 20px;
}

.modal-title-image {
  width: 100%;
  height: 100%; /* 이미지가 헤더를 꽉 채우도록 설정 */
  object-fit: cover; /* 이미지 비율 유지 */
}

.modal-header h5 {
  position: absolute;
  color: #E1D3A8;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.35);
  margin: 0;
  font-size: 20px; 
}

.modal-body {
  padding: 20px;
  text-align: center;
}

.input-container {
  position: relative;
  margin-top: 20px;
}

input {
  width: 100%;
  padding: 10px;
  background-color: transparent;
  border: none;
  color: #ffffff;
  font-size: 16px;
}

.input-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  pointer-events: none;
}

.modal-footer {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.footer-button {
  position: relative;
  margin: 0 10px;
  background: none;
  border: none;
}

.footer-button img {
  display: block;
}

.button-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #E1D3A8; /* 버튼 텍스트 색상 */
  pointer-events: none;
  font-size: 13px;
  text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.35);
}
</style>