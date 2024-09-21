import { createStore } from "vuex";
import accountStore from "./accountStore";
import menuStore from "./menuStore";
import platformInfoStore from "./platformInfoStore";
import calendarStore from "./calendarStore";

export default createStore({
  modules: {
    accountStore,
    menuStore,
    platformInfoStore,
    calendarStore,
  }
});
