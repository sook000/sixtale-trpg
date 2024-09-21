import axios from "axios";
import Cookies from "js-cookie";

const BASE_URL = "/api/v1/scenarios";

// 토큰을 로컬 스토리지에서 가져오기
const getAccessToken = () => {
  return Cookies.get("access-token");
};

// 공통 헤더 설정
const getHeaders = () => {
  const accessToken = getAccessToken();
  const headers = {
    "Content-Type": "application/json",
  };
  if (accessToken) {
    headers["Authorization"] = `Bearer ${accessToken}`;
  }
  return headers;
};

// 시나리오 목록 조회
export const getScenarioList = async (
  genre,
  title,
  page,
  size,
  sort,
  order
) => {
  try {
    const response = await axios.get(
      `${BASE_URL}?genre=${genre}&title=${title}&page=${page}&size=${size}&sort=${sort}&order=${order}`,
      {
        headers: getHeaders(),
      }
    );
    return response.data.data;
  } catch (error) {
    console.error("Error fetching scenario info:", error);
    throw error;
  }
};

export const likeScenario = async (scenarioID) => {
  try {
    const response = await axios.post(`${BASE_URL}/${scenarioID}/like`, null, {
      headers: getHeaders(),
    });
    return response.data;
  } catch (error) {
    console.error("Error Scenario Like:", error);
    throw error;
  }
};

export const unlikeScenario = async (scenarioID) => {
  try {
    const response = await axios.delete(`${BASE_URL}/${scenarioID}/like`, {
      headers: getHeaders(),
    });
    return response.data;
  } catch (error) {
    console.error("Error Scenario Unlike:", error);
    throw error;
  }
};

// 방 생성 모달용
export const getScenarioListForCreateRoom = async () => {
  try {
    const response = await getScenarioList('', '', 0, 100, 'updatedAt', 'desc');
    if (response && Array.isArray(response.scenarioList)) {
      return response.scenarioList.map(scenario => ({
        id: scenario.id,
        title: scenario.title
      }));
    }
  } catch (error) {
    console.error("Error fetching scenario list for room:", error);
  }
  
  // 오류 발생 시 또는 데이터가 없을 경우 기본값 반환
  return [{ id: 1, title: "기본 시나리오" }];
};

export const getAllScenarios = async () => {
  let allScenarios = [];
  let page = 0;
  const size = 100;
  
  while (true) {
    const response = await getScenarioList('', '', page, size, 'id', 'asc');
    allScenarios = allScenarios.concat(response.scenarioList);
    
    if (response.scenarioList.length < size) {
      break; // 마지막 페이지에 도달
    }
    page++;
  }
  
  return allScenarios.map(scenario => ({
    id: scenario.id,
    title: scenario.title
  }));
};

export const getScenarioListForRoomWithErrorHandling = async () => {
  try {
    const scenarios = await getScenarioListForRoom();
    if (scenarios.length === 0) {
      console.warn("No scenarios found for room creation");
    }
    return scenarios;
  } catch (error) {
    console.error("Failed to fetch scenarios for room creation:", error);
    // 여기에서 사용자에게 에러 메시지를 표시하거나 기본 시나리오 목록을 반환할 수 있습니다.
    return [{ id: 0, title: "기본 시나리오" }];
  }
};