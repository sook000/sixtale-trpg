<template>
  <div><h3 class="menu-title">좋아요한 시나리오</h3></div>
  <div class="content-box right-box">
    <div class="card-container">
      <div class="card-inner-container">
        <ScenarioCard
          class="card-item"
          v-for="scenario in likedScenarios.scenarioList"
          :key="scenario.id"
          :scenario="scenario"
          @toggle-like="handleToggleLike"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import ScenarioCard from "../menu/components/ScenarioCard.vue";
import { getLikedScenarioList } from "../../common/api/mypageAPI";

export default {
  name: "UserLikeScenario",
  components: {
    ScenarioCard,
  },
  setup() {
    const likedScenarios = ref({ scenarioList: [] });

    onMounted(async () => {
      likedScenarios.value = await getLikedScenarioList();
    });

    // 좋아요 버튼 토글
    const handleToggleLike = (scenarioID, op) => {
      if (op == "unlike") {
        likedScenarios.value.scenarioList =
          likedScenarios.value.scenarioList.filter((s) => s.id !== scenarioID);
      }
    };

    return {
      likedScenarios,
      handleToggleLike,
    };
  },
};
</script>

<style scoped>
.menu-title {
  color: white;
  margin: 0 0 20px 20px;
}
/* .content-box {
  background-color: #3a3a3c;
  border-radius: 18px;
} */
.left-box {
  height: 750x;
  padding: 20px;
  border: 1px solid #3a3a3c;
}
.right-box {
  height: 695px; /*왼쪽 박스와 높이 끝 같게 */
}
.card-container {
  padding-left: 20px;
  display: flex;
  justify-content: center;
}
.card-inner-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: start;
  gap: 20px;
  width: 100%;
}
</style>
