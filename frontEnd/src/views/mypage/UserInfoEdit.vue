<template>
  <div><h3 class="menu-title">정보 조회/수정</h3></div>
  <!-- 정보 조회 페이지 -->
  <div class="content-box right-box">
    <div class="profile-header">
      <div class="profile-image-container">
        <img
          :src="previewImage || profileImage || require('@/assets/images/mypage/user.png')"
          alt="Profile Image"
          class="profile-avatar"
        />
        <label for="file-input" class="custom-file-label">파일 선택</label>
        <input type="file" id="file-input" multiple @change="handleFileChange" />
      </div>
      <div class="profile-info">
        <input
          type="text"
          v-model="nickName"
          :placeholder="user.nickName"
          class="profile-nickname"
        />
        <p class="profile-join-date">{{ formatJoinDate(user.createdAt) }} 가입</p>
      </div>
    </div>
    <hr class="divider" />
    <div class="profile-details">
      <div class="profile-detail-item">
        <strong  class="detail-label">선호 룰</strong>
        <input
          type="text"
          v-model="user.preferredRules"
          :placeholder="user.preferredRules"
          class="profile-nickname detail-input"
        />
      </div>
      <div class="profile-detail-item ">
        <strong>선호 장르</strong>
        <p>{{ user.preferredGenres }}</p>
      </div>
    </div>
    <div class="profile-actions">
      <button class="profile-button" @click="saveChanges">저장</button>
      <button class="profile-button danger" @click="cancelEditing">취소</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getMemberInfo, getMemberDetailInfo, updateMemberInfo } from "@/common/api/mypageAPI";
const router = useRouter();
const user = ref({});
const userDetail = ref({});

const nickName = ref(""); // 수정 닉네임 정보
const previewImage = ref(""); // 새로운 이미지 미리보기
const profileImage = ref("");

// const genres = ["", "판타지", "모험", "드라마", "호러", "스릴러", "현대", "중세", 
// 8	활극
// 9	SF
// 10	히어로
// 11	추리수사

onMounted(() => {
  //회원 기본 정보
  getMemberInfo()
    .then((response) => {
      console.log(response.data.data);
      user.value = response.data.data;
      nickName.value = user.value.nickName;
      profileImage.value = user.value.imageURL;
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
    // 장르 조회

});

// createdAt 배열 포맷팅
const formatJoinDate = (createdAtArray) => {
  if (!createdAtArray || createdAtArray.length < 3) {
    return ""; // createdAt이 배열이 아니거나, 데이터가 충분하지 않으면 빈 문자열 반환
  }
  const [year, month, day] = createdAtArray;
  return `${year}.${String(month).padStart(2, "0")}.${String(day).padStart(2, "0")}`;
};

// 수정 파일 정보
const selectedFiles = ref([]);
const handleFileChange = (event) => {
  const files = event.target.files;
  if (files.length > 0) {
    selectedFiles.value = files;
    previewImage.value = URL.createObjectURL(files[0]);
  }
};

const saveChanges = () => {
  //회원 기본 정보 저장
  updateMemberInfo(nickName.value, selectedFiles.value)
    .then((response) => {
      console.log("User info updated successfully");
      router.push({ name: "UserInfo" });
    })
    .catch((error) => {
      console.error("Failed to update user info:", error);
    });
};

const cancelEditing = () => {
  router.push({ name: "UserInfo" });
};
</script>

<style scoped>
.menu-title {
  color: white;
  margin: 0 0 20px 20px;
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
.profile-image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: fit-content;  /* 부모 요소의 너비를 전체로 설정 */
  height: auto;  /* 부모 요소의 높이를 자동으로 설정 */
  text-align: center;  /* 텍스트 정렬을 중앙으로 설정 */
}
/* 파일 입력 요소 숨기기 */
input[type="file"] {
  display: none;
}
/* 커스텀 레이블 스타일링 */
.custom-file-label {
  display: inline-block;
  padding: 5px 10px;
  color: #fff;
  background-color: #474646;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
    text-align: center;

}
.custom-file-label:hover {
  background-color: #28221E;
}

.profile-avatar  {
  display: block;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  border: 4px solid #ffffff95;
  margin-bottom: 10px;
  object-fit: cover;
  max-width: 200px; /* 최대 너비 제한 */
  max-height: 200px; /* 최대 높이 제한 */
}
.profile-info {
  flex-grow: 1;
  padding: 50px;
}
.profile-nickname {
  color: #ffffff;
  font-size: 1.8rem;
  margin-bottom: 20px;
  background-color: #28221E;
  border: none;
  border-radius: 10px;
  padding: 5px 10px;
  width: 100%;
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
  display: flex;
  margin-bottom: 40px;
  align-items: center; /* 세로 가운데 정렬 */
  gap: 70px; /* 요소 간의 간격 */
  width:1210px;


}
.detail-label {
  flex-shrink: 0; /* strong 요소가 축소되지 않도록 함 */
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
  border-radius: 20%;
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
