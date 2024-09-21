<template>
  <div class="video-profile">
    <div class="sidebar">
      <button class="icon-button" @click="toggleAIImages">
        <img src="@/assets/images/ingame/Person.png" alt="AI 이미지" />
      </button>
      <button class="icon-button" @click="toggleStats">
        <img src="@/assets/images/ingame/Status.png" alt="캐릭터 스탯" />
      </button>
    </div>
    <div class="profile-cards">
      <div v-if="users.length === 0">No users available</div>
      <div class="profile-card" v-for="user in users" :key="user.playMemberID">
        <div class="profile-image">
          <img
            v-if="showAIImages"
            :src="user.imageURL || require('@/assets/images/user.png')"
            :alt="'User ' + user.playMemberNickname + ' AI 프로필 사진'"
            @click="fetchUserJob(user.playMemberID)"
          />
          <CharacterStats
            v-else-if="showStats"
            :characterName="user.name"
            :level="user.level"
            :job="user.jobName"
            :stats="[
              { name: '경험치', value: user.exp },
              { name: 'HP', value: user.currentHp },
              { name: '근력', value: getStatValue(user.stat, 1) },
              { name: '지능', value: getStatValue(user.stat, 2) },
              { name: '체력', value: getStatValue(user.stat, 3) },
              { name: '지혜', value: getStatValue(user.stat, 4) },
              { name: '민첩성', value: getStatValue(user.stat, 5) },
              { name: '매력', value: getStatValue(user.stat, 6) }
            ]"
          />
          <video
            v-show="!showAIImages && !showStats"
            :id="'video-' + user.playMemberID"
            autoplay
            playsinline
            ref="videoElements"
          ></video>
        </div>
        <div class="profile-info">
          <h3 class="usernickname">{{ user.playMemberNickname }}</h3>
          <div class="user-actions">
            <span v-if="isGM" class="user-info-icon">정보</span>
            <img
              v-if="isGM"
              src="@/assets/images/ingame/Info.png"
              alt="정보"
              class="info-icon"
              @click="showUserInfo(user.playMemberID)" 
            />
            <VoiceChatButton :userId="user.playMemberID" class="voicebutton" />
          </div>
        </div>
      </div>
    </div>
    <!-- 모달 컴포넌트 -->
    <CharacterInfo
      v-if="showCharacterInfo"
      :roomID="roomID" 
      :playMemberID="selectedCharacterData.playMemberID" 
      :formData="selectedCharacterData" 
      @close="closeCharacterInfo" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getCharacterSheet } from '@/common/api/CharacterSheetAPI.js';
import { getRoomInfo } from '@/common/api/RoomsAPI.js';
import CharacterStats from './CharacterStats.vue';
import CharacterInfo from '@/views/games/components/ingame/CharacterInfoModal.vue';
import VoiceChatButton from '../../VoiceChatButton.vue';
import { selectedPlayMemberID, selectedUserJobID } from '@/store/state.js'; 

// roomID를 route.params.roomId에서 가져옵니다.
const route = useRoute();
const roomID = ref(Number(route.params.roomId));

const showStats = ref(false);
const showAIImages = ref(true);
const showCharacterInfo = ref(false);
const isGM = ref(true);
const users = ref([]);
const videoElements = ref([]);
const selectedCharacterData = ref(null);

const fetchCharacterSheets = async () => {
  if (!roomID.value) {
    console.error('Room ID가 설정되지 않았습니다.');
    return;
  }

  try {
    const roomInfo = await getRoomInfo(roomID.value);
    if (roomInfo && Array.isArray(roomInfo.playMemberList)) {
      users.value = [];
      for (let playMember of roomInfo.playMemberList) {
        try {
          const characterSheet = await getCharacterSheet(roomID.value, playMember.playMemberID);
          if (characterSheet) {
            users.value.push({ ...playMember, ...characterSheet, playMemberID: playMember.playMemberID });
          } else {
            users.value.push({ ...playMember, playMemberID: playMember.playMemberID }); 
          }
        } catch (fetchError) {
          users.value.push({ ...playMember, playMemberID: playMember.playMemberID });
        }
      }
    }
  } catch (error) {
    console.error('Error fetching character sheets:', error);
  }
};

// 새로운 fetchCharacterData 함수 추가
const fetchCharacterData = async (playMemberID) => {
  try {
    const characterSheet = await getCharacterSheet(roomID.value, playMemberID);
    if (characterSheet) {
      selectedCharacterData.value = { ...characterSheet, playMemberID: playMemberID };
      console.log('Character data updated in fetchCharacterData:', selectedCharacterData.value);
    } else {
      console.error('Character data not found for playMemberID:', playMemberID);
    }
  } catch (error) {
    console.error('Error fetching character sheet:', error);
  }
};

const fetchUserJob = async (playMemberID) => {
  selectedPlayMemberID.value = playMemberID;

  try {
    const characterSheet = await getCharacterSheet(roomID.value, playMemberID);
    selectedUserJobID.value = characterSheet.jobId;
  } catch (error) {
    console.error('사용자 직업 정보를 가져오는 중 오류 발생:', error);
  }
};

const toggleStats = () => {
  showAIImages.value = false;
  showStats.value = true;
};

const toggleAIImages = () => {
  showAIImages.value = true;
  showStats.value = false;
};

const showUserInfo = async (playMemberID) => {
  await fetchCharacterData(playMemberID);  // 최신 데이터를 가져옴
  showCharacterInfo.value = true;
};

const closeCharacterInfo = async () => {
  showCharacterInfo.value = false;
  await fetchCharacterSheets();  // 모달이 닫힐 때 최신 데이터를 다시 가져옴
};

const getStatValue = (statsArray, statID) => {
  const stat = statsArray.find(stat => stat.statID === statID);
  return stat ? stat.statValue : 0;
};

onMounted(() => {
  fetchCharacterSheets();
});
</script>


<style scoped>
/* 스타일 부분은 그대로 유지 */
.video-profile {
  color: white;
  padding: 0;
  display: flex;
  height: 100%;
}

.sidebar {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  padding: 10px 0;
  width: 40px;
}

.icon-button {
  background: none;
  border: none;
  cursor: pointer;
  width: 100%;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.icon-button img {
  width: 30px;
  height: 30px;
}

.profile-cards {
  display: flex;
  justify-content: space-between;
  width: calc(100% - 40px);
  margin-left: 0;
}

.profile-card {
  width: calc(12.5% - 10px);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

.profile-image {
  width: 100%;
  height: 100%;
  position: relative;
}

.profile-image img,
.profile-image video,
.character-stats {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.character-stats .stats-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  object-fit: cover;
}

.profile-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: rgba(83, 78, 78, 0.5);
  overflow: hidden;
}

.user-info {
  display: flex;
  align-items: center;
  flex-shrink: 1;
  min-width: 0;
  margin-right: 10px;
}

.usernickname {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 70%;
  font-size: 12px;
}

.user-info-icon {
  margin-left: 0;
  margin-right: 0;
  flex-shrink: 0;
}

.info-icon {
  width: 15px;
  height: 15px;
  margin-right: 0;
  cursor: pointer;
}

.profile-info h3 {
  margin: 0;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.voicebuton {
  margin-left: 0;
}
</style>
