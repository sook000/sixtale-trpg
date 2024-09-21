export default {
  namespaced: true,
  state: {
    selectedDate: new Date(),
  },
  mutations: {
    setSelectedDate(state, date) {
      state.selectedDate = date;
    },
  },
  actions: {
    updateSelectedDate({ commit }, date) {
      commit('setSelectedDate', date);
    },
  },
  getters: {
    getSelectedDate: (state) => state.selectedDate,
  },
};
