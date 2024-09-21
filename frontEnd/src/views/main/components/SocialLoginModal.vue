<!-- <template>
  <div v-if="modelValue" class="modal" @click.self="closeModal">
    <div class="modal-content">
      <span class="close-btn" @click="closeModal">&times;</span>
      <h2 class="modal-title">소셜 로그인</h2>
      <button class="social-btn" @click="googleLogin">Google 로그인</button>
      <button class="social-btn" @click="naverLogin">Naver 로그인</button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";

const props = defineProps({
  modelValue: Boolean,
});

const emit = defineEmits(["update:modelValue"]);

const closeModal = () => {
  emit("update:modelValue", false);
};

const googleLogin = async () => {
  const clientId =
    "288654374576-oakoiq7biqtduolbs3rfgef4cb30umr1.apps.googleusercontent.com";
  const redirectUri = "https://i11d108.p.ssafy.io/api/v1/members/auth/login/google";
  const responseType = "code";
  const scope = "email profile";

  const url = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}&scope=${scope}`;

  window.location.href = url;
};

const naverLogin = async () => {
  const clientId = "jCJ25DM0P7fbySK5L9Fd";
  const redirectUri = "https://i11d108.p.ssafy.io/api/v1/members/auth/login/naver";
  const responseType = "code";
  // const scope = 'email profile_image'

  const url = `https://nid.naver.com/oauth2.0/authorize?response_type=${responseType}&client_id=${clientId}&redirect_uri=${redirectUri}`;

  window.location.href = url;
};
</script>

<style scoped>
.modal {
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.7);
}

.modal-content {
  background-color: #1c1c1c;
  color: #ffffff;
  padding: 30px;
  border: 2px solid #888;
  width: 400px;
  text-align: center;
  box-shadow: 0px 0px 10px #000000;
  position: relative;
}

.modal-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}

.close-btn {
  color: #ffffff;
  position: absolute;
  top: 10px;
  right: 20px;
  font-size: 28px;
  font-weight: bold;
  cursor: pointer;
}

.close-btn:hover {
  color: #ff0000;
}

.social-btn {
  display: block;
  width: 100%;
  padding: 10px;
  margin: 20px 0;
  font-size: 18px;
  background-color: #db4437;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.social-btn:hover {
  background-color: #c33d30;
}
</style> -->
<template>
  <div v-if="modelValue" class="modal" @click.self="closeModal">
    <div class="modal-content">
      <span class="close-btn" @click="closeModal">&times;</span>
      <h2 class="modal-title">소셜 로그인</h2>

      <!-- 소셜 로그인 버튼 -->
      <button class="social-btn google-btn" @click="googleLogin">Google 로그인</button>
      <button class="social-btn naver-btn" @click="naverLogin">Naver 로그인</button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";
import { useRouter } from "vue-router";

const props = defineProps({
  modelValue: Boolean,
});

const emit = defineEmits(["update:modelValue"]);
const router = useRouter();

const closeModal = () => {
  emit("update:modelValue", false);
};

// Google OAuth 로그인 처리
const googleLogin = () => {
  const clientId = "288654374576-oakoiq7biqtduolbs3rfgef4cb30umr1.apps.googleusercontent.com";
  const redirectUri = "https://i11d108.p.ssafy.io/api/v1/members/auth/login/google";
  const responseType = "code";
  const scope = "email profile";

  const url = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}&scope=${scope}`;
  window.location.href = url;
};
// const googleLogin = async () => {
//   const clientId =
//     "288654374576-oakoiq7biqtduolbs3rfgef4cb30umr1.apps.googleusercontent.com";
//   const redirectUri = "http://localhost:8888/api/v1/members/auth/login/google";
//   const responseType = "code";
//   const scope = "email profile";

//   const url = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}&scope=${scope}`;

//   window.location.href = url;
// };
// const naverLogin = async () => {
//   const clientId = "jCJ25DM0P7fbySK5L9Fd";
//   const redirectUri = "http://localhost:8888/api/v1/members/auth/login/naver";
//   const responseType = "code";
//   // const scope = 'email profile_image'

//   const url = `https://nid.naver.com/oauth2.0/authorize?response_type=${responseType}&client_id=${clientId}&redirect_uri=${redirectUri}`;

//   window.location.href = url;
// };
// Naver OAuth 로그인 처리
const naverLogin = () => {
  const clientId = "jCJ25DM0P7fbySK5L9Fd";
  const redirectUri = "https://i11d108.p.ssafy.io/api/v1/members/auth/login/naver";
  const responseType = "code";

  const url = `https://nid.naver.com/oauth2.0/authorize?response_type=${responseType}&client_id=${clientId}&redirect_uri=${redirectUri}`;

  window.location.href = url;
};
</script>

<style scoped>
.modal {
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.7);
}

.modal-content {
  background-color: #1c1c1c;
  color: #ffffff;
  padding: 30px;
  border: 2px solid #888;
  width: 400px;
  text-align: center;
  box-shadow: 0px 0px 10px #000000;
  position: relative;
}

.modal-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}

.close-btn {
  color: #ffffff;
  position: absolute;
  top: 10px;
  right: 20px;
  font-size: 28px;
  font-weight: bold;
  cursor: pointer;
}

.close-btn:hover {
  color: #ff0000;
}

.social-btn {
  display: block;
  width: 100%;
  padding: 10px;
  margin: 20px 0;
  font-size: 18px;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.google-btn {
  background-color: #db4437;
}

.google-btn:hover {
  background-color: #c33d30;
}

.naver-btn {
  background-color: #1ec800;
}

.naver-btn:hover {
  background-color: #17a300;
}
</style>
