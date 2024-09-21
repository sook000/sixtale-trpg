<template>
  <div class="chat-modal">
    <div class="chat-header">
      <h2>채팅</h2>
      <button class="close-button" @click="closeChat">×</button>
    </div>
    <div class="chat-body">
      <p>채팅 내용이 여기에 표시됩니다.</p>
      <!-- 채팅 기능 구현 -->
    </div>
  </div>
</template>

<script setup>
import { defineEmits } from 'vue';

const emit = defineEmits(['close']);

const closeChat = () => {
  emit('close');
};
</script>

<style scoped>
.chat-modal {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 33.33%;
  height: 50%;
  background-color: #1c1c1c;
  border: 2px solid #444;
  display: flex;
  flex-direction: column;
  padding: 20px;
  border-radius: 15px;
  z-index: 1000;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chat-header h2 {
  margin: 0;
  color: white;
}

.close-button {
  background: none;
  border: none;
  color: white;
  font-size: 2rem;
  cursor: pointer;
}

.chat-body {
  flex-grow: 1;
  color: white;
}
</style>
