<template>
    <div class="modal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          친구 추가
        </div>
        <div class="modal-body">
          {{ user.name }}을 친구로 추가하시겠습니까?
        </div>
        <div class="modal-footer">
          <button @click="confirmAddFriend">추가</button>
          <button @click="closeModal">취소</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  
  const props = defineProps({
    user: Object,
  });
  
  const emit = defineEmits(['close']);
  
  const closeModal = () => {
    emit('close');
  };
  
  const confirmAddFriend = () => {
    console.log(`Friend request sent to ${props.user.name}`);
    closeModal();
  };
  </script>
  
  <style scoped>
  .modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .modal-content {
    background-color: #291707;
    border: 1px solid white;
    border-radius: 10px;
    padding: 20px;
    width: 300px;
    text-align: center;
  }
  
  .modal-header {
    font-size: 1.5em;
    margin-bottom: 20px;
    color: white;
  }
  
  .modal-body {
    margin-bottom: 20px;
    color: white;
  }
  
  .modal-footer button {
    margin: 5px;
    
    padding: 5px 10px;
  }
  </style>
  