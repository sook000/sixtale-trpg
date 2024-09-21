<template>
  <div class="video-profile">
    <div class="profile-cards">
      <!-- 방에 있는 사용자 데이터를 기반으로 프로필 카드 생성 -->
      <div v-for="user in allUsers" :key="user.playMemberID || user.placeholder">
        <div class="profile-card">
          <div class="profile-image">
            <img
              :src="user.playMemberImageURL || require('@/assets/images/user.png')"
              :alt="'User ' + user.playMemberNickname + ' profile picture'"
            />
            <video
              v-if="!user.placeholder"
              :id="'video-' + user.playMemberID"
              autoplay
              playsinline
              ref="videoElements"
            ></video>
          </div>
          <div class="profile-info">
            <h3 class="usernickname">{{ user.playMemberNickname || '임시 사용자' }}</h3>
            <VoiceChatButton v-if="!user.placeholder" :userId="user.playMemberID" />
            <span v-if="user.placeholder">임시 카드</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getRoomInfo } from '@/common/api/RoomsAPI.js';
import VoiceChatButton from '../../VoiceChatButton.vue';

// 임시 카드 이미지 경로 설정
const placeholderImage = require('@/assets/images/users/default.png');

const route = useRoute();
const roomID = ref(Number(route.params.roomId));
const playMemberList = ref([]);
const videoElements = ref([]);

const fetchRoomInfo = async () => {
  if (!roomID.value) {
    console.error('Room ID가 설정되지 않았습니다.');
    return;
  }

  try {
    const response = await getRoomInfo(roomID.value);
    playMemberList.value = response.playMemberList;
  } catch (error) {
    console.error('Error fetching room info:', error);
  }
};

// 플레이어 카드 생성 및 빈 자리 임시 카드로 채우기
const allUsers = computed(() => {
  const maxCards = 8;
  const placeholders = maxCards - playMemberList.value.length;
  
  const placeholderCards = Array.from({ length: placeholders }, (_, index) => ({
    placeholder: `placeholder-${index + 1}`,
    playMemberNickname: '임시 사용자',
    playMemberImageURL: placeholderImage,  // 임시 카드 이미지 적용
  }));
  
  return [...playMemberList.value, ...placeholderCards];
});

onMounted(() => {
  fetchRoomInfo();
});
</script>


<style scoped>
.video-profile {
  color: white;
  border-radius: 10px;
  display: flex;
  height: 100%;
}

.profile-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(110px, 1fr));
  gap: 10px;
  width: 100%;
}

.profile-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: rgba(83, 78, 78, 0.7); /* 프로필 카드 배경색 추가 */
  border-radius: 10px; /* 프로필 카드 모서리 둥글게 */
  overflow: hidden; /* 프로필 카드 경계 밖으로 나가는 내용 숨기기 */
  position: relative;
  z-index: 10;
  max-width: 150px; /* 카드 최대 너비 */
}

.profile-image {
  width: 100%;
  height: 0;
  padding-bottom: 100%; /* 정사각형 비율 유지 */
  position: relative;
  overflow: hidden;
}

.profile-image img,
.profile-image video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px;
  background-color: rgba(83, 78, 78, 0.5);
  width: 100%;
  box-sizing: border-box;
}

.profile-info h3 {
  margin: 0;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
