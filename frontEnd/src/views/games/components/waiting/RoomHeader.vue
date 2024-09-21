<template>
  <div class="room-header">
    <div class="left-section">
      <div class="back-button" @click="goBack">
        <img src="@/assets/images/room/arrow.png" alt="Back" />
        <span class="room-title">{{ roomTitle }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { leaveRoom } from "@/common/api/RoomsAPI";
import { getMemberInfo } from "@/common/api/mypageAPI"; // 사용자 정보 가져오기 API
import { getRoomInfo } from "@/common/api/RoomsAPI";
import WebSocketService from "@/store/websocket/waiting"; // WebSocket 서비스 가져오기

const props = defineProps({
  roomTitle: String,
  nextSchedule: String,
});

const router = useRouter();
const route = useRoute();

const roomId = route.params.roomId;
const room = ref({});
const userId = ref(0);
const goBack = async () => {
  try {
    room.value = await getRoomInfo(roomId);
    await getMemberInfo()
      .then((response) => {
        console.log(response.data.data);
        userId.value = response.data.data.id;
      })
      .catch((error) => {
        console.error("Failed to fetch member info:", error);
      });

    console.log("userId", userId.value);
    console.log("room", room.value.gmID);
    if (userId.value !== room.value.gmID) {
      await leaveRoom(roomId); // 방에서 사용자 나가기 API 호출
    } else {
      WebSocketService.disconnect();
    }
    router.push({ name: "Lobby" }); // 로비로 이동
  } catch (error) {
    console.error("Failed to leave the room:", error);
    // 추가적인 오류 처리 가능
  }
};

const createSchedule = () => {
  console.log("Creating new schedule");
};
</script>

<style scoped>
/* 스타일 코드는 그대로 유지 */
.room-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  align-items: center;
  background-color: #081017;
  color: white;
  padding: 0 20px;
  box-sizing: border-box;
  height: 8%;
}

.left-section {
  display: flex;
  align-items: center;
}

.back-button {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.back-button img {
  width: 30px;
  height: 30px;
  margin-right: 10px;
}

.room-title {
  font-size: 1.5em;
}

.right-section {
  display: flex;
  justify-content: align;
  align-items: center;
  gap: 10px;
}

.next-schedule-box {
  display: flex;
  align-items: center;
  background-color: #564307;
  border: 1px solid #ccc;
  padding: 5px 10px;
  border-radius: 5px;
  color: white;
}

.next-schedule {
  margin-right: 10px;
}

.create-button {
  padding: 5px 10px;
  background-color: #564307;
  color: white; /* 하얀 글씨 */
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>
