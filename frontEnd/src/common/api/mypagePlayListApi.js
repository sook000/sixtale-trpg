import axios from "axios";
import Cookies from "js-cookie"; // 쿠키 다루기 위한 라이브러리

const BASE_URL = "/api/v1/rooms";

// 토큰을 쿠키에서 가져오기
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

// 방 목록 가져오는 api 호출
export const fetchRooms = async (page, size) => {
  try {
    const response = await axios.get(`${BASE_URL}/member`, {
      params: { page, size },
      headers: getHeaders(),
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching rooms: ", error);
    throw error;
  }
};
