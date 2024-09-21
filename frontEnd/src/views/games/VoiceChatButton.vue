<template>
    <button class="voice-chat-button" @click="toggleVoiceChat">
      <img :src="voiceIcon" alt="Voice chat" />
    </button>
  </template>
  
  <script setup>
  import { computed } from 'vue';
  import { useSessionStore } from '@/store/session';
  
  const props = defineProps({
    userId: Number,
  });
  
  const sessionStore = useSessionStore();
  
  const voiceIcon = computed(() => {
    return sessionStore.isVoiceOn(props.userId)
      ? require('@/assets/images/ingame/voice.png')
      : require('@/assets/images/ingame/voicex.png');
  });
  
  const toggleVoiceChat = () => {
    sessionStore.toggleVoiceChat(props.userId);
  };
  </script>
  
  <style scoped>
  .voice-chat-button {
    background: none;
    border: none;
    cursor: pointer;
    padding: 0;
  }
  
  .voice-chat-button img {
    width: 15px;
    height: 15px;
  }
  </style>
  