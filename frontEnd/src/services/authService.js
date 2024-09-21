import axios from 'axios';

const API_URL = 'http://localhost:8888/api/v1';

// 소셜 로그인 처리 함수
export const login = async (provider, code) => {
    const response = await axios.post(`${API_URL}/members/auth/login/${provider}`, { code });

    // 서버 응답 확인
    console.log("서버 응답 데이터:", response.data);

    const token = response.data.token;
    if (token) {
        localStorage.setItem('jwt', token); // JWT 토큰을 로컬 스토리지에 저장
        console.log("JWT 토큰이 저장되었습니다:", token);  // 토큰 저장 시 콘솔 출력
    } else {
        console.error("서버로부터 JWT 토큰을 받지 못했습니다.");  // 토큰이 없는 경우 에러 출력
    }
    return response.data;
};

// 로그아웃 처리 함수
export const logout = () => {
    localStorage.removeItem('jwt'); // 로그아웃 시 JWT 토큰 제거
    console.log("JWT 토큰이 삭제되었습니다.");  // 로그아웃 시 토큰 삭제 확인
};

// JWT 토큰을 로컬 스토리지에서 가져오는 함수
export const getToken = () => {
    return localStorage.getItem('jwt');
};

export const setToken = (token) => {
    localStorage.setItem('jwt', token);
};

export const removeToken = () => {
    localStorage.removeItem('jwt');
};
