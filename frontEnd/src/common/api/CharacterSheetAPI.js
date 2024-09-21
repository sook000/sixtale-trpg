import axios from 'axios';

const BASE_SHEETS_URL = '/api/v1/rooms';

// 쿠키에서 토큰을 가져오기
const getAccessTokenFromCookie = () => {
  const cookieName = 'access-token';
  const cookieString = document.cookie.split('; ').find(row => row.startsWith(`${cookieName}=`));
  return cookieString ? cookieString.split('=')[1] : null;
};

// 공통 헤더 설정
const getHeaders = () => {
  const accessToken = getAccessTokenFromCookie();  // 쿠키에서 토큰 가져오기
  const headers = {
    'Content-Type': 'application/json'
  };
  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`;
  } else {
    console.error('No access token found in cookies. Please log in.');
  }
  return headers;
};

// 캐릭터 시트 조회
export const getCharacterSheet = async (roomId, playMemberID) => {
  const headers = getHeaders();
  const url = `/api/v1/rooms/${roomId}/sheets/${playMemberID}`;
  console.log(`Fetching character sheet from: ${url}`);

  try {
    const response = await axios.get(url, { headers });
    return response.data.data;
  } catch (error) {
    console.error('Error fetching character sheet:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
    }
    // 기본 상태 반환
    return null;
  }
};

// 캐릭터 시트 생성
export const createCharacterSheet = async (roomID, characterData) => {
  try {
    const response = await axios.post(`${BASE_SHEETS_URL}/${roomID}/sheets`, characterData, {
      headers: getHeaders()
    });
    return response.data;
  } catch (error) {
    console.error('Error creating character sheet:', error);
    throw error;
  }
};

// 캐릭터 시트 업데이트 - 플레이 전 (캐릭터 시트작성할 때)
export const updateCharacterSheet = async (roomID, playMemberID, updatedData) => {
  try {
    // FormData 객체 생성
    const formData = new FormData();

    // 파일이 있는 경우 추가
    if (updatedData.files) {
      for (let file of updatedData.files) {
        formData.append('files', file);
      }
    }

    // 나머지 데이터를 characterSheetRequest로 추가
    formData.append('characterSheetRequest', new Blob([JSON.stringify(updatedData.characterSheetRequest)], {
      type: 'application/json'
    }));

    const response = await axios.put(`${BASE_SHEETS_URL}/${roomID}/sheets/${playMemberID}`, formData, {
      headers: {
        ...getHeaders(),
        'Content-Type': 'multipart/form-data' // FormData에 맞는 Content-Type 설정
      }
    });

    return response.data;
  } catch (error) {
    console.error('Error updating character sheet:', error);
    throw error;
  }
};


// 캐릭터 시트 업데이트 - 플레이 후 (스탯 수정 시)
export const updateCharacterSheet2 = async (roomID, playMemberID, updatedData) => {
  try {
    const response = await axios.put(`${BASE_SHEETS_URL}/${roomID}/sheets/${playMemberID}/sheet`, updatedData, {
      headers: getHeaders()
    });
    return response.data;
  } catch (error) {
    console.error('Error updating character sheet:', error);
    throw error;
  }
};

// 캐릭터 시트 삭제 요청 추가
export const deleteCharacterSheet = async (roomId) => {
  try {
    const response = await axios.delete(`${BASE_SHEETS_URL}/${roomId}/sheets`, {
      headers: getHeaders()
    });
    return response.data;
  } catch (error) {
    console.error('Error deleting character sheet:', error);
    throw error;
  }
};