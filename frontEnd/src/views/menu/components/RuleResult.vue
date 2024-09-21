<template>
    <div class="section">
      <div v-for="(message, i) in messages" :key="i" class="mb-4" :class="{ 'text-right': message.role === 'user', 'text-left': message.role !== 'user' }">
        <!-- <b-badge variant="primary" class="mb-2">
          {{ capitalize(message.role) }}
        </b-badge> -->
        <b-card 
         body-class="message-card py-2 px-"
         :style="{
          borderRadius: '18px',
          fontWeight: 600,
          backgroundColor: message.role === 'user' ? '#4E5E8A' : '#f4f4f4',
          color: message.role === 'user' ? '#ffff' : black,
          maxWidth: message.role === 'user' ? 'fit-content' : '70%', /* 사용자 역할일 때는 최대 너비 제한 없음 */
          width: message.role === 'user' ? 'fit-content' : '70%', /* 사용자 역할일 때는 자동 크기 조정 */
          marginLeft: message.role === 'user' ? 'auto' : '0', /* 오른쪽 정렬 */
          marginRight: message.role !== 'user' ? 'auto' : '0' /* 왼쪽 정렬 */
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
  
  const capitalize = (v) => `${v.charAt(0).toUpperCase()}${v.slice(1)}`;
  </script>
  <style scoped>
  .section {
    overflow-y: auto; 
    overflow-x: hidden; 
  }
  
  .message-card {
    max-width: 10px;
    border-radius: 18px; /* 기존 스타일 유지 */
    color: black; /* 기본 텍스트 색상 */

  }
  
  .text-right {
    text-align: right;
  }
  
  .text-left {
    text-align: left;
  }
  </style>
  