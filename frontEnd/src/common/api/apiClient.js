import axios from 'axios';
import { getToken, removeToken } from '@/services/authService';

const apiClient = axios.create({
    baseURL: 'http://localhost:8888/api/v1', // 기본 API URL 설정
});

apiClient.interceptors.request.use(
    (config) => {
        const token = getToken();
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
            console.log("API 요청 시 JWT 토큰이 포함되었습니다:", token);  // API 요청 시 토큰 포함 여부 확인
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

apiClient.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response.status === 401) {
            removeToken();
            window.location.href = '/login'; // 토큰이 만료된 경우 로그인 페이지로 리다이렉트
        }
        return Promise.reject(error);
    }
);

export default apiClient;
