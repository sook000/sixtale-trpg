import axios from 'axios';

// Axios 인스턴스 생성
const userProfile = axios.create({
  baseURL: 'http://i11d108.p.ssafy.io/api/v1:8888', // 기본 URL 설정
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 추가
userProfile.interceptors.request.use(
  (config) => {
    // 요청 보내기 전에 실행되는 함수
    // 토큰 추가 (로그인 후 토큰이 있다면)
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    // 요청 오류 처리
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터 추가
userProfile.interceptors.response.use(
  (response) => {
    // 응답 데이터 처리
    // 여기서 응답 데이터를 수정하거나 추가적인 로직을 적용할 수 있습니다.
    console.log('Response data:', response.data);
    return response;
  },
  (error) => {
    // 응답 오류 처리
    console.error('API 요청 오류:', error);
    // 여기서 전역적인 에러 처리를 할 수 있습니다.
    // 예를 들어, 인증 오류가 발생했을 때 로그인 페이지로 리다이렉트 할 수 있습니다.
    if (error.response && error.response.status === 401) {
      // 인증 오류 처리
      alert('인증이 필요합니다. 로그인 페이지로 이동합니다.');
      window.location.href = '/login'; // 로그인 페이지로 리다이렉트
    }
    return Promise.reject(error);
  }
);

export default userProfile;
