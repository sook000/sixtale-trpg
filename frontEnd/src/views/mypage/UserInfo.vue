<template>
  <div><h3 class="menu-title">정보 조회/수정</h3></div>
  <!-- 정보 조회 페이지 -->
  <div class="content-box right-box">
    <div class="profile-header">
      <img
        :src="profileImage || require('@/assets/images/mypage/user.png')"
        alt="Profile Image"
        class="profile-avater"
      />
      <div class="profile-info">
        <h3 class="profile-nickname">{{ user.nickName }}</h3>
        <p class="profile-join-date">{{ formatJoinDate(user.createdAt) }} 가입</p>
      </div>
    </div>
    <hr class="divider" />
    <div class="profile-details">
      <div class="profile-detail-item">
        <strong>선호 룰</strong>
        <p>{{ user.preferredRules }}</p>
      </div>
      <div class="profile-detail-item">
        <strong>선호 장르</strong>
        <p>{{ user.preferredGenres }}</p>
      </div>
    </div>
    <div class="profile-actions">
      <button class="profile-button" @click="goToEditPage">정보수정</button>
      <button class="profile-button danger">회원탈퇴</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getMemberInfo, getMemberDetailInfo } from "@/common/api/mypageAPI";
const router = useRouter();
const user = ref({});
const userDetail = ref({});

onMounted(() => {
  //회원 기본 정보
  getMemberInfo()
    .then((response) => {
      console.log(response.data.data);
      user.value = response.data.data;
    })
    .catch((error) => {
      console.error("Failed to fetch member info:", error);
    });
  //회원 상세 정보
  getMemberDetailInfo()
    .then((response) => {
      console.log(response.data.data);
      userDetail.value = response.data.data;
    })
    .catch((error) => {
      console.error("Failed to fetch member Detail info:", error);
    });
});

// createdAt 배열 포맷팅
const formatJoinDate = (createdAtArray) => {
  if (!createdAtArray || createdAtArray.length < 3) {
    return ""; // createdAt이 배열이 아니거나, 데이터가 충분하지 않으면 빈 문자열 반환
  }
  const [year, month, day] = createdAtArray;
  return `${year}.${String(month).padStart(2, "0")}.${String(day).padStart(2, "0")}`;
};

// 정보 수정 페이지로 이동
const goToEditPage = () => {
  router.push({ name: "UserEdit" });
};
</script>

<style scoped>
.menu-title {
  color: white;
  margin: 0 0 20px 20px;
  text-align: left;


}
.content-box {
  background-color: #3a3a3c;
  border-radius: 18px;
  position: relative;
}
.left-box {
  height: 750x;
  padding: 20px;
  border: 1px solid #3a3a3c;
}
.right-box {
  height: 695px; /*왼쪽 박스와 높이 끝 같게 */
  padding: 80px;
  border: 1px solid #3a3a3c;
}
.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
}

.profile-avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 4px solid #ffffff;
  margin-right: 50px;
}
.profile-info {
  flex-grow: 1;
  padding: 50px;
}
.profile-nickname {
  color: #ffffff;
  font-size: 1.8rem;
  margin-bottom: 20px;
}
.profile-join-date {
  color: #bfbfc0;
  margin: 5px 0 0;
}
.divider {
  border: 1px solid #ffffff;
  margin: 60px 0;
}
.profile-details {
  margin-bottom: 50px;
}
.profile-detail-item {
  margin-bottom: 40px;
}
.profile-detail-item strong {
  color: #ffffff;
  font-size: 1.2rem;
  display: block;
  margin-bottom: 5px;
}
.profile-detail-item p {
  color: #bfbfc0;
  margin: 0;
}
.profile-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  position: absolute;
  right: 20px;
  bottom: 20px;
}
.profile-button {
  color: white;
  background-color: #3a3a3c;
  border: none;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1rem;
  text-decoration: none;
}
.profile-button.danger {
  background-color: #3a3a3c;
}
.profile-button:hover {
  text-decoration: underline;
  font-weight: bold;
}
</style>
