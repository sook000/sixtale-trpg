<template>
  <div :style="mapViewStyle" class="map-view">
    <!-- GM만 맵 전환 버튼을 볼 수 있게 설정 -->
    <div class="map-controls" v-if="nickName === gmName">
      <img :src="previousIcon" alt="Previous" @click="previousMap" style="width: 40px; height: auto;" />
      <img :src="nextIcon" alt="Next" @click="nextMap" style="width: 40px; height: auto;" />
    </div>
    <!-- 플레이어는 첫 번째 맵만 볼 수 있게 설정 -->
    <img :src="currentImage" alt="Map or Game" class="map-image" />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { getMemberInfo } from "@/common/api/mypageAPI";

// Props 받아오기
const props = defineProps({
  roomId: {
    type: String,
    required: true,
  },
  mapList: {
    type: Array,
    required: true,
  },
  gmName: {
    type: String,
    required: true,
  }
});

const previousIcon = require('@/assets/images/room/previous.png');
const nextIcon = require('@/assets/images/room/next.png');

// 현재 사용자 닉네임을 가져오기 위한 변수
const nickName = ref("");

onMounted(() => {
  getMemberInfo()
    .then((response) => {
      nickName.value = response.data.data.nickName;
      console.log('User Nickname:', nickName.value);
    })
    .catch((error) => {
      console.error("Failed to fetch member info:", error);
    });

  // 컴포넌트가 마운트될 때 맵 목록 리스트를 콘솔에 출력
  console.log('Initial Map List:', props.mapList);
});

// 상태 변수 정의
const currentMap = ref(null);
let currentMapIndex = 0;

// MapList에 따라 currentMap 설정 및 맵 목록 출력
watch(
  () => props.mapList,
  (newMapList) => {
    if (newMapList && newMapList.length > 0) {
      currentMap.value = newMapList[0];
      console.log('Updated Map List:', newMapList);
    }
  },
  { immediate: true }
);

// GM만 맵 전환 기능 사용 가능
const previousMap = () => {
  if (nickName.value === props.gmName) {
    if (currentMapIndex > 0) {
      currentMapIndex -= 1;
    } else {
      currentMapIndex = props.mapList.length - 1;
    }
    currentMap.value = props.mapList[currentMapIndex];
  }
};

const nextMap = () => {
  if (nickName.value === props.gmName) {
    if (currentMapIndex < props.mapList.length - 1) {
      currentMapIndex += 1;
    } else {
      currentMapIndex = 0;
    }
    currentMap.value = props.mapList[currentMapIndex];
  }
};

// 배경 이미지 스타일 설정
const mapViewStyle = computed(() => ({
  position: 'relative',
  height: '80%',
  backgroundImage: currentMap.value && currentMap.value.bgmURL ? `url(${currentMap.value.bgmURL})` : '',
  backgroundColor: 'transparent',
  borderRadius: '10px',
  width: '70%',
  boxSizing: 'border-box',
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundRepeat: 'no-repeat',
}));

const currentImage = computed(() => {
  console.log('Current Map Image URL:', currentMap.value ? currentMap.value.imageURL : 'No Map Selected');
  return currentMap.value ? currentMap.value.imageURL : require('@/assets/images/room/gameImage.png');
});

</script>

<style scoped>
.map-view {
  position: relative;
  height: 100%; 
  border-radius: 10px;
  width: 100%; 
  box-sizing: border-box; 
  border: 3px solid #4A3A2E;
}

.map-controls {
  display: flex;
  justify-content: space-between;
  position: absolute;
  top: 47%; 
  left: 20px; 
  right: 20px; 
  transform: translateY(-50%);
}

.map-controls img {
  width: 40px; 
  height: auto;
  cursor: pointer;
}

.map-image {
  width: 100%;
  height: 100%;
  border-radius: 30px;
  /* padding: 15px; */
}
</style>