<template>
  <div class="modal" @click.self="closeModal">
    <div class="modal-content custom-modal" :style="modalStyle">
      <div class="modal-header custom-modal-header">
        <img :src="titleImage" alt="Background" class="modal-title-image" />
        <div class="overlay-text">경고</div>
      </div>
      <div class="modal-body">
        {{ user.name }} 님을 추방하시겠습니까?
      </div>
      <div class="modal-footer custom-modal-footer">
        <button type="button" class="btn footer-button" @click="closeModal">
          <img :src="cancelImage" alt="취소" />
          <span class="button-text">아니요</span>
        </button>
        <button type="button" class="btn footer-button" @click="confirmKick">
          <img :src="okImage" alt="추방" />
          <span class="button-text">예</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { kickUserFromRoom } from '@/common/api/RoomsAPI'; // API 호출 함수 가져오기
import scheduleModal from '@/assets/images/room/calendar_modal/background.png';
import titleImage from '@/assets/images/room/calendar_modal/title.png';
import okImage from '@/assets/images/room/calendar_modal/ok.png';
import cancelImage from '@/assets/images/room/calendar_modal/cancel.png';

// props 정의
const props = defineProps({
  user: {
    type: Object,
    required: true,
  },
  roomId: {
    type: [String, Number],
    required: true,
  },
  memberId: {
    type: [Number],
    required: true,
  }
});

// emits 정의
const emit = defineEmits(['close', 'kick']);

// 모달 닫는 함수
const closeModal = () => {
  emit('close');
};

// 유저 퇴장 확인 및 DELETE 요청 전송 함수
const confirmKick = async () => {
  console.log('Room ID:', props.roomId);  // roomId가 undefined인지 확인
  console.log('User ID:', props.memberId);  // user ID도 확인

  try {
    const response = await kickUserFromRoom(props.roomId, props.memberId);
    if (response.statusCode === 200 || response.status === 'success') {
      emit('kick', props.memberId);
    } else {
      console.error('유저 퇴장 실패:', response);
    }
  } catch (error) {
    console.error('DELETE 요청 중 오류 발생:', error);
  } finally {
    closeModal();
  }
};


// 모달 스타일
const modalStyle = computed(() => ({
  backgroundImage: `url(${scheduleModal})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundRepeat: 'no-repeat',
}));
</script>

<style scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
}

.modal-content {
  background-color: #291707;
  border-radius: 10px;
  padding: 0;
  width: 500px;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.custom-modal-header {
  position: relative;
  padding: 0;
  margin: 0;
  border-bottom: none;
}

.modal-title-image {
  width: 100%;
  height: auto;
  display: block;
  margin-top: 0; 
  border-bottom: none; 
}

.overlay-text {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #d9b992;
  font-size: 1.3em;
  font-weight: bold;
}

.modal-body {
  margin: 0;
  padding: 30px 0;
  color: #e0e0e0;
  font-size: 1.1em;
  border-top: none; 
  border-bottom: none; 
}

.custom-modal-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 15px;
  margin: 0; 
  border-top: none; 
}

.footer-button {
  position: relative;
  margin: 0 10px;
  background-color: transparent;
  border: none;
  cursor: pointer;
  font-size: 14px;
  color: white;
  transition: transform 0.3s;
}

.footer-button:hover {
  transform: scale(1.05);
}

.footer-button img {
  display: block;
  width: 100%;
  height: 100%;
}

.button-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #d9b992;
  font-size: 1.2em;
  font-weight: bold;
  pointer-events: none;
}
</style>
