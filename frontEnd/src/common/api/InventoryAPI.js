import axios from 'axios';

const BASE_SHEETS_URL = '/api/v1/rooms';

// 토큰을 로컬 스토리지에서 가져오기
const getAccessToken = () => {
  return localStorage.getItem('accessToken');
};

// 공통 헤더 설정
const getHeaders = () => {
  const accessToken = getAccessToken();
  const headers = {
    'Content-Type': 'application/json'
  };
  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`;
  }
  return headers;
};

// 캐릭터 장비 목록 조회
export const getInventory = async (roomID, playMemberID) => {
  const headers = getHeaders();
  const url = `${BASE_SHEETS_URL}/${roomID}/sheets/${playMemberID}/equipment`;
  console.log('Fetching inventory:', url, headers);

  try {
    const response = await axios.get(url, {
      headers
    });
    console.log('Response:', response);
    return response.data.data;
  } catch (error) {
    console.error('Error fetching inventory:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
    }
    throw error;
  }
};

// 룰에서 장비 목록 조회
export const getEquipmentList = async (ruleID) => {
  const headers = getHeaders();
  const url = `/api/v1/rules/${ruleID}/equipments`;
  console.log('Fetching equipment list:', url, headers);

  try {
    const response = await axios.get(url, {
      headers
    });
    console.log('Response:', response);
    return response.data.data.equipments;
  } catch (error) {
    console.error('Error fetching equipment list:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
    }
    throw error;
  }
};

// 캐릭터 장비 추가
export const addEquipment = async (roomID, playMemberID, equipmentData) => {
  const headers = getHeaders();
  const url = `${BASE_SHEETS_URL}/${roomID}/sheets/${playMemberID}/equipment`;
  console.log('Adding equipment:', url, headers);

  try {
    const response = await axios.post(url, equipmentData, {
      headers
    });
    console.log('Response:', response);
    return response.data;
  } catch (error) {
    console.error('Error adding equipment:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
    }
    throw error;
  }
};

// 캐릭터 장비 삭제
export const deleteEquipment = async (roomID, playMemberID, equipmentID) => {
  const headers = getHeaders();
  const url = `${BASE_SHEETS_URL}/${roomID}/sheets/${playMemberID}/equipment/${equipmentID}`;
  console.log('Deleting equipment:', url, headers);

  try {
    const response = await axios.delete(url, {
      headers
    });
    console.log('Response:', response);
    return response.data;
  } catch (error) {
    console.error('Error deleting equipment:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
    }
    throw error;
  }
};


// 캐릭터 장비 수량 수정
export const updateEquipmentCount = async (roomID, playMemberID, equipmentData) => {
  const headers = getHeaders();
  const url = `${BASE_SHEETS_URL}/${roomID}/sheets/${playMemberID}/equipment`;
  
  console.log('Updating equipment count:', url, headers, equipmentData);

  try {
    const response = await axios.put(url, equipmentData, {
      headers
    });
    console.log('Response:', response);
    return response.data;
  } catch (error) {
    console.error('Error updating equipment count:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
    }
    throw error;
  }
};

// 골드 수정
export const updateGold = async (roomID, playMemberID, newGoldAmount) => {
  const headers = getHeaders();
  const url = `${BASE_SHEETS_URL}/${roomID}/sheets/${playMemberID}/gold`;

  console.log('Updating gold:', url, headers, newGoldAmount);

  try {
    const response = await axios.put(url, { currentMoney: newGoldAmount }, {
      headers
    });
    console.log('Response:', response);
    return response.data;
  } catch (error) {
    console.error('Error updating gold:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
    }
    throw error;
  }
};