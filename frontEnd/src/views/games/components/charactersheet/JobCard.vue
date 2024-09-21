<template>
  <div class="job-card" @mouseover="hover = true" @mouseleave="hover = false">
    <div class="job-card-inner">
      <div class="card-content">
        <div class="card-border" :style="borderStyle"></div>
        <img :src="image" alt="Job Image" class="job-image" />
        <!-- <h2 class="job-name">{{ name }}</h2> -->
      </div>
      <div class="hover-name">
        <h2>{{ name }}</h2>
        <div v-if="disabled">
          <p>선택 완료</p>
          <button @click="showDeleteModal" :style="deleteButtonStyle">삭제하기</button>
        </div>
        <div v-else>
          <p>{{ description }}</p>
          <button @click="selectCard" :style="buttonStyle">직업 선택</button>
        </div>
      </div>
    </div>
    <CharacterSheetDeleteModal
      v-if="isDeleteModalVisible"
      :roomId="Number(roomId)" 
      @confirm="deleteCard"
      @close="hideDeleteModal"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import borderImage from '@/assets/images/character/character_border.png';
import saveButtonImage from '@/assets/images/maps/background/save.png';
import startButtonImagePath from '@/assets/images/room/start_button.png';
import CharacterSheetDeleteModal from '@/views/games/components/Modal/CharacterSheetDeleteModal.vue';

const props = defineProps({
  name: {
    type: String,
    required: true
  },
  image: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  selectedBy: {
    type: String,
    default: ''
  },
  generatedImages: {
    type: Array,
    default: () => []
  },
  selectedImage: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false // 비활성화 상태를 나타내는 속성 추가
  },
  roomId: {
    type: Number,
    required: true,  // roomId를 필수로 설정
  }
});

const emit = defineEmits(['select-card', 'delete-card']);

const hover = ref(false);
const isDeleteModalVisible = ref(false);

const borderStyle = computed(() => ({
  // backgroundImage: `url(${borderImage})`,
  backgroundSize: 'cover',
  backgroundRepeat: 'no-repeat',
  zIndex: 1,
  color: '#e6ddc4',
  position: 'absolute',
  top: '0',
  left: '0',
  right: '0',
  bottom: '0'
}));

const buttonStyle = computed(() => ({
  background: `url(${saveButtonImage}) no-repeat center center`,
  backgroundSize: 'cover',
  color: '#e6ddc4',
  padding: '10px 20px',
  border: 'none',
  borderRadius: '5px',
  cursor: 'pointer',
  textAlign: 'center'
}));

const deleteButtonStyle = computed(() => ({
  background: `url(${startButtonImagePath}) no-repeat center center`,
  backgroundSize: 'cover',
  color: '#e6ddc4',
  padding: '10px 20px',
  border: 'none',
  borderRadius: '5px',
  cursor: 'pointer',
  textAlign: 'center',
  transition: 'background-color 0.3s',
}));

const selectCard = () => emit('select-card', props.name);
const showDeleteModal = () => isDeleteModalVisible.value = true;
const hideDeleteModal = () => isDeleteModalVisible.value = false;
const deleteCard = () => {
  emit('delete-card', props.name);
  hideDeleteModal();
};
</script>

<style scoped>
.job-card {
  perspective: 1000px;
  background-color: transparent;
  width: 100%;
  height: 100%;
  position: relative;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.job-card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  text-align: center;
  transition: transform 0.6s;
  transform-style: preserve-3d;
}

.job-card:hover .job-card-inner {
  transform: rotateY(180deg);
}



.card-content, .hover-name {
  position: absolute;
  width: 100%;
  height: 100%;
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
  border-radius: 5px;
  overflow: hidden;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.3);
}

.card-content {
  background-color: #4A3A2E;
  color: #e6ddc4;
  display: flex;
  flex-direction: column;
  justify-content: center; /* 세로 중앙 정렬 */
  align-items: center; /* 가로 중앙 정렬 */
  padding: 10px;
  z-index: 2;

}

/* .card-content.hidden {
  opacity: 0;
} */

.card-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  background-repeat: no-repeat;
  z-index: 1;
}

.hover-name {
  background-color: #19120c; /* 뒷면 배경색 변경 */
  color: #ECF0F1; /* 텍스트 색상 변경 */
  transform: rotateY(180deg);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 10px;
  border: 8px solid #4A3A2E; /* 보더 색상 추가 */
  border-radius: 5px; /* 보더 둥글게 */
}
.hover-name h2 {
  margin: 10px 0;
  font-size: 1.2rem;
  color: #e1d3a8; /* 제목 색상 변경 */
}

.hover-name p {
  font-size: 0.9rem;
  margin: 10px 0;
  max-height: 50%;
  overflow-y: auto;
  padding: 0 10px;
  color: #e1d3a8; /* 설명 텍스트 색상 */
}

.card-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  background-repeat: no-repeat;
  z-index: 0;
}

.job-name {
  margin: 10px 0;
  font-size: 1.2rem;
  font-weight: bold;
  z-index: 2;
}

.job-image {
  width: 100%;
  height: auto;
  object-fit: cover;
  border-radius: 5px;
  z-index: 2;
  justify-content: center;
}

.hover-name h2 {
  margin: 10px 0;
  font-size: 1.2rem;
}

.hover-name p {
  font-size: 0.9rem;
  margin: 10px 0;
  max-height: 50%;
  overflow-y: auto;
  padding: 0 10px;
}

button {
  margin-top: 10px;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
  color: #e1d3a8;
  background-color: #007bff;
  z-index: 2;
}

button:hover {
  background-color: #0056b3;
  color: #e1d3a8;
}
</style>