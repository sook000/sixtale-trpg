<template>
  <div class="calendar">
    <el-calendar v-model="value">
      <template #header="{ date }">
        <el-button-group>
          <el-button size="small" @click="changeMonth(-1)" class="arrow-button">
            <img :src="arrowImageLeft" alt="Previous Month" />
          </el-button>
          <span>{{ formatDate(date) }}</span>
          <el-button size="small" @click="changeMonth(1)" class="arrow-button">
            <img :src="arrowImageRight" alt="Next Month" />
          </el-button>
        </el-button-group>
      </template>
      <template #date-cell="{ data }">
        <button 
          class="date-button" 
          :class="{ 'is-today': isToday(data.date), 'selected-date': isSelected(data.date) }"
          @click="selectDate(data.date)">
          {{ new Date(data.date).getDate() }}
        </button>
      </template>
    </el-calendar>

    <!-- Bootstrap Modal -->
    <div class="modal fade" id="eventModal" tabindex="-1" aria-labelledby="eventModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content custom-modal" :style="modalStyle">
          <div class="modal-header custom-modal-header">
            <img :src="titleImage" alt="Background" class="modal-title-image" />
            <div class="overlay-text">일정 생성</div>
          </div>
          <div class="modal-body">
            <p class="modal-date">{{ selectedDate }}</p>
            <div class="time-selection-row">
              <div class="time-selection">
                <label for="startTime">시작 시간</label>
                <div class="time-select">
                  <select id="startTime" v-model="startTime" :style="dropdownStyle">
                    <option v-for="time in timeOptions" :key="time" :value="time">{{ time }}</option>
                  </select>
                  <img :src="dropdownArrowBox" alt="Dropdown" class="dropdown-arrow" />
                </div>
              </div>
              <div class="time-selection">
                <label for="endTime">종료 시간</label>
                <div class="time-select">
                  <select id="endTime" v-model="endTime" :style="dropdownStyle">
                    <option v-for="time in timeOptions" :key="time" :value="time">{{ time }}</option>
                  </select>
                  <img :src="dropdownArrowBox" alt="Dropdown" class="dropdown-arrow" />
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer custom-modal-footer">
            <button type="button" class="btn footer-button" data-bs-dismiss="modal">
              <img :src="cancelImage" alt="닫기" />
              <span class="button-text">닫기</span>
            </button>
            <button type="button" class="btn footer-button" @click="saveEvent">
              <img :src="okImage" alt="저장" />
              <span class="button-text">저장</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap';

// 이미지 경로를 정확하게 설정합니다.
import scheduleModal from '@/assets/images/room/calendar_modal/background.png';
import titleImage from '@/assets/images/room/calendar_modal/title.png';
import arrowImage from '@/assets/images/room/calendar_modal/arrow.png';
import arrowImageRight from '@/assets/images/room/calendar_modal/Arrow_Right.png';
import arrowImageLeft from '@/assets/images/room/calendar_modal/Arrow_Left.png';
import cancelImage from '@/assets/images/room/calendar_modal/cancel.png';
import dropdownArrowBox from '@/assets/images/room/calendar_modal/dropdown_arrow_box.png';
import dropdownBox from '@/assets/images/room/calendar_modal/dropdown_box.png';
import okImage from '@/assets/images/room/calendar_modal/ok.png';
import timeImage from '@/assets/images/room/calendar_modal/time.png';
import timeBackground from '@/assets/images/room/calendar_modal/time_background.png';

const value = ref(new Date());
const selectedDate = ref('');
const startTime = ref('00:00');
const endTime = ref('00:00');
const selectedDates = ref([]);

const timeOptions = computed(() => {
  const times = [];
  for (let hour = 0; hour < 24; hour++) {
    for (let minute = 0; minute < 60; minute += 30) {
      const formattedHour = hour.toString().padStart(2, '0');
      const formattedMinute = minute.toString().padStart(2, '0');
      times.push(`${formattedHour}:${formattedMinute}`);
    }
  }
  return times;
});

const formatDate = (date) => {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = (d.getMonth() + 1).toString().padStart(2, '0');
  const day = d.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const changeMonth = (increment) => {
  const newDate = new Date(value.value);
  newDate.setMonth(newDate.getMonth() + increment);
  value.value = newDate;
};

const goToToday = () => {
  value.value = new Date();
};

const showModal = () => {
  const modal = new bootstrap.Modal(document.getElementById('eventModal'));
  modal.show();
};

const selectDate = (date) => {
  selectedDate.value = formatDate(date);
  showModal();
};

const saveEvent = () => {
  console.log(`날짜: ${selectedDate.value}, 시작 시간: ${startTime.value}, 종료 시간: ${endTime.value}`);
  const modal = bootstrap.Modal.getInstance(document.getElementById('eventModal'));
  modal.hide();
};

const isToday = (date) => {
  const today = new Date();
  return (
    date.getFullYear() === today.getFullYear() &&
    date.getMonth() === today.getMonth() &&
    date.getDate() === today.getDate()
  );
};

const isSelected = (date) => {
  return formatDate(date) === selectedDate.value;
};

// 모달 스타일에 배경 이미지를 설정합니다.
const modalStyle = computed(() => ({
  backgroundImage: `url(${scheduleModal})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundRepeat: 'no-repeat',
}));

const dropdownStyle = computed(() => ({
  backgroundImage: `url(${dropdownBox})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundRepeat: 'no-repeat',
}));
</script>

<style scoped>
.calendar {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: #291707;
  border-radius: 10px;
}

:deep(.el-calendar) {
  --el-calendar-border: none;
  --el-calendar-header-border-bottom: none;
  background-color: transparent;
}

:deep(.el-calendar__header) {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #ffffff;
}

.arrow-button {
  background-color: transparent !important;
  border-color: transparent !important;
  color: #ffffff;
}

:deep(.el-calendar-table thead th) {
  color: #ffffff;
  position: relative;
}

:deep(.el-calendar-table thead th::before) {
  content: attr(data-content);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  text-align: center;
}

:deep(.el-calendar-table thead th:nth-child(1)) {
  visibility: hidden;
}
:deep(.el-calendar-table thead th:nth-child(1)::before) {
  content: '일';
  visibility: visible;
}

:deep(.el-calendar-table thead th:nth-child(2)) {
  visibility: hidden;
}
:deep(.el-calendar-table thead th:nth-child(2)::before) {
  content: '월';
  visibility: visible;
}

:deep(.el-calendar-table thead th:nth-child(3)) {
  visibility: hidden;
}
:deep(.el-calendar-table thead th:nth-child(3)::before) {
  content: '화';
  visibility: visible;
}

:deep(.el-calendar-table thead th:nth-child(4)) {
  visibility: hidden;
}
:deep(.el-calendar-table thead th:nth-child(4)::before) {
  content: '수';
  visibility: visible;
}

:deep(.el-calendar-table thead th:nth-child(5)) {
  visibility: hidden;
}
:deep(.el-calendar-table thead th:nth-child(5)::before) {
  content: '목';
  visibility: visible;
}

:deep(.el-calendar-table thead th:nth-child(6)) {
  visibility: hidden;
}
:deep(.el-calendar-table thead th:nth-child(6)::before) {
  content: '금';
  visibility: visible;
}

:deep(.el-calendar-table thead th:nth-child(7)) {
  visibility: hidden;
}
:deep(.el-calendar-table thead th:nth-child(7)::before) {
  content: '토';
  visibility: visible;
}

:deep(.el-calendar__body) {
  padding: 12px 20px 35px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

:deep(.el-calendar-table) {
  width: 100%;
  table-layout: fixed;
}

:deep(.el-calendar-table td) {
  border: none;
  padding: 5px 0;
  text-align: center;
}

:deep(.el-calendar-table .el-calendar-day) {
  color: #ffffff; /* 기본 날짜 색상 */
}

.date-button {
  background: none;
  border: none;
  color: inherit;
  font: inherit;
  cursor: pointer;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 50%;
  display: inline-flex;
  justify-content: center;
  align-items: center;
}

.date-button.is-today {
  color: red !important; /* 오늘 날짜 글씨 색상 */
}

.date-button.selected-date {
  background-color: wheat;
  color: black;
}

.custom-modal {
  background-color: #1e1e1e;
  color: #fff;
  border-radius: 10px;
  padding: 0; /* Padding 조정 */
}

.modal-content {
  background-color: #291707;
  color: #ffffff;
}

.custom-modal-header {
  position: relative;
  padding-top: 10px; /* Add padding to move content down */
}

.modal-title-image {
  width: 100%;
  height: auto;
  margin-top: -7px; /* Adjust margin to move image up */
}

.overlay-text {
  position: absolute;
  top: 8px; /* Adjust as needed */
  left: 50%;
  transform: translateX(-50%);
  color: #ffffff;
  font-size: 20px; /* Adjust as needed */
  font-weight: bolder;
}

.modal-header, .modal-footer {
  border: none; /* 경계선 제거 */
}

.modal-title {
  color: #ffffff;
}

.custom-modal-footer {
  display: flex;
  justify-content: center;
  align-items: center;
}

.footer-button {
  position: relative;
  margin: 0 10px; /* Adjust spacing as needed */
}

.footer-button img {
  display: block;
}

.button-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  pointer-events: none;
  font-size: 13px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #fff;
}

.btn-secondary, .btn-primary {
  background-color: #5f5f5f;
  border-color: #5f5f5f;
}

.btn-primary {
  background-color: #c3a084;
  border-color: #c3a084;
}

.time-selection-row {
  display: flex;
  justify-content: center;
  align-items: center;
}

.time-selection {
  display: flex;
  align-items: center;
  margin: 10px 20px;
}

label {
  margin-right: 10px;
}

select {
  background-color: #444444;
  color: #ffffff;
  border: none;
  padding: 5px;
  border-radius: 5px;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
}

.dropdown-arrow {
  width: 20px;
  height: 20px;
  position: absolute;
  right: 10px;
  pointer-events: none;
}

.modal-date {
  text-align: center;
  font-size: 1.5em;
  margin-bottom: 20px;
}
</style>
