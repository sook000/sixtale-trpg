<template>
  <div>
    <div class="jobs-board">
      <JobCard
        v-for="job in jobs"
        :key="job.id"
        :name="job.name"
        :image="job.imageURL"
        :description="job.description"
        :selectedBy="job.selectedBy"
        :roomId="Number(roomID)"
        :disabled="isJobDisabled(job.id)"
        @select-card="!isJobDisabled(job.id) && openCharacterSheet(job.id)"
        @delete-card="handleDeleteCard(job.id)"
      />
    </div>
    <CharacterSheetModal
      v-if="showModal"
      :job="selectedJob"
      @close="closeModal"
      @save-success="handleSaveSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import JobCard from './JobCard.vue';
import CharacterSheetModal from './CharacterSheetModal.vue';
import { fetchJobList } from '@/common/api/JobAPI.js';
import { getRoomInfo } from '@/common/api/RoomsAPI.js';

const props = defineProps({
  createdJobs: {
    type: Array,
    required: true,
  },
});

const emit = defineEmits(['job-selected']);

const route = useRoute();
const roomID = route.params.roomId;

const jobs = ref([]);
const showModal = ref(false);
const selectedJob = ref(null);
const ruleId = ref(null);

const fetchRoomInfo = async () => {
  try {
    if (!roomID) throw new Error("roomID가 정의되지 않았습니다.");
    const roomInfo = await getRoomInfo(roomID);
    ruleId.value = roomInfo.ruleID;
    await loadJobs();
  } catch (error) {
    console.error('Error fetching room info or job list:', error);
  }
};

const loadJobs = async () => {
  if (!ruleId.value) return;
  try {
    jobs.value = await fetchJobList(ruleId.value);
    console.log('Fetched Jobs:', jobs.value);
  } catch (error) {
    console.error('직업 목록을 불러오는 중 오류가 발생했습니다.', error);
  }
};

const openCharacterSheet = (jobID) => {
  selectedJob.value = jobs.value.find(j => j.id === jobID);
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const handleSaveSuccess = (jobId) => {
  emit('job-selected', jobId);
  closeModal();
};

const handleDeleteCard = (jobId) => {
  // 삭제된 직업 ID를 createdJobs에서 제거
  const index = props.createdJobs.indexOf(jobId);
  if (index !== -1) {
    props.createdJobs.splice(index, 1);
  }
  emit('job-deleted', jobId); // 삭제 후 상위 컴포넌트에 알림
};

const isJobDisabled = (jobId) => {
  return props.createdJobs.includes(jobId);
};

onMounted(fetchRoomInfo);
</script>



<style scoped>
.jobs-board {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-auto-rows: minmax(min-content, max-content);
  gap: 20px;
  padding: 20px;
  max-height: 90vh; /* 뷰포트 높이의 70%로 제한 */
  overflow-y: auto; /* 내용이 넘칠 경우 스크롤 표시 */
  align-content: start; /* 컨텐츠를 위쪽으로 정렬 */
}

.job-card {
  width: 100%;
  aspect-ratio: 1.5 / 1.25; /* 카드의 비율을 3:4로 유지 */
  max-width: 300px; /* 카드의 최대 너비 설정 */
  justify-self: center;
}

@media (max-width: 1200px) {
  .jobs-board {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .jobs-board {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .jobs-board {
    grid-template-columns: 1fr;
  }
}
</style>
