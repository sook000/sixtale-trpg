<template>
  <div>
    <div class="side-bar">
      <!-- 검색 -->
      <div class="mb-3">
        <p class="filter-title mb-2" style="color: rgba(255, 255, 255, 0.64)">
          검색
        </p>
        <div class="search w-100">
          <input
            class="input-search py-1"
            type="text"
            placeholder="검색"
            v-model="searchKeyword"
            @keyup.enter="handleSearch"
          />
          <img
            src="https://s3.ap-northeast-2.amazonaws.com/cdn.wecode.co.kr/icon/search.png"
            @click="handleSearch"
          />
        </div>
      </div>

      <hr />
      <!-- 필터 -->
      <div>
        <p class="filter-title mb-2" style="color: rgba(255, 255, 255, 0.64)">
          필터
        </p>
        <div
          class="btn-group d-block"
          role="group"
          area-label="Basic checkbox toggle button group"
        >
          <div
            class="d-flex px-3 py-1 mb-1"
            :class="{ 'bg-checked': selectedGenres.includes(genre.id) }"
            v-for="genre in genres"
            :key="genre.id"
          >
            <input
              type="checkbox"
              class="check-genre"
              :id="'btn-check-' + genre.id"
              @change="toggleGenreSelection(genre.id)"
              autocomplete="off"
            />
            <label :for="'btn-check-' + genre.id"> {{ genre.name }}</label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";

export default {
  name: "ScenarioGenreFilter",
  props: {
    genres: {
      type: Array,
      required: true,
    },
    selectedGenres: {
      type: Array,
      reuiqred: true,
    },
    searchKeyword: {
      type: String,
      required: true,
    },
  },
  setup(props, { emit }) {
    const searchKeyword = ref("");

    // 검색 처리
    const handleSearch = async () => {
      emit("update:searchKeyword", searchKeyword.value);
    };

    // 각 장르 버튼 토글
    const toggleGenreSelection = (genreId) => {
      const index = props.selectedGenres.indexOf(genreId);

      if (index > -1) {
        // 이미 선택된 경우 제거
        emit(
          "update:selectedGenres",
          props.selectedGenres.filter((id) => id !== genreId)
        );
      } else {
        // 선택되지 않은 경우 추가
        emit("update:selectedGenres", [...props.selectedGenres, genreId]);
      }
    };

    return {
      searchKeyword,
      handleSearch,
      toggleGenreSelection,
    };
  },
};
</script>

<style scoped>
.side-bar {
  width: 130px;
  background-color: #3c3d41;
  border-radius: 0.5em;
  color: white;
  padding: 10px 15px;
}
.search {
  position: relative;
  width: 300px;
}
.input-search {
  width: 100%;
  border: 1px solid #bbb;
  border-radius: 5px;
  padding: 10px;
  font-size: 13px;
}
img {
  position: absolute;
  width: 15px;
  top: 7px;
  right: 10px;
  margin: 0;
}
input[type="checkbox"] {
  display: none;
}
.filter-title,
label {
  font-size: 13px;
}
.bg-checked {
  background-color: #273047;
  border-radius: 5px;
}
</style>
