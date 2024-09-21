<template>
  <div><h3 class="menu-title">플레이 일정</h3></div>
  <div class="content-box right-box"></div>
</template>

<script>
import { ref, onMounted } from "vue";

onMounted();

export default {
  name: "UserCalendar",
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
</style>
