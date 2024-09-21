<template>
  <div class="side-bar col-md-3">
    <div class="content-box left-box">
      <!--프로필 섹션 -->
      <div class="profile-section">
        <img
          :src="profileImage || require('@/assets/images/mypage/user.png')"
          alt="Profile Image"
          class="profile-image"
        />
        <!-- Profile Name -->
        <div class="profile-name fs-5">
          <router-link to="/mypage/user-info"
            >{{
              nickName.length > 7 ? nickName.substring(0, 7) + "..." : nickName
            }}
            님 &gt;</router-link
          >
        </div>
        <hr class="divider" />
        <!-- Menu -->
        <div class="menu-section">
          <h4 class="menu-header">플레이</h4>
          <ul class="menu-list">
            <li>
              <router-link to="/mypage/user-playlist">
                <!-- <img
                  src="@/assets/images/mypage/left_box/play_log.png"
                  alt="Records Icon"
                  class="menu-icon"
                /> -->
                <i class="bi bi-file-text pe-2"></i>
                플레이 기록
              </router-link>
            </li>
            <li>
              <router-link to="/mypage/user-calendar">
                <!-- <img
                  src="@/assets/images/mypage/left_box/play_schedule.png"
                  alt="Schedule Icon"
                  class="menu-icon"
                /> -->
                <i class="bi bi-calendar4-week pe-2"></i>
                플레이 일정
              </router-link>
            </li>
          </ul>
          <h4 class="menu-header">시나리오</h4>
          <ul class="menu-list">
            <li>
              <router-link to="/mypage/user-likescenario">
                <!-- <img
                  src="@/assets/images/mypage/left_box/heart.png"
                  alt="Scenario Icon"
                  class="menu-icon"
                /> -->
                <i class="bi bi-suit-heart pe-2"></i>
                좋아요한 시나리오
              </router-link>
            </li>
          </ul>
          <h4 class="menu-header">나의 정보</h4>
          <ul class="menu-list">
            <li>
              <router-link to="/mypage/user-info">
                <!-- <img
                  src="@/assets/images/mypage/left_box/my_profile.png"
                  alt="Profile Icon"
                  class="menu-icon"
                /> -->
                <i class="bi bi-person-circle pe-2"></i>
                정보 조회 / 수정
              </router-link>
            </li>
            <li>
              <router-link to="/mypage/user-style">
                <!-- <img
                  src="@/assets/images/mypage/left_box/my_like.png"
                  alt="Sheets Icon"
                  class="menu-icon"
                /> -->
                <i class="bi bi-clipboard-heart pe-2"></i>
                성향 시트
              </router-link>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    nickName: {
      type: String,
      required: true,
    },
  },
  setup() {},
};
</script>

<style scoped>
.side-bar {
  width: 250px;
  background-color: #3c3d41;
  border-radius: 0.5em;
  /* color: white; */
  padding: 10px 25px;
}
.menu-title {
  color: white;
  margin: 0 0 20px 30px;
}
.left-box {
  height: 750px;
  border: 1px solid #3a3a3c;
}
.right-box {
  height: 685px; /*왼쪽 박스와 높이 끝 같게 */
}
.profile-section {
  text-align: center;
  color: white;
}
.profile-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  margin-top: 30px;
  margin-bottom: 20px;
  border: 2px solid #4c5c68; /* Profile Image border color */
}
.profile-name {
  font-size: 1.5rem;
  margin-bottom: 30px;
  color: white;
  font-style: none;
}
.profile-name a {
  color: white;
  text-decoration: none;
}
.divider {
  border-color: #5c5c5e;
  margin: 30px 0 40px 0;
}
.menu-section {
  text-align: left;
}
.profile-name a:hover {
  text-decoration: underline;
}
.menu-header {
  font-size: 1rem;
  color: #bbb;
  margin: 20px 0 20px 0;
}
.menu-list {
  list-style-type: none;
  padding: 0;
  margin-bottom: 30px;
}
.menu-list li {
  /* padding: 10px; */
  padding: 10px 0;
}
.menu-list a {
  color: white;
  text-decoration: none;
  font-size: 1rem;
  display: flex;
  align-items: center;
  margin-left: 20px;
}
.menu-list :hover {
  background-color: #273047; /* Blue background on hover */
  color: white;
  border-radius: 0.5em;
}
.menu-icon {
  width: 24px;
  height: 24px;
  margin-right: 10px;
  object-fit: contain;
}
</style>
