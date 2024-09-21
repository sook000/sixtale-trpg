import { defineStore } from "pinia";

export const useMapStore = defineStore("map", {
  state: () => ({
    mapData: [], // API에서 가져온 맵 데이터 저장
    isLoading: false,
    error: null,
    selectedMap: {}, // 선택한 맵을 저장할 상태
    selectedMapImage: null, // 선택한 맵의 이미지를 저장할 상태
    selectedMapId: null, // 선택한 맵의 ID를 저장할 상태
    selectedToken: {},
    currentTokenX: -1,
    currentTokenY: -1,
  }),

  actions: {
    // 선택한 맵과 ID를 설정하는 함수
    setSelectedMap(map) {
      this.selectedMap = map;
      this.selectedMapImage = map.imageURL; // 선택한 맵의 이미지를 저장
      this.selectedMapId = map.id; // 선택한 맵의 ID를 저장
    },

    // 특정 위치에 해당하는 맵을 가져오는 함수임
    getMapByPosition(row, column) {
      const map = this.mapData.find((m) =>
        m.cellList.some((cell) => cell.row === row && cell.column === column)
      );
      return map || null;
    },

    async fetchMaps(roomID, mapID) {
      this.isLoading = true;
      try {
        const response = await axios.get(
          `http://localhost:8888/api/v1/rooms/${roomID}/maps/${mapID}`
        );
      } catch (error) {
        this.error = error;
        console.error("맵 데이터를 가져오는 중 오류 발생:", error);
      } finally {
        this.isLoading = false;
      }
    },

    setCurrentTokenX(tokenX) {
      this.currentTokenX = tokenX;
    },

    setCurrentTokenY(tokenY) {
      this.currentTokenY = tokenY;
    },

    clearCurrentMap() {
      this.selectedMap = null;
    },

    selectToken(token) {
      this.selectedToken = token;
    },

    clearToken() {
      this.selectedToken = null;
    },
  },
});
