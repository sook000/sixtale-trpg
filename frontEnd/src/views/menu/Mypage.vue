<template>
  <div class="background">
    <div class="container">
      <div class="row mt-2 g-5">
        <!-- 메인 왼쪽 콘텐츠 -->
        <Sidebar :nickName="nickName" />
        <!-- 메인 오른쪽 콘텐츠 -->
        <div class="col-md-9">
          <router-view></router-view>
        </div>
      </div>
      <div style="height: 200px"></div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { getMemberInfo } from "@/common/api/mypageAPI";
import Sidebar from "../mypage/components/Sidebar.vue";

export default {
  name: "MyPage",
  components: {
    Sidebar,
  },
  setup() {
    const id = ref(0);
    const nickName = ref("");
    const profileImage = ref("");

    onMounted(() => {
      getMemberInfo()
        .then((response) => {
          id.value = response.data.data.id;
          nickName.value = response.data.data.nickName;
          profileImage.value = response.data.data.imageURL;
          console.log(id.value);
          console.log(nickName.value);
          console.log(profileImage.value);
        })
        .catch((error) => {
          console.error("Failed to fetch member info:", error);
        });
    });

    return {
      id,
      nickName,
      profileImage,
    };
  },
};
</script>

<style scoped>
.background {
  width: 100%;
  background: linear-gradient(270deg, rgba(26, 26, 26, 0.45) 65%, #0a0a10 100%),
    linear-gradient(89.84deg, rgba(60, 60, 60, 0.9) 65.72%, #0a0a10 100%);
}
.container {
  max-width: 95%;
  padding-top: 80px;
}
</style>
