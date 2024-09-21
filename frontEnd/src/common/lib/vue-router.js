import { createRouter, createWebHistory } from "vue-router";
import Home from "@/views/home/home";
import Rulebook from "@/views/menu/Rulebook";
import Scenarios from "@/views/menu/Scenarios";
import FindPlayers from "@/views/menu/FindPlayers";
import Lobby from "@/views/menu/Lobby";
import Main from "@/views/main/main"; // main.vue를 공통 레이아웃으로 사용
import CharacterSheet from "@/views/games/CharacterSheet";
import InGame from "@/views/games/InGame";
import Waiting from "@/views/games/Waiting";
import Mypage from "@/views/menu/Mypage";
import UserPlayList from "@/views/mypage/UserPlayList";
import UserCalendar from "@/views/mypage/UserCalendar";
import UserInfo from "@/views/mypage/UserInfo";
import UserEdit from "@/views/mypage/UserInfoEdit";
import UserLikeScenario from "@/views/mypage/UserLikeScenario";
import UserStyle from "@/views/mypage/UserStyle";
import AuthCallback from "@/views/AuthCallback"; // 추가: AuthCallback 컴포넌트 임포트
import { getToken } from '@/services/authService';

const routes = [
  {
    path: "/",
    component: Main,
    children: [
      {
        path: "",
        name: "Home",
        component: Home,
      },
      {
        path: "rulebook",
        name: "Rulebook",
        component: Rulebook,
      },
      {
        path: "scenarios",
        name: "Scenarios",
        component: Scenarios,
      },
      {
        path: "find-players",
        name: "FindPlayers",
        component: FindPlayers,
      },
      {
        path: "lobby",
        name: "Lobby",
        component: Lobby,
      },
      {
        path: "mypage",
        name: "Mypage",
        component: Mypage,
        redirect: "/mypage/user-playlist",
        children: [
          {
            path: "user-playlist",
            name: "UserPlayList",
            component: UserPlayList,
          },
          {
            path: "user-calendar",
            name: "UserCalendar",
            component: UserCalendar,
          },
          {
            path: "user-info",
            name: "UserInfo",
            component: UserInfo,
          },
          {
            path: "user-edit",
            name: "UserEdit",
            component: UserEdit,
          },
          {
            path: "user-likescenario",
            name: "UserLikeScenario",
            component: UserLikeScenario,
          },
          {
            path: "user-style",
            name: "UserStyle",
            component: UserStyle,
          },
        ],
      },
    ],
  },
  {
    path: "/game",
    children: [
      {
        path: ":roomId/waiting",
        name: "Waiting",
        component: Waiting,
      },
      {
        path: ":roomId/character-sheet",
        name: "CharacterSheet",
        component: CharacterSheet,
      },
      {
        path: ":roomId/in-game",
        name: "InGame",
        component: InGame,
      },
    ],
  },
  {
    path: "/auth/callback", // 추가: 소셜 로그인 후 리디렉션 처리 경로
    name: "AuthCallback",
    component: AuthCallback,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const token = getToken();

  // 페이지 이동 시마다 JWT 토큰을 콘솔에 출력
  if (token) {
    console.log("현재 JWT 토큰:", token);
  } else {
    console.log("JWT 토큰이 존재하지 않습니다.");
  }

  // 페이지 이동 허용
  next();
});

router.afterEach((to) => {
  console.log(to);
});

export default router;
