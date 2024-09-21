import axios from "axios";
import Cookies from "js-cookie";

const BASE_URL = "/api/v1/rules";

const getHeaders = () => {
  const accessToken = Cookies.get("access-token");
  return {
    "Content-Type": "application/json",
    ...(accessToken && { Authorization: `Bearer ${accessToken}` }),
  };
};

export const getRuleList = async () => {
  try {
    const response = await axios.get(`${BASE_URL}/1`, { headers: getHeaders() });
    if (response.data && response.data.data) {
      return [{ id: response.data.data.id, title: response.data.data.title }];
    }
  } catch (error) {
    console.error("Error fetching rule info:", error);
  }

  // 오류 발생 시 또는 데이터가 없을 경우 기본값 반환
  return [{ id: 1, title: "기본 룰" }];
};
