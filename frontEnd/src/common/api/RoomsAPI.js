import axios from "axios";
import Cookies from "js-cookie";

const BASE_URL = "/api/v1/rooms";

// 토큰을 로컬 스토리지에서 가져오기
const getAccessToken = () => {
  return localStorage.getItem("accessToken");
};

// 공통 헤더 설정
const getHeaders = () => {
  const accessToken = Cookies.get("access-token");
  return {
    "Content-Type": "application/json",
    ...(accessToken && { Authorization: `Bearer ${accessToken}` }),
  };
};
// const getHeaders = () => {
//   const accessToken = getAccessToken();
//   const headers = {
//     'Content-Type': 'application/json'
//   };
//   if (accessToken) {
//     headers['Authorization'] = `Bearer ${accessToken}`;
//   }
//   return headers;
// };

export const createAndEnterRoom = async (roomData) => {
  try {
    const headers = getHeaders();
    console.log("Request headers:", headers);
    const response = await axios.post(BASE_URL, roomData, { headers });
    console.log("Room created:", response.data);

    const createdRoom = response.data.data;
    if (createdRoom && createdRoom.id) {
      const enterResponse = await enterRoomWithoutPassword(
        createdRoom.id,
        roomData.isLocked,
        roomData.password
      );
      console.log("Entered room:", enterResponse);
      return { ...createdRoom, enterResponse };
    } else {
      throw new Error("Failed to create room: Invalid response structure");
    }
  } catch (error) {
    console.error("Error in createAndEnterRoom:", error);
    throw error;
  }
};

const enterRoomWithCheck = async (roomId, password = null) => {
  try {
    const roomInfo = await getRoomInfo(roomId);
    console.log("Room info:", roomInfo);

    if (roomInfo.currentCount >= roomInfo.maxCount) {
      throw new Error("방이 가득 찼습니다.");
    }

    if (!user.value.id) {
      throw new Error("사용자 정보를 가져올 수 없습니다.");
    }

    const response = await enterRoom(roomId, user.value.id, password);
    console.log("Enter room response:", response);

    if (response.statusCode === 201) {
      console.log("Successfully entered the room");
      return true;
    } else {
      console.error("Unexpected response:", response);
      throw new Error(response.responseMessage || "방 입장에 실패했습니다.");
    }
  } catch (error) {
    console.error("Error entering room:", error);
    if (error.response && error.response.data) {
      console.error("Server error details:", error.response.data);
    }
    throw error;
  }
};

// 게임 방 생성
export const createRoom = async (roomData) => {
  try {
    const headers = getHeaders();
    console.log("Request headers:", headers);
    console.log("BASE_URL++++++", BASE_URL);
    const response = await axios.post(`${BASE_URL}`, roomData, { headers });
    console.log("Room created:", response.data);

    return response.data.data; // 생성된 방 정보 반환
  } catch (error) {
    console.error("Error in createRoom:", error);
    throw error;
  }
};

// 게임 방 목록 조회
export const getRoomList = async (status = "", page = 0, size = 15, title = "") => {
  try {
    const response = await axios.get(
      `${BASE_URL}?status=${status}&page=${page}&size=${size}&title=${title}`,
      {
        headers: getHeaders(),
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error fetching room list:", error);
    throw error;
  }
};
// 게임 방 정보 조회
export const getRoomInfo = async (roomId) => {
  try {
    const response = await axios.get(`${BASE_URL}/${roomId}`, {
      headers: getHeaders(),
    });
    return response.data.data;
  } catch (error) {
    console.error("Error fetching room info:", error);
    throw error;
  }
};


// 게임 방 상태 변경
export const updateRoomStatus = async (roomID, status) => {
  try {
    const response = await axios.patch(`${BASE_URL}/${roomID}/status`, { status }, {
      headers: getHeaders()
    });
    return response.data;
  } catch (error) {
    console.error('Error updating room status:', error);
    throw error;
  }
};


// 게임방에서 나가기
export const leaveRoom = async (roomId) => {
  try {
    const response = await axios.delete(`${BASE_URL}/${roomId}/players`, {
      headers: getHeaders(),
    });
    return response.data;
  } catch (error) {
    console.error("Error leaving the room:", error);
    if (error.response) {
      console.error("Server response:", error.response.data);
    }
    throw error;
  }
};

// 맵 목록 조회
export const getMapList = async (roomId) => {
  try {
    const response = await axios.get(`${BASE_URL}/${roomId}/maps`, {
      headers: getHeaders(),
    });
    return response.data.data;
  } catch (error) {
    console.error("Error fetching map list:", error);
    throw error;
  }
};

// 맵 정보 조회
export const getMapInfo = async (roomId, mapId) => {
  try {
    const response = await axios.get(`${BASE_URL}/${roomId}/maps/${mapId}`, {
      headers: getHeaders(),
    });
    return response.data.data;
  } catch (error) {
    console.error("Error fetching map info:", error);
    throw error;
  }
};

// 맵 장소 이벤트 목록 조회
export const getMapPlace = async (roomId, mapId) => {
  try {
    const response = await axios.get(`${BASE_URL}/${roomId}/maps/${mapId}/places`, {
      headers: getHeaders(),
    });
    return response.data.data;
  } catch (error) {
    console.error("Error fetching map place info:", error);
    throw error;
  }
};

// 맵 NPC 이벤트 목록 조회
export const getMapNpcs = async (roomId, mapId) => {
  try {
    const response = await axios.get(`${BASE_URL}/${roomId}/maps/${mapId}/npcs`, {
      headers: getHeaders(),
    });
    return response.data.data;
  } catch (error) {
    console.error("Error fetching NPC event list:", error);
    throw error;
  }
};

// export const enterRoom = async (roomId, password = null) => {
//   try {
//     const url = `${BASE_URL}/${roomId}/players`;
//     const data = password ? { password } : {};
//     const headers = getHeaders();

//     console.log('Entering room with:', { roomId, password: password ? '[REDACTED]' : null });
//     console.log('Request URL:', url);
//     console.log('Request data:', data);

//     const response = await axios.post(url, data, { headers });
//     console.log('Enter room response:', response.data);
//     return response.data;
//   } catch (error) {
//     if (error.response && error.response.status === 400 && error.response.data.responseMessage.includes('이미 입장한 사용자')) {
//       console.log('User already in the room. Proceeding to enter.');
//       return { statusCode: 200, responseMessage: '이미 방에 입장해 있습니다.' };
//     }
//     console.error('Error entering room:', error);
//     throw error;
//   }
// };

export const enterRoom = async (roomId, password = null) => {
  try {
    const url = `${BASE_URL}/${roomId}/players`;
    const data = password ? { password } : {};
    const headers = getHeaders();

    console.log("Entering room with:", { roomId, password: password ? "[REDACTED]" : null });
    console.log("Request URL:", url);
    console.log("Request data:", data);

    const response = await axios.post(url, data, { headers });
    console.log("Enter room response:", response.data);
    return response.data;
  } catch (error) {
    console.error("Error entering room:", error);
    throw error;
  }
};

export const getJoinedRooms = async (userId) => {
  try {
    const response = await axios.get(`${BASE_URL}/joined/${userId}`, {
      headers: getHeaders(),
    });
    console.log("Joined rooms response:", response.data);
    return response.data; // 전체 응답을 반환
  } catch (error) {
    console.error("Error fetching joined rooms:", error);
    throw error;
  }
};
