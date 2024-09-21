<template>
  <div class="action-tab">
    <div class="tab-buttons">
      <button 
        class="tab-button" 
        :class="{ active: activeTab === 'core' }" 
        :style="getTabButtonStyle('core')" 
        @click="activeTab = 'core'"
      >
        핵심 액션
      </button>
      <button 
        class="tab-button" 
        :class="{ active: activeTab === 'advanced' }" 
        :style="getTabButtonStyle('advanced')" 
        @click="activeTab = 'advanced'"
      >
        고급 액션
      </button>
    </div>

    <div class="action-list">
      <div v-if="activeTab === 'core'">
        <div v-if="coreActions.length > 0" class="action-grid">
          <div v-for="(action, index) in coreActions" :key="index" class="action-item">
            <h3>{{ action.name }}</h3>
            <p v-html="action.description" class="description"></p>
            <div class="action-details">
              <div class="detail-box">
                <strong>주사위:</strong> {{ action.diceType }} x {{ action.diceCount }}
              </div>
              <div class="detail-box">
                <strong>레벨:</strong> {{ action.level }}
              </div>
              <div class="detail-box" v-if="action.actionOption.length > 0">
                <strong>옵션:</strong> {{ action.actionOption[0].content }}
              </div>
            </div>
          </div>
        </div>
        <div v-else>
          <p>핵심 액션이 없습니다.</p>
        </div>
      </div>
      <div v-else>
        <div v-if="advancedActions.length > 0" class="action-grid">
          <div v-for="(action, index) in advancedActions" :key="index" class="action-item">
            <h3>{{ action.name }}</h3>
            <p v-html="action.description" class="description"></p>
            <div class="action-details">
              <div class="detail-box">
                <strong>주사위:</strong> {{ action.diceType }} x {{ action.diceCount }}
              </div>
              <div class="detail-box">
                <strong>레벨:</strong> {{ action.level }}
              </div>
              <div class="detail-box" v-if="action.actionOption.length > 0">
                <strong>옵션:</strong> {{ action.actionOption[0].content }}
              </div>
            </div>
          </div>
        </div>
        <div v-else>
          <p>고급 액션이 없습니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps } from 'vue';

const props = defineProps({
  actionData: {
    type: Array,
    default: () => []
  }
});

const activeTab = ref('core');

const coreActions = computed(() => {
  return (props.actionData || []).filter(action => action.isCore);
});

const advancedActions = computed(() => {
  return (props.actionData || []).filter(action => !action.isCore);
});

function getTabButtonStyle(tab) {
  const active = activeTab.value === tab;
  const imagePath = active
    ? require('@/assets/images/character_sheet/clicked_tab.png')
    : require('@/assets/images/character_sheet/tabButton.png');
  return {
    background: `url(${imagePath}) no-repeat center center`,
    backgroundSize: 'cover',
  };
}
</script>

<style scoped>
.action-tab {
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.tab-buttons {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.tab-button {
  flex: 1;
  padding: 10px;
  color: #fff;
  border: none;
  cursor: pointer;
  margin-right: 10px;
  transition: background-color 0.3s ease;
}

.action-list {
  display: flex;
  flex-direction: column;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.action-item {
  background-color: #1d1707;
  padding: 15px;
  border-radius: 5px;
  color: #fff;
  text-align: center;
}

.action-item h3 {
  margin-top: 0;
  font-size: 1.5rem;
  text-align: center;
}

.description {
  text-align: left;
  margin: 10px 0;
}

.action-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
}

.detail-box {
  background-color: #2a210f;
  padding: 10px;
  border-radius: 5px;
  width: 100%;
}

.detail-box strong {
  display: block;
  margin-bottom: 5px;
}

.detail-box ul {
  list-style: none;
  padding-left: 0;
  margin: 5px 0 0 0;
}

.detail-box ul li {
  margin-bottom: 5px;
}
</style>
