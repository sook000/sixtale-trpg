<template>
  <div><h3 class="menu-title">성향 시트</h3></div>
  <div class="content-box right-box">
    <div class="disposition-sheet">
      <div class="sheet-row">
        <div class="sheet-label">RP</div>
        <div class="sheet-value">
          긴 장문을 선호합니다.<br />
          클라이맥스나 전투, 감정 표현이 필요한 부분에서는 긴 장문을 선호
        </div>
      </div>
      <div class="sheet-row">
        <div class="sheet-label">채팅</div>
        <div class="sheet-value">보통</div>
      </div>
      <div class="sheet-row">
        <div class="sheet-label">사람</div>
        <div class="sheet-value">중요</div>
      </div>
      <div class="sheet-row">
        <div class="sheet-label">취향</div>
        <div class="sheet-value">필수</div>
      </div>
      <div class="sheet-row">
        <div class="sheet-label">시스템</div>
        <div class="sheet-value">선호</div>
      </div>
      <div class="sheet-row">
        <div class="sheet-label">시간</div>
        <div class="sheet-value">필수</div>
      </div>
      <button class="sheet-submit">수정</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";

onMounted();
</script>

<style scoped>
.menu-title {
  color: white;
  margin: 0 0 20px 20px;
}
.content-box {
  background-color: #3a3a3c;
  border-radius: 18px;
}
.left-box {
  height: 750x;
  padding: 20px;
  border: 1px solid #3a3a3c;
}
.right-box {
  height: 695px; /*왼쪽 박스와 높이 끝 같게 */
  padding: 20px;
  border: 1px solid #3a3a3c;
}
.disposition-sheet {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding: 20px;
  background-color: #2b2b2b;
  border-radius: 10px;
  height: 100%;
  overflow: hidden;
}
.sheet-row {
  display: flex;
  margin-bottom: 20px;
  align-items: center;
}
.sheet-label {
  flex: 0 0 50px;
  color: #bfbfc0;
  font-weight: bold;
}
.sheet-value {
  flex: 1;
  color: white;
  padding-left: 10px;
  border-left: 1px solid #bfbfc0;
}
.sheet-submit {
  margin-top: auto;
  align-self: flex-end;
  padding: 10px 20px;
  background-color: #4c5c68;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-decoration: none;
  font-weight: bold;
}
.sheet-submit:hover {
  background-color: #3a4b57;
}
</style>
