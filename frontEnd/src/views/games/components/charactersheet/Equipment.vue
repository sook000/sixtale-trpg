<template>
  <div class="equipment-selection-container">
    <div class="equipment-decripton-title">
      <p>캐릭터 시트에 들어갈 장비를 선택하세요 <br> 종류당 1개씩만 선택 가능합니다.</p>
      <p>하중은 능력치 탭에서 선택한 <br> 근력 수치의 영향을 받습니다. <br> 
        HP는 체력수치의 영향을 받습니다 <br> 능력치를 먼저 입력하세요! </p>
      <p>현재 하중: {{ calculateCurrentWeight }} / {{ calculateLimitWeight }}</p>
      <p>최대 HP: {{ calculateLimitHP }}</p>
    </div>
    <div v-if="equipmentGroups.length > 0">
      <div class="equipment-group" v-for="(group, groupIndex) in equipmentGroups" :key="groupIndex">
        <div class="equipment-category">
          <div class="title-container" :style="getCategoryStyle()">
            <span class="title-text" v-if="group[0] && group[0].equipmentType">{{ group[0].equipmentType.name }}</span>
            <span class="title-text" v-else>Unknown</span>
          </div>
          <div class="equipment-cards">
            <div 
              v-for="(item, index) in group" 
              :key="item.id" 
              :class="['action-card', { selected: isSelected(item.equipmentType.id, item.id) }]"
              @click="selectEquipment(item.equipmentType.id, item)"
            >
              <img :src="item.imageUrl || 'path/to/default-image.png'" alt="장비 이미지" class="equipment-image">
              <div class="equipment-info">
                <span class="equipment-name">{{ item.name }}</span>
                <br>
                <span class="equipment-description">{{ item.description }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else>
      <p>장비를 불러오는 중 오류가 발생했습니다. 다시 시도해 주세요.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { fetchJobList } from '@/common/api/JobAPI.js';

const props = defineProps({
  formData: Object,
  currentOptions: Array,
  jobId: Number,
  ruleId: Number
});

const emit = defineEmits(['update:character-equipment', 'update:current-hp', 'update:limit-hp']);

const selectedEquipment = reactive({});
const jobWeight = ref(0);
const jobHP = ref(0); // 직업의 기본 HP
const currentWeight = ref(0); // 현재 하중

// 부모 컴포넌트로부터 전달된 characterEquipment 값을 로컬 상태로 초기화
onMounted(async () => {
  await fetchJobAttributes();
  initializeSelectedEquipment();
  resetCurrentWeight(); // 현재 하중 초기화 및 계산
  updateHP(); // 초기 HP 계산 및 부모에 반영
});

// 부모에서 전달된 characterEquipment를 통해 선택된 장비 초기화
const initializeSelectedEquipment = () => {
  if (props.formData.characterEquipment && Array.isArray(props.formData.characterEquipment)) {
    props.formData.characterEquipment.forEach(equipment => {
      if (equipment.equipmentType) {
        selectedEquipment[equipment.equipmentType.id] = equipment;
      }
    });
  }
};

// 직업의 무게와 HP 가져오기
const fetchJobAttributes = async () => {
  try {
    const jobList = await fetchJobList(props.ruleId);
    const job = jobList.find(job => job.id === props.jobId);
    if (job) {
      jobWeight.value = job.weight;
      jobHP.value = job.hp; // 직업의 기본 HP 저장
      updateHP(); // 직업의 HP에 따라 초기 설정
    }
  } catch (error) {
    console.error('Error fetching job attributes:', error);
  }
};

// 한계 하중 계산 (직업의 기본 무게 + 근력 스탯)
const calculateLimitWeight = computed(() => {
  const strengthStat = props.formData.stat.find(stat => stat.statID === 1);
  return Number(jobWeight.value) + Number(strengthStat ? strengthStat.statValue : 0);
});

// 최대 HP 계산 (직업의 기본 HP + 체력 스탯)
const calculateLimitHP = computed(() => {
  const staminaStat = props.formData.stat.find(stat => stat.statID === 3);
  return Number(jobHP.value) + Number(staminaStat ? staminaStat.statValue : 0);
});

// 현재 하중 계산 (선택된 아이템의 무게 합산)
const calculateCurrentWeight = computed(() => {
  return currentWeight.value;
});

// 현재 하중 초기화 및 다시 계산
const resetCurrentWeight = () => {
  currentWeight.value = 0;
  Object.values(selectedEquipment).forEach(equipment => {
    currentWeight.value += equipment.weight || 0;
  });
  updateParentCurrentWeight(); // 부모의 하중을 덮어씌움
};

// 부모의 현재 하중을 덮어씌우는 함수
const updateParentCurrentWeight = () => {
  props.formData.currentWeight = currentWeight.value;
  props.formData.limitWeight = calculateLimitWeight.value; 
  emit('update:character-equipment', Object.values(selectedEquipment));
  console.log('선택된 장비들:', Object.values(selectedEquipment));
  console.log('Updated currentWeight:', props.formData.currentWeight); // 로그 추가
};

// 부모 컴포넌트에 한계 HP와 현재 HP 업데이트
const updateHP = () => {
  const limitHp = calculateLimitHP.value;
  props.formData.limitHp = limitHp;  // 한계 HP를 직접 부모의 formData에 할당
  props.formData.currentHp = limitHp;  // 현재 HP도 한계 HP로 설정
  console.log('Updated limitHp:', props.formData.limitHp); // 로그 추가
  console.log('Updated currentHp:', props.formData.currentHp); // 로그 추가
};



// 선택된 아이템의 무게를 계산하고 업데이트
const selectEquipment = (typeId, item) => {
  if (selectedEquipment[typeId]) {
    // 기존 선택된 아이템의 무게를 제거
    currentWeight.value -= selectedEquipment[typeId].weight || 0;
  }

  // 새로운 아이템을 선택
  selectedEquipment[typeId] = {
    equipmentId: item.id,
    equipmentType: {
      id: typeId,
    },
    currentCount: 1,
    weight: item.weight
  };

  // 새로운 아이템의 무게를 추가
  currentWeight.value += item.weight || 0;

  // 부모 컴포넌트로 업데이트
  updateParentCurrentWeight();
};

// 선택된 장비인지 확인하는 함수
const isSelected = (typeId, itemId) => {
  return selectedEquipment[typeId] && selectedEquipment[typeId].equipmentId === itemId;
};

// equipmentGroups 계산
const equipmentGroups = computed(() => {
  if (!Array.isArray(props.currentOptions) || props.currentOptions.length === 0) {
    return [];
  }
  return Object.values(
    props.currentOptions.reduce((groups, item) => {
      if (item.equipmentType && item.equipmentType.id) {
        const groupId = item.equipmentType.id;
        if (!groups[groupId]) {
          groups[groupId] = [];
        }
        groups[groupId].push(item);
      }
      return groups;
    }, {})
  );
});

const categoryImage = require('@/assets/images/character_sheet/Vector.png');
const getCategoryStyle = () => {
  return {
    backgroundImage: `url(${categoryImage})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    position: 'relative',
    width: '350px',
    height: '60px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  };
};

// watch로 stat 배열이 변경될 때 한계 HP를 자동 업데이트
watch(() => props.formData.stat, () => {
  updateHP();
});

</script>


<style scoped>
.equipment-decripton-title {
  background: rgba(0, 0, 0, 0.5);
  border-radius: 5px;
  font-size: 2rem;
  font-weight: bold;
  text-align: center;
  padding: 10px;
  margin-bottom: 20px;
}

.equipment-selection-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.equipment-group {
  margin-bottom: 20px;
  width: 80%;
  padding: 5%;
  max-width: 1200px;
  text-align: left;
}

.equipment-category {
  margin-bottom: 20px;
}

.action-card {
  background: rgba(0, 0, 0, 0.5);
  border-radius: 5px;
  padding: 10px;
  box-sizing: border-box;
  cursor: pointer;
  transition: background 0.3s, transform 0.3s, box-shadow 0.3s, border 0.3s;
}

.action-card:hover {
  background: rgba(0, 0, 0, 0.7);
}

.action-card.selected {
  background: rgba(0, 0, 0, 0.7);
  border: 2px solid #fff;
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.8);
}

.title-container {
  position: relative;
  margin-bottom: 15px;
}

.title-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #fff;
  font-size: 1.2rem;
  white-space: nowrap;
}

.equipment-cards {
  display: flex;
  flex-wrap: nowrap;
  justify-content: flex-start;
  gap: 10px;
  overflow-x: hidden;
  overflow-y: hidden;
}

.equipment-image {
  width: 100%;
  height: auto;
  max-height: 150px;
  margin-bottom: 10px;
  transition: transform 0.3s;
}

.action-card.selected .equipment-image {
  transform: scale(1.1);
}

.equipment-info {
  text-align: center;
}

.equipment-name {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 10px;
}

.equipment-description {
  font-size: 1rem;
  color: #ddd;
}
</style>
