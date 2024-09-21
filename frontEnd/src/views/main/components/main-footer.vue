<template>
  <footer class="main-footer">
    <div class="contents">
      <div class="footer-logo">
        <img src="@/assets/images/SixtaleLogo.png" alt="Sixtale Logo" class="logo-image" />
      </div>
      <div class="footer-description">
        <p class="description">{{ description }}</p>
        <p class="ceo-info">{{ ceoInfo }}</p>
      </div>
      <div class="footer-company-info">
        <p v-for="(info, index) in companyInfo" :key="index">{{ info }}</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref } from 'vue';

const description = ref('Sixtale은 당신이 상상하던 이야기의 주인공이 되어 꿈을 이루게 합니다.');
const ceoInfo = ref('대표이사: 미카엘');
const companyInfo = ref([
  'Sixtale 엔터테인먼트 유한 회사',
  '주소: 06164 서울시 강남구 테헤란로 521 파르나스타워 150층',
  '대표 번호: 1234-0000 | 팩스 번호: (02)123-4567 | 이메일: support@sixtale.com',
  '사업자 등록 번호: 211-87-49910 | 통신 판매업 신고 번호: 강남-6017호 | 사업자정보확인',
  '대표이사: 미카엘 | 개인정보 보호책임자: 잭 스패로우'
]);
</script>

<style scoped>
.main-footer {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px 10px;
  background-color: #031527;
  color: white;
  text-align: center;
}

.contents {
  max-width: 1200px;
  width: 100%;
  display: flex;
  align-items: center; 
  justify-content: space-between;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.footer-logo {
  flex: 0 1 auto; 
}

.logo-image {
  max-width: 100px; 
  width: 100%; 
}

.footer-description {
  flex: 1 1 0%; 
  margin-left: 20px; 
  text-align: center;
}

.description {
  font-size: 1.7em; 
  font-weight: bold;
  margin-bottom: 10px;
}

.ceo-info {
  font-size: 1.em; 
  margin-bottom: 10px;
}

.footer-company-info {
  margin-bottom: 20px;
  text-align: left; 
  width: 100%; 
}

.footer-description p,
.footer-company-info p {
  margin: 5px 0;
}

@media (max-width: 700px) {
  .main-footer {
    height: auto;
  }
  .contents {
    text-align: center;
  }
  .footer-description {
    margin-left: 0;
  }
}

@media (min-width: 701px) {
  .main-footer {
    height: auto;
  }
  .contents {
    line-height: normal;
  }
}
</style>
