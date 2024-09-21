<template>
    <div>로그인 중입니다...</div>
  </template>
  
  <script setup>
  import { onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { login } from '@/services/authService'; // 이 함수가 JWT 토큰을 로컬 스토리지에 저장함
  
  const route = useRoute();
  const router = useRouter();
  
  onMounted(async () => {
    const provider = route.query.provider; // 예: 'google' 또는 'naver'
    const code = route.query.code;
  
    if (!provider || !code) {
      console.error('소셜 로그인에 필요한 정보가 부족합니다.');
      return;
    }
  
    try {
      await login(provider, code);
      router.push('/'); // 로그인 후 홈으로 리디렉트
    } catch (error) {
      console.error("로그인 처리 중 오류 발생:", error);
      alert("로그인 처리 중 오류가 발생했습니다.");
    }
  });
  </script>
  