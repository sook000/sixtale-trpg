<template>
  <div class="section">
    <div v-for="(message, i) in messages" :key="i" class="mb-3" :class="{ 'text-right': message.role === 'user', 'text-left': message.role !== 'user' }">
      <b-card 
        body-class="message-card py-2 px-2"
        :style="{
          borderRadius: '18px',
          fontWeight: 400,
          backgroundColor: message.role === 'user' ? '#4E5E8A' : '#f4f4f4',
          color: message.role === 'user' ? '#ffff' : '#000', // 기본 텍스트 색상 검정으로 변경
          marginLeft: message.role === 'user' ? 'auto' : '0',
          marginRight: message.role !== 'user' ? 'auto' : '0'
        }"
      >
        <div class="content" v-html="formatMessageContent(message.content)"></div>
      </b-card>
    </div>

  </div>
</template>

<script setup>
import MarkdownIt from 'markdown-it';
import { defineProps } from 'vue';

defineProps({
  messages: {
    type: Array,
    default: () => [],
  },
});

const md = new MarkdownIt();

const formatMessageContent = (content) => {
  return md.render(content); // Markdown을 HTML로 변환
};
</script>

<style scoped>
.section {
  overflow-y: auto; 
  overflow-x: hidden;
  padding-top: 10px;
}

.message-card {
  border-radius: 18px;
  color: black;
}

.text-right {
  text-align: right;
}

.text-left {
  text-align: left;
}
</style>
