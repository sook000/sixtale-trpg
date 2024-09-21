<template>
  <div class="background">
    <div class="container">
      <h1 class="menu-title">시나리오</h1>
      <!-- 정렬 버튼 -->
      <div class="d-flex justify-content-end">
        <div class="mb-5" role="group">
          <button
            type="button"
            class="rounded text-white dropdown-toggle px-2"
            style="background-color: #3c3d41"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            {{ selectedSort.text }}
          </button>
          <ul class="dropdown-menu">
            <li>
              <a
                class="dropdown-item"
                href="#"
                @click="selectSort('updatedAt', 'desc', '최신순')"
                >최신순</a
              >
            </li>
            <li>
              <a
                class="dropdown-item"
                href="#"
                @click="selectSort('updatedAt', 'asc', '등록순')"
                >등록순</a
              >
            </li>
            <li>
              <a
                class="dropdown-item"
                href="#"
                @click="selectSort('likes', 'desc', '좋아요순')"
                >좋아요순</a
              >
            </li>
          </ul>
        </div>
      </div>

      <div class="main-container d-flex">
        <!-- 장르 필터 -->
        <ScenarioGenreFilter
          :searchKeyword="searchKeyword"
          :genres="genres"
          :selectedGenres="selectedGenres"
          @update:searchKeyword="searchKeyword = $event"
          @update:selectedGenres="selectedGenres = $event"
        />
        <!-- 시나리오 아이템 목록 -->
        <div class="card-container">
          <div class="card-inner-container">
            <ScenarioCard
              class="card-item"
              v-for="scenario in scenarios.scenarioList"
              :key="scenario.id"
              :scenario="scenario"
              @toggle-like="handleToggleLike"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from "vue";
import ScenarioCard from "./components/ScenarioCard.vue";
import ScenarioGenreFilter from "./components/ScenarioGenreFilter.vue";
import { getScenarioList } from "@/common/api/ScenarioAPI.js";
import { getGenreList } from "@/common/api/GenreAPI";
import { getLikedScenarioList } from "../../common/api/mypageAPI";

export default {
  name: "Scenarios",
  components: {
    ScenarioCard,
    ScenarioGenreFilter,
  },
  setup() {
    const scenarios = ref([]);
    const genres = ref([]);
    const selectedGenres = ref([]);
    const selectedSort = ref({
      criteria: "updatedAt",
      order: "desc",
      text: "최신순",
    });
    const searchKeyword = ref("");
    const likedScenarios = ref([]);

    // 첫 조회
    onMounted(async () => {
      showScenarios("", "");
      genres.value = await getGenreList();
      likedScenarios.value = await getLikedScenarioList(); //
    });

    // 검색 키워드에 따른 시나리오 조회
    watch(searchKeyword, (newKeyword) => {
      showScenarios(selectedGenres.value, newKeyword);
    });

    // 장르 필터링에 따른 시나리오 조회
    watch(selectedGenres, async (newGenres) => {
      showScenarios(newGenres, searchKeyword.value);
    });

    // 정렬 선택에 따른 시나리오 조회
    const selectSort = async (criteria, order, text) => {
      if (
        selectedSort.value.criteria == criteria &&
        selectedSort.value.order == order
      ) {
        return;
      }
      selectedSort.value = { criteria, order, text };
      showScenarios(selectedGenres.value, searchKeyword.value);
    };

    // 시나리오 조회 API 호출
    const showScenarios = async (g, keyword) => {
      let genres = "";
      if (g != "") genres = g.join(",");

      scenarios.value = await getScenarioList(
        genres,
        keyword,
        0,
        20,
        selectedSort.value.criteria,
        selectedSort.value.order
      );
    };

    // 좋아요 버튼 토글
    const handleToggleLike = (scenarioID, op) => {
      console.log(`Scenario ID: ${scenarioID}, Operation: ${op}`);
      const scenario = scenarios.value.scenarioList.find(
        (s) => s.id == scenarioID
      );
      if (scenario) {
        scenario.isLiked = op === "like";
      }
    };

    return {
      scenarios,
      genres,
      selectedSort,
      searchKeyword,
      selectSort,
      selectedGenres,
      handleToggleLike,
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
  max-width: 1200px;
  padding: 100px 0;
}
.main-container {
  max-width: 1200px;
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
.menu-title {
  color: white;
  margin-top: 60px;
  margin-bottom: 40px;
}
.dropdown-toggle,
.dropdown-item {
  font-size: smaller;
}
.dropdown-item:active {
  background: gray;
  color: white;
}
</style>
