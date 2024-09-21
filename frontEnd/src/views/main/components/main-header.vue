<template>
  <header class="main-header" :style="{ height: height }">
    <div class="logo" @click="goHome">
      <img src="@/assets/images/SixtaleLogo.png" alt="Site Logo" class="logo-image" />
      <img src="@/assets/images/SixtaleTitle.png" alt="Site Title" class="title-image" />
    </div>
    <nav class="nav-menu">
      <ul>
        <li><router-link to="/rulebook">룰북</router-link></li>
        <li><router-link to="/scenarios">시나리오</router-link></li>
        <li><router-link to="/find-players">구인광장</router-link></li>
        <li><router-link to="/lobby">로비</router-link></li>
      </ul>
    </nav>
    <div class="profile">
      <img src="@/assets/images/user.png" alt="Profile Image" class="profile-image" />
      <div v-if="isLoggedIn" class="dropdown">
        <button
          class="btn dropdown-toggle custom-dropdown-btn"
          type="button"
          data-bs-toggle="dropdown"
          aria-expanded="false"
        ></button>
        <ul class="dropdown-menu custom-dropdown-menu">
          <li>
            <a class="dropdown-item custom-dropdown-item" href="#" @click="doLogout">로그아웃</a>
          </li>
          <li><hr class="dropdown-divider custom-divider" /></li>
          <li>
            <a class="dropdown-item custom-dropdown-item" href="#" @click="doDeleteAccount"
              >회원탈퇴</a
            >
          </li>
          <li><hr class="dropdown-divider custom-divider" /></li>
          <li>
            <router-link to="/mypage"
              ><a class="dropdown-item custom-dropdown-item">마이페이지</a></router-link
            >
          </li>
        </ul>
      </div>
      <div v-else class="account" @click="openLoginModal">로그인</div>
    </div>
    <SocialLoginModal v-model="isModalOpen" />
  </header>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { useAccountApi } from "@/common/api/accountAPI.js"; // 경로가 올바른지 확인
import Cookies from "js-cookie";
import SocialLoginModal from "./SocialLoginModal.vue";

onMounted(() => {
  checkLoginStatus();
});

// Props
const props = defineProps({
  height: {
    type: String,
    default: "70px",
  },
});

// Router instance
const router = useRouter();

// State variables
const isModalOpen = ref(false);
const isLoggedIn = ref(false);

// Methods
const goHome = () => {
  router.push("/");
};

const openLoginModal = () => {
  isModalOpen.value = true;
};

const checkLoginStatus = () => {
  const token = Cookies.get("access-token");
  isLoggedIn.value = token != null;
};

const doLogout = async () => {
  await logout();
  checkLoginStatus();
};

const doDeleteAccount = async () => {
  await deleteAccount();
  checkLoginStatus();
};

// Account API methods
const { logout, deleteAccount } = useAccountApi(router);
</script>

<style scoped>
.main-header {
  display: flex;
  align-items: center;
  padding: 0 20px;
  width: 97% !important;
  background: rgba(30, 30, 30, 0.88);
  border-radius: 15px;
  margin-left: 1.5%;
  color: white;
  top: 3% !important;
}

.logo {
  cursor: pointer;
  display: flex;
  object-fit: cover;
  align-items: center;
}

.logo-image {
  height: 60px; /* 로고 이미지 높이 설정 */
  max-width: 100px; /* 로고 이미지 최대 너비 설정 */
  object-fit: contain;
  margin-right: 5px;
}

.title-image {
  height: 65px; /* 타이틀 이미지 높이 설정 */
  max-width: 200px; /* 타이틀 이미지 최대 너비 설정 */
  object-fit: contain;
}

.nav-menu ul {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-menu li {
  margin: 0 15px;
}

.nav-menu a {
  color: white;
/* font-family: 'Abhaya Libre ExtraBold'; */
font-weight: 800;
  text-decoration: none;
}

.profile {
  margin-left: auto;
  display: flex;
  align-items: center;
  position: relative;
}

.profile-image {
  height: 30px;
  width: 35px;
  object-fit: contain;
  border-radius: 50%;
  cursor: pointer;
}

.account {
  color: white;
  padding: 5px;
  cursor: pointer;
  font-weight: 800;
}

.dropdown {
  margin-left: 10px;
}

.custom-divider {
  border-top: 1px solid white; /* 구분선 색상 변경 */
  margin: 0.5rem 0; /* 구분선 위아래 여백 조정 */
}

.custom-dropdown-btn {
  background-color: #5b7591;
  color: white;
}

.custom-dropdown-btn:hover,
.custom-dropdown-btn:focus {
  background-color: #0056b3;
  color: white;
}

.custom-dropdown-menu {
  background-color: #333;
  border-color: #333;
}

.custom-dropdown-item {
  color: white;
}

.custom-dropdown-item:hover,
.custom-dropdown-item:focus {
  background-color: #555;
  color: white;
}
</style>



