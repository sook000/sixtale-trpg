// src/common/api/accountApi.js
import axios from "axios";
import Cookies from "js-cookie";

export function useAccountApi(router) {
  const logout = async () => {
    try {
      const response = await axios.get("/api/v1/members/logout", {
        withCredentials: true,
      });

      if (response.status === 200) {
        alert("로그아웃 성공");
        // localStorage.removeItem("accessToken");
        // document.cookie = 'session=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        document.cookie =
          "accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        router.push("/");
      } else {
        alert("로그아웃 실패");
      }
    } catch (error) {
      console.error(error);
      alert("로그아웃 중 오류 발생");
    }
  };

  const deleteAccount = async () => {
    try {
      const response = await axios.patch("/api/v1/members/withdraw", {
        withCredentials: true,
      });

      if (response.status === 200) {
        alert("회원 탈퇴 성공");
        // localStorage.removeItem("accessToken");
        // document.cookie = "session=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        document.cookie =
          "accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        router.push("/");
      } else {
        alert("회원 탈퇴 실패");
      }
    } catch (error) {
      console.error(error);
      alert("회원 탈퇴 중 오류 발생");
    }
  };

  return {
    logout,
    deleteAccount,
  };
}
