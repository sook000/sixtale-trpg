// src/api/ScenarioAPI.js
import axios from "axios";

const BASE_URL = "/api/v1/genres";

// 장르 목록 조회
export const getGenreList = async () => {
  try {
    const response = await axios.get(`${BASE_URL}`);
    return response.data.data;
  } catch (error) {
    console.error("Error fetching genres:", error);
    throw error;
  }
};
