<template>
  <div>
    <div class="card-background">
      <div class="position-relative" style="height: 140px">
        <img class="card-image" :src="scenario.imageURL" alt="" />
        <i
          v-if="scenario.isLiked"
          class="position-absolute bi bi-suit-heart-fill text-danger end-0 px-2"
          @click="toggleLikeScenario(scenario.id, 'unlike')"
        ></i>
        <i
          v-else
          class="position-absolute bi bi-suit-heart text-white end-0 px-2"
          @click="toggleLikeScenario(scenario.id, 'like')"
        ></i>
        <img />
      </div>
      <div class="text-container">
        <div class="text-title">{{ scenario.title }}</div>
        <div class="text-genres">
          <span class="text-genre" v-for="genre in scenario.genreList">
            #{{ genre.name }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { likeScenario, unlikeScenario } from "@/common/api/ScenarioAPI";

export default {
  name: "ScenarioCard",
  props: {
    scenario: {
      type: Object,
      required: true,
    },
  },
  emits: ["toggle-like"],
  setup(props, { emit }) {
    const toggleLikeScenario = async (scenarioID, op) => {
      if (op == "like") {
        await likeScenario(scenarioID);
      } else if (op == "unlike") {
        await unlikeScenario(scenarioID);
      }
      emit("toggle-like", scenarioID, op);
    };

    return { toggleLikeScenario };
  },
};
</script>

<style scoped>
.card-background {
  background-color: #3c3d41;
  width: 230px;
  height: 225px;
  border-radius: 1ch;
  padding: 15px;
}
.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 1ch;
}
.text-title {
  white-space: nowrap;
  overflow: hidden;
  color: white;
  margin: 10px 0 0 0;
}
.text-genres {
  white-space: nowrap;
  overflow: hidden;
  width: 100%;
  display: block;
  position: relative;
}
.text-genre {
  display: inline-block;
  margin-right: 5px;
  color: white;
  font-size: smaller;
}
</style>
