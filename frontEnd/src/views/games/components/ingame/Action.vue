<template>
  <div class="action-section" :style="backgroundStyle">
    <div class="tabs">
      <button
        v-for="tab in tabs"
        :key="tab.name"
        @click="selectTab(tab.name)"
        :class="{ active: selectedTab === tab.name }"
        :style="tabStyle(tab.name)"
      >
        {{ tab.label }}
      </button>
    </div>
    <div v-if="selectedTab === 'common'" class="tab-content">
      <div class="sub-tabs">
        <button
          v-for="subTab in commonSubTabs"
          :key="subTab.name"
          @click="selectSubTab(subTab.name)"
          :class="{ active: selectedSubTab === subTab.name }"
          :style="subTabStyle(subTab.name)"
        >
          {{ subTab.label }}
        </button>
      </div>
      <div class="action-list-container">
        <div class="action-list">
          <div
            v-for="action in currentActions"
            :key="action.id"
            :class="['action-item', { selected: selectedAction === action.id }]"
            @click="selectAction(action.id)"
            @dblclick="openActionModal(action, $event)"
            :style="getActionItemStyle(action.id)"
            @mouseover="showTooltip"
            @mouseleave="hideTooltip"
          >
            {{ action.name }}
            <div class="tooltip">
              더블 클릭시 해당 액션 정보를 볼 수 있습니다
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="selectedTab === 'job'" class="tab-content">
      <div class="sub-tabs">
        <button
          v-for="subTab in jobSubTabs"
          :key="subTab.name"
          @click="selectSubTab(subTab.name)"
          :class="{ active: selectedSubTab === subTab.name }"
          :style="subTabStyle(subTab.name)"
        >
          {{ subTab.label }}
        </button>
      </div>
      <div class="action-list-container">
        <div class="action-list">
          <div
            v-for="action in currentActions"
            :key="action.id"
            :class="['action-item', { selected: selectedAction === action.id }]"
            @click="selectAction(action.id)"
            @dblclick="openActionModal(action, $event)"
            :style="getActionItemStyle(action.id)"
            @mouseover="showTooltip"
            @mouseleave="hideTooltip"
          >
            {{ action.name }}
            <!-- 삭제 버튼 추가 (핵심 및 고급액션에만 표시) -->
            <button
              v-if="
                selectedTab === 'job' &&
                (selectedSubTab === 'core' || selectedSubTab === 'advanced')
              "
              @click.stop="confirmDeleteAction(action, $event)"
              class="remove-action-button"
            >
              <img
                :src="require('@/assets/images/ingame/Delete.png')"
                alt="Delete"
                class="delete"
              />
            </button>
            <div class="tooltip">
              더블 클릭시 해당 액션 정보를 볼 수 있습니다
            </div>
          </div>
          <button class="add-action-button" @click="openAddActionModal">
            <img :src="require('@/assets/images/ingame/Plus.png')" alt="추가" />
          </button>
        </div>
      </div>
    </div>
    <ActionInfoModal
      v-if="isActionModalVisible"
      :action="selectedModalAction"
      @close="isActionModalVisible = false"
      @select="handleSelectAction"
    />
    <AddActionModal
      v-if="isAddActionModalVisible && roomId"
      :roomID="roomId"
      :playMemberID="playMemberID"
      :currentActions="currentActions"
      @close="closeAddActionModal"
      @add-action="handleAddAction"
    />
    <ConfirmDeleteModal
      v-if="isConfirmDeleteModalVisible"
      :isVisible="isConfirmDeleteModalVisible"
      :item="selectedModalAction"
      @close="closeConfirmDeleteModal"
      @confirm="handleActionDeleted"
    />
  </div>
</template>

<script>
import { ref, watch, onMounted } from "vue";
import { useRoute } from "vue-router";
import { selectedPlayMemberID, selectedUserJobID } from "@/store/state";
import {
  getCommonActions,
  addCharacterAction,
} from "@/common/api/ActionAPI.js";
import { getRoomInfo } from "@/common/api/RoomsAPI.js";
import { getCharacterSheet } from "@/common/api/CharacterSheetAPI.js";
import ActionInfoModal from "@/views/games/components/Modal/ActionInfoModal.vue";
import AddActionModal from "@/views/games/components/Modal/AddActionModal.vue";
import ConfirmDeleteModal from "@/views/games/components/Modal/ConfirmDeleteModal2.vue";

export default {
  components: {
    ActionInfoModal,
    AddActionModal,
    ConfirmDeleteModal,
  },
  setup() {
    const jobNames = {
      1: "도적",
      2: "드루이드",
      3: "마법사",
      4: "사냥꾼",
      5: "사제",
      6: "성기사",
      7: "음유시인",
      8: "전사",
    };

    const actionAreaBackground = require("@/assets/images/ingame/Border5.png");
    const coreActionImage = require("@/assets/images/ingame/CoreAction.png");
    const advancedActionImage = require("@/assets/images/ingame/AdvancedAction.png");
    const coreActionSelectImage = require("@/assets/images/ingame/CoreActionSelect.png");
    const advancedActionSelectImage = require("@/assets/images/ingame/AdvancedActionSelect.png");
    const actionListImage = require("@/assets/images/ingame/ActionList.png");
    const actionListHoverImage = require("@/assets/images/ingame/ActionListHover.png");
    const actionListSelectImage = require("@/assets/images/ingame/ActionListSelect.png");
    const tabImage = require("@/assets/images/ingame/Tab.png");

    const backgroundStyle = {
      backgroundImage: `url(${actionAreaBackground})`,
      backgroundSize: "100% 100%",
      backgroundRepeat: "no-repeat",
      backgroundPosition: "center",
      display: "flex",
      flexDirection: "column",
      padding: "10px",
      margin: "5px",
      boxSizing: "border-box",
      width: "100%",
      height: "100%",
      position: "relative",
    };

    const tabs = ref([
      { name: "common", label: "공통" },
      { name: "job", label: jobNames[selectedUserJobID.value] || "직업" },
    ]);

    watch(selectedUserJobID, (newJobID) => {
      tabs.value = [
        { name: "common", label: "공통" },
        { name: "job", label: jobNames[newJobID] || "직업" },
      ];
    });

    const commonSubTabs = ref([
      { name: "basic", label: "기본액션" },
      { name: "special", label: "특수액션" },
    ]);

    const jobSubTabs = ref([
      { name: "core", label: "핵심액션" },
      { name: "advanced", label: "고급액션" },
    ]);

    const selectedTab = ref("common");
    const selectedSubTab = ref("basic");
    const selectedAction = ref(null);
    const currentActions = ref([]);

    const isActionModalVisible = ref(false);
    const isAddActionModalVisible = ref(false);
    const isConfirmDeleteModalVisible = ref(false);
    const selectedModalAction = ref(null);

    const route = useRoute();
    const roomId = ref(null); // 초기에는 null로 설정
    const playMemberID = ref(selectedPlayMemberID.value);
    const ruleID = ref(null);

    const fetchRoomInfo = async () => {
      try {
        const roomInfo = await getRoomInfo(roomId.value);
        ruleID.value = roomInfo.ruleID;
        await updateCurrentActions(); // 룰 아이디가 설정된 후에 액션을 불러옴
      } catch (error) {
        console.error("Error fetching room info:", error);
      }
    };

    const updateCurrentActions = async () => {
      try {
        if (selectedTab.value === "job") {
          const characterSheet = await getCharacterSheet(
            roomId.value,
            playMemberID.value
          );
          const actions = characterSheet.characterAction.filter((action) => {
            return action.isCore
              ? selectedSubTab.value === "core"
              : selectedSubTab.value === "advanced";
          });
          currentActions.value = actions;
        } else {
          if (ruleID.value !== null) {
            const commonActions = await getCommonActions(ruleID.value);
            if (selectedSubTab.value === "basic") {
              currentActions.value = commonActions.basicActions || [];
            } else if (selectedSubTab.value === "special") {
              currentActions.value = commonActions.specialActions || [];
            }
          }
        }
      } catch (error) {
        console.error("Error fetching actions:", error);
      }
    };

    const handleActionDeleted = () => {
      if (selectedModalAction.value) {
        // 삭제된 액션을 currentActions 배열에서 제거
        currentActions.value = currentActions.value.filter(
          (action) => action.id !== selectedModalAction.value.id
        );
        // 선택된 액션도 초기화
        selectedAction.value = null;
        // 모달 닫기
        closeConfirmDeleteModal();
      }
    };

    const openActionModal = (action, event) => {
      event.stopPropagation(); // 이벤트 버블링을 중단시켜 클릭 이벤트가 전파되지 않도록 함
      selectedModalAction.value = {
        category:
          selectedTab.value === "common"
            ? commonSubTabs.value.find(
                (subTab) => subTab.name === selectedSubTab.value
              ).label
            : jobSubTabs.value.find(
                (subTab) => subTab.name === selectedSubTab.value
              ).label,
        ...action,
      };
      isActionModalVisible.value = true;
    };

    const openAddActionModal = () => {
      isAddActionModalVisible.value = true;
    };

    const closeAddActionModal = () => {
      isAddActionModalVisible.value = false;
    };

    const confirmDeleteAction = (action, event) => {
      event.stopPropagation();
      selectedModalAction.value = action;
      isConfirmDeleteModalVisible.value = true;
    };

    const closeConfirmDeleteModal = () => {
      isConfirmDeleteModalVisible.value = false;
      selectedModalAction.value = null;
    };

    const handleAddAction = async (action) => {
      try {
        currentActions.value.push(action); // 새 액션을 리스트에 추가
        await addCharacterAction(
          roomId.value,
          playMemberID.value,
          action.id,
          action.selectedOption ? action.selectedOption.id : null
        );
        await updateCurrentActions(); // 서버의 최신 데이터로 다시 불러오기
      } catch (error) {
        console.error("Error adding action:", error);
      }
    };

    watch(selectedPlayMemberID, async (newID) => {
      if (newID) {
        playMemberID.value = newID;
        await updateCurrentActions();
      } else {
        console.log("Player ID is null or undefined");
      }
    });

    watch(
      route,
      (newRoute) => {
        const newRoomId = newRoute.params.roomId;
        if (newRoomId) {
          roomId.value = newRoomId;
          console.log("Updated roomId:", newRoomId);
          fetchRoomInfo(); // roomId가 변경되었을 때 방 정보와 액션 목록을 업데이트
        }
      },
      { immediate: true }
    ); // 컴포넌트 초기화 시에도 감시자 실행

    onMounted(async () => {
      console.log("Initial roomId:", roomId.value); // roomId.value가 무엇인지 확인
      if (roomId.value) {
        await fetchRoomInfo();
        if (selectedPlayMemberID.value) {
          playMemberID.value = selectedPlayMemberID.value;
          await updateCurrentActions();
        } else {
          console.log("No player selected initially");
        }
      }
    });

    const tabStyle = (tabName) => {
      return {
        background:
          selectedTab.value === tabName
            ? "none"
            : `url(${tabImage}) no-repeat center center`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        border: "none",
        padding: "0",
        margin: "0",
      };
    };

    const selectTab = (tabName) => {
      selectedTab.value = tabName;
      selectedSubTab.value = tabName === "common" ? "basic" : "core";
      selectedAction.value = null;
      updateCurrentActions();
    };

    const selectSubTab = (subTabName) => {
      selectedSubTab.value = subTabName;
      selectedAction.value = null;
      updateCurrentActions();
    };

    const selectAction = (actionId) => {
      selectedAction.value = actionId;
    };

    const subTabStyle = (subTabName) => {
      const isCoreOrBasic = subTabName === "core" || subTabName === "basic";
      const isSelected = selectedSubTab.value === subTabName;
      const imagePath = isSelected
        ? isCoreOrBasic
          ? coreActionSelectImage
          : advancedActionSelectImage
        : isCoreOrBasic
        ? coreActionImage
        : advancedActionImage;
      return {
        backgroundImage: `url(${imagePath})`,
        backgroundSize: "cover",
        backgroundRepeat: "no-repeat",
      };
    };

    const getActionItemStyle = (actionId) => {
      const isSelected = selectedAction.value === actionId;
      const baseStyle = {
        backgroundSize: "100% 100%",
        backgroundPosition: "center",
        width: "45%",
        backgroundRepeat: "no-repeat",
      };

      if (isSelected) {
        return {
          ...baseStyle,
          backgroundImage: `url(${actionListSelectImage})`,
        };
      }

      return {
        ...baseStyle,
        backgroundImage: `url(${actionListImage})`,
        ":hover": {
          backgroundImage: `url(${actionListHoverImage})`,
        },
      };
    };

    const showTooltip = (event) => {
      const tooltip = event.target.querySelector(".tooltip");
      if (tooltip) {
        tooltip.style.visibility = "visible";
        tooltip.style.opacity = 1;
        tooltip.style.top = `${event.target.offsetHeight}px`;
        tooltip.style.left = `50%`;
        tooltip.style.transform = `translateX(-50%)`;
      }
    };

    const hideTooltip = (event) => {
      const tooltip = event.target.querySelector(".tooltip");
      if (tooltip) {
        tooltip.style.visibility = "hidden";
        tooltip.style.opacity = 0;
      }
    };

    const handleSelectAction = (action) => {
      console.log("Selected action:", action);
      // 추가 로직 작성...
    };

    return {
      backgroundStyle,
      tabs,
      commonSubTabs,
      jobSubTabs,
      selectedTab,
      selectedSubTab,
      currentActions,
      selectTab,
      selectSubTab,
      selectedAction,
      selectAction,
      subTabStyle,
      tabStyle,
      handleActionDeleted,
      getActionItemStyle,
      isActionModalVisible,
      selectedModalAction,
      openActionModal,
      isAddActionModalVisible,
      openAddActionModal,
      closeAddActionModal,
      confirmDeleteAction,
      closeConfirmDeleteModal,
      isConfirmDeleteModalVisible,
      handleAddAction,
      handleSelectAction,
      showTooltip,
      hideTooltip,
      jobTabName: jobNames[selectedUserJobID.value] || "직업",
      roomId,
      playMemberID,
    };
  },
};
</script>

<style scoped>
.action-section {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}

.tabs {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding: 0; /* 여백 제거 */
  height: 40px; /* 탭의 높이를 설정 */
}

.tabs button {
  flex: 1;
  padding: 0;
  border: none;
  background-color: transparent;
  background-size: cover;
  background-position: center;
  color: white;
  cursor: pointer;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  box-sizing: border-box;
}

.tabs button.active {
  background-color: #000000;
}

.tabs button:not(.active) {
  background-size: cover;
  background-position: center;
}

.tab-content {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  overflow: hidden;
}

.sub-tabs {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  margin-bottom: 15px;
  padding: 0 10px;
}

.sub-tabs button {
  flex: 1;
  margin: 0 5px;
  padding: 8px 0;
  border: none;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  width: 45%;
  height: 35px;
  transition: border 0.3s;
  font-size: 1rem;
}

.add-action-button {
  margin-right: 50px;
  height: 45px;
  border: none;
  background: none;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}

.action-list-container {
  flex-grow: 1;
  overflow: hidden;
  height: 200px; /* 필요에 따라 높이 값을 조정 */
  max-width: 100%;
}

.action-list {
  height: 100%;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-content: flex-start;
  gap: 10px;
  padding: 10px;
  max-width: 100%;
  overflow-x: hidden;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #855e2fee #201805;
}

.action-item {
  color: white;
  border-radius: 5px;
  width: calc(50% - 15px);
  text-align: center;
  cursor: pointer;
  border: 1px solid #444;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem; /* 텍스트 크기 조정 */
  transition: border-color 0.3s ease, transform 0.3s ease;
  background-size: 100% 100%;
  background-position: center;
  background-repeat: no-repeat;
  box-sizing: border-box;
  position: relative;
}

.delete {
  padding-right: 5px;
}

.remove-action-button {
  object-fit: cover;
  position: absolute;
  top: 2px;
  right: 10px;
  background: none;
  border: none;
  width: 20px;
  height: 20px;
  cursor: pointer;
  z-index: 10;
}

.tooltip {
  visibility: hidden;
  width: 150px;
  background-color: rgba(0, 0, 0, 0.8);
  color: #fff;
  text-align: center;
  border-radius: 3px;
  padding: 5px;
  position: absolute; /* 고정 위치 */
  z-index: 10;
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 0.8rem;
}

.tooltip::after {
  content: "";
  position: absolute;
  top: -5px;
  left: 50%;
  margin-left: -5px;
  border-width: 5px;
  border-style: solid;
  border-color: rgba(0, 0, 0, 0.8) transparent transparent transparent;
}

/* Chrome, Edge, Safari 스크롤바 커스터마이징 */
.action-list::-webkit-scrollbar {
  width: 8px;
}

.action-list::-webkit-scrollbar-track {
  background: #a56722;
  border-radius: 30px;
}

.action-list::-webkit-scrollbar-thumb {
  background-color: #6b3d01;
  border-radius: 30px;
  border: 1px solid #5e3b13;
}
</style>
