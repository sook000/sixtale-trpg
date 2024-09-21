// store/state.js
import { ref } from 'vue';

export const selectedPlayMemberID = ref(null); // 전역 상태로 선택된 플레이어 ID를 저장
export const selectedUserJobID = ref(null); // 전역 상태로 선택된 유저의 직업 ID를 저장

// 주사위 정보 상태 추가
export const selectedDice = ref({
  type: null,  // 주사위 타입 (예: 6, 20 등)
  count: 0,    // 주사위 개수
});

// 변이 함수 추가
export function setSelectedDice(diceType, diceCount) {
  selectedDice.value.type = diceType;
  selectedDice.value.count = diceCount;
}
