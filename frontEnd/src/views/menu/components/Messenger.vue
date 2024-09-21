<template>
    <div class="messenger-modal-wrapper" @click.self="closeMessenger">
      <div class="messenger-modal">
        <div class="messenger-header">
          <img :src="userProfileImage" alt="Profile" class="profile-image" />
          <span class="user-nickname">{{ userNickname }}</span>
          <button class="my-page-button" @click="goToMyPage">마이페이지</button>
          <button class="close-button" @click="closeMessenger">×</button>
        </div>
        <div class="friend-list-section">
          <h3 class="friend-list-title">내 친구목록 리스트</h3>
          <div class="friend-list">
            <div class="friend-item" v-for="friend in friends" :key="friend.id">
              <img :src="friend.profileImage" alt="Friend Profile" class="friend-profile-image" />
              <span class="friend-nickname">{{ friend.nickname }}</span>
              <button class="friend-detail-button" @click="viewFriendDetail(friend.id)">상세보기</button>
              <button class="friend-delete-button" @click="deleteFriend(friend.id)">삭제</button>
            </div>
          </div>
        </div>
        <div class="search-section">
          <div class="search-bar">
            <input type="text" v-model="searchQuery" placeholder="아이디나 이메일로 검색" class="search-input" />
            <button class="search-button" @click="searchUsers">검색</button>
          </div>
          <div class="search-results">
            <div class="search-item" v-for="result in searchResults" :key="result.id">
              <img :src="result.profileImage" alt="Search Result Profile" class="search-profile-image" />
              <span class="search-nickname">{{ result.nickname }}</span>
              <button class="search-detail-button" @click="viewUserDetail(result.id)">상세보기</button>
              <button class="search-add-button" @click="addFriend(result.id)">친구추가</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { defineEmits } from 'vue';
  
  const emit = defineEmits(['close', 'startChat']);
  
  const userProfileImage = ref(require('@/assets/images/messenger/friend1.png'));
  const userNickname = ref('내 닉네임');
  
  const friends = ref([
    { id: 1, profileImage: require('@/assets/images/messenger/friend1.png'), nickname: '친구1' },
    { id: 2, profileImage: require('@/assets/images/messenger/friend2.png'), nickname: '친구2' },
    { id: 3, profileImage: require('@/assets/images/messenger/friend3.png'), nickname: '친구3' },
  ]);
  
  const searchQuery = ref('');
  const searchResults = ref([]);
  
  const viewFriendDetail = (friendId) => {
    console.log(`Viewing detail for friend with ID ${friendId}`);
  };
  
  const deleteFriend = (friendId) => {
    friends.value = friends.value.filter(friend => friend.id !== friendId);
    console.log(`Deleted friend with ID ${friendId}`);
  };
  
  const startChat = (friendId) => {
    emit('startChat', friendId);
    emit('close'); // 채팅 시작 시 메신저 모달을 닫음
  };
  
  const closeMessenger = () => {
    emit('close');
  };
  
  const goToMyPage = () => {
    console.log('Navigating to My Page');
    // 여기에 마이페이지로 이동하는 로직을 구현합니다.
  };
  
  const searchUsers = () => {
    console.log(`Searching for users with query: ${searchQuery.value}`);
    // 여기에 검색 로직을 구현합니다.
    // 예시 데이터를 사용하여 검색 결과를 표시합니다.
    searchResults.value = [
      { id: 4, profileImage: require('@/assets/images/messenger/friend4.png'), nickname: '검색결과1' },
      { id: 5, profileImage: require('@/assets/images/messenger/friend5.png'), nickname: '검색결과2' },
    ];
  };
  
  const viewUserDetail = (userId) => {
    console.log(`Viewing detail for user with ID ${userId}`);
  };
  
  const addFriend = (userId) => {
    console.log(`Adding friend with ID ${userId}`);
    // 여기에 친구 추가 로직을 구현합니다.
  };
  </script>
  
  <style scoped>
  .messenger-modal-wrapper {
    position: fixed;
    bottom: 0;
    right: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.7);
    display: flex;
    justify-content: flex-end;
    align-items: flex-end;
    z-index: 999;
  }
  
  .messenger-modal {
    width: 25%;
    height: 80%;
    background-color: #001f3f;
    border: 2px solid #444;
    display: flex;
    flex-direction: column;
    padding: 20px;
    border-radius: 15px;
    z-index: 1000;
    overflow: hidden;
  }
  
  .messenger-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #0a3d62;
    padding: 10px;
    border-radius: 10px;
  }
  
  .profile-image {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 10px;
  }
  
  .user-nickname {
    color: white;
    font-size: 1.2rem;
    font-weight: bold;
    flex-grow: 1;
  }
  
  .my-page-button {
    background: none;
    border: none;
    color: white;
    font-size: 1rem;
    cursor: pointer;
    margin-right: 10px;
  }
  
  .close-button {
    background: none;
    border: none;
    color: white;
    font-size: 2rem;
    cursor: pointer;
  }
  
  .friend-list-section {
    flex: 1 1 60%;
    margin-top: 20px;
    overflow-y: auto;
  }
  
  .friend-list-title {
    margin-bottom: 10px;
  }
  
  .friend-list {
    display: flex;
    flex-direction: column;
  }
  
  .friend-item {
    display: flex;
    align-items: center;
    background-color: #2c2c2c;
    padding: 10px;
    border-radius: 10px;
    margin-bottom: 10px;
  }
  
  .friend-profile-image {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 10px;
  }
  
  .friend-nickname {
    color: white;
    font-size: 1.1rem;
    flex-grow: 1;
  }
  
  .friend-detail-button,
  .friend-delete-button {
    background-color: #4caf50;
    color: white;
    border: none;
    padding: 5px 10px;
    margin-left: 5px;
    cursor: pointer;
    border-radius: 5px;
    transition: background-color 0.3s ease;
  }
  
  .friend-delete-button {
    background-color: #f44336;
  }
  
  .friend-detail-button:hover,
  .friend-delete-button:hover {
    background-color: #45a049;
  }
  
  .friend-delete-button:hover {
    background-color: #e53935;
  }
  
  .search-section {
    flex: 0 1 20%;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    margin-top: 20px;
  }
  
  .search-bar {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
  }
  
  .search-input {
    flex: 1;
    padding: 10px;
    margin-right: 10px;
    border: none;
    border-radius: 5px;
  }
  
  .search-button {
    padding: 10px;
    background-color: #4caf50;
    color: white;
    border: none;
    cursor: pointer;
    border-radius: 5px;
    transition: background-color 0.3s ease;
  }
  
  .search-results {
    overflow-y: auto;
  }
  
  .search-item {
    display: flex;
    align-items: center;
    background-color: #2c2c2c;
    padding: 10px;
    border-radius: 10px;
    margin-bottom: 10px;
  }
  
  .search-profile-image {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 10px;
  }
  
  .search-nickname {
    color: white;
    font-size: 1.1rem;
    flex-grow: 1;
  }
  
  .search-detail-button,
  .search-add-button {
    background-color: #4caf50;
    color: white;
    border: none;
    padding: 5px 10px;
    margin-left: 5px;
    cursor: pointer;
    border-radius: 5px;
    transition: background-color 0.3s ease;
  }
  
  .search-detail-button:hover,
  .search-add-button:hover {
    background-color: #45a049;
  }
  
  @media (max-width: 768px) {
    .messenger-modal {
      width: 80%;
      height: 70%;
      bottom: 10px;
      right: 10px;
    }
  
    .search-bar {
      flex-direction: column;
    }
  
    .search-input {
      margin-right: 0;
      margin-bottom: 10px;
    }
  }
  </style>
  