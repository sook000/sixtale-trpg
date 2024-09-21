package org.infinity.sixtalebackend.global.common.response;

public class ResponseMessage {
    public static final String CREATED_USER = "회원 가입 성공";
    public static final String CREATED_MEMBER_DETAIL = "회원 상세정보 생성 성공";
    public static final String CREATED_MEMBER_INFO = "회원 정보 생성 성공";
    public static final String UPDATED_MEMBER_INFO = "회원 정보 수정 성공";
    public static final String READ_MEMBER_INFO = "회원 정보 조회 성공";
    public static final String UPDATED_MEMBER_DETAIL = "회원 상세정보 수정 성공";
    public static final String READ_MEMBER_DETAIL = "회원 상세정보 조회 성공";
    public static final String READ_SCENARIO_LIST = "시나리오 목록 조회 성공";
    public static final String READ_WAITING_CHAT_LOG_LIST = "대기방 일반 채팅 로그 리스트 조회 성공";
    public static final String READ_PLAY_CHAT_LOG_LIST = "플레이방 일반 채팅 로그 리스트 조회 성공";
    public static final String READ_WAITING_WHISPER_CHAT_LOG_LIST = "대기방 귓속말 채팅 로그 리스트 조회 성공";
    public static final String READ_PLAY_WHISPER_CHAT_LOG_LIST = "플레이방 귓속말 채팅 로그 리스트 조회 성공";
    public static final String READ_WAITING_ALL_CHAT_LOG_LIST = "대기방 전체 채팅 로그 리스트 조회 성공";
    public static final String READ_PLAY_ALL_CHAT_LOG_LIST = "플레이방 전체 채팅 로그 리스트 조회 성공";
    public static final String BAD_REQUEST_TERMS = "회원약관 요청 에러";
    public static final String DUPLICATE_EMAIL = "이메일 중복 에러";
    public static final String VALIDATION_ERROR = "이메일 유효성 에러";
    public static final String SEND_EMAIL_AUTHENTICATION_CODE = "이메일 인증코드 전송 성공";
    public static final String LOGIN_SUCCESS = "로그인 성공";
    public static final String LOGOUT_SUCCESS = "로그아웃 성공";
    public static final String KAKAO_LOGIN_SUCCESS = "카카오 로그인 성공";
    public static final String LOGIN_FAIL = "로그인 실패";
    public static final String FIND_PASSWORD = "비밀번호 찾기 성공";

    public static final String CHANGE_PASSWORD = "비밀번호 변경 성공";
    public static final String READ_USER = "회원 정보 조회 성공";
    public static final String READ_DIARYS = "일기 정보 조회 성공";
    public static final String READ_DIARYS_FAIL = "일기 정보 조회 실패";
    public static final String UPDATE_DIARY = "일기 수정 성공";
    public static final String UNAUTHORIZED = "회원 인증 실패";

    public static final String UPDATE_USER = "회원 정보 수정 성공";
    public static final String WITHDRAWAL_USER = "회원 탈퇴 성공";
    public static final String CREATE_USER_CALENDAR = "회원 일정 생성 성공";
    public static final String CREATE_USER_CALENDAR_FAIL = "회원 일정 생성 실패";
    public static final String CREATE_USER_CALENDAR_ERROR = "회원 일정 생성 시 입력 오류";
    public static final String READ_SCENARIO_LIKE_LIST = "좋아요한 시나리오 목록 조회 성공";
    public static final String READ_SCENARIO_LIKE_LIST_FAIL = "좋아요한 시나리오 목록 조회 실패";
    public static final String CREATE_SCENARIO_LIKE = "시나리오 좋아요 성공";
    public static final String CREATE_SCENARIO_LIKE_FAIL = "시나리오 좋아요 실패";
    public static final String DELETE_SCENARIO_LIKE = "시나리오 좋아요 삭제 성공";
    public static final String DELETE_SCENARIO_LIKE_FAIL = "시나리오 좋아요 삭제 실패";
    
    public static final String READ_GENRE_LIST = "장르 목록 조회 성공";
    public static final String READ_GENRE_LIST_FAIL = "장르 목록 조회 실패";

    public static final String DELETE_USER_CALENDAR = "회원 일정 삭제 성공";
    public static final String DELETE_USER_CALENDAR_FAIL = "회원 일정 삭제 실패";
    public static final String READ_USER_CALENDAR = "회원 일정 조회 성공";
    public static final String READ_USER_CALENDAR_FAIL = "회원 일정 조회 실패";

    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";

    public static final String ENTER_USER = "게임 방 유저 입장 성공";
    public static final String EXIT_USER = "게임 방 유저 퇴장 성공";
    public static final String INCORRECT_PASSWORD = "게임 방 비밀번호 오류";
    public static final String ENTER_USER_FAIL = "게임 방 유저 입장 실패";
    public static final String EXIT_USER_FAIL = "게임 방 유저 퇴장 실패";
    public static final String UPDATE_ROOM_STATUS = "게임 방 상태 변경 성공";
    public static final String UPDATE_ROOM_STATUS_FAIL = "게임 방 상태 변경 실패";
    public static final String CREATE_ROOM = "게임 방 생성 성공";
    public static final String CREATE_ROOM_FAIL = "게임 방 생성 실패";
    public static final String READ_ROOM_INFO = "게임 방 정보 조회 성공";
    public static final String READ_ROOM_INFO_FAIL = "게임 방 정보 조회 실패";
    public static final String UPDATE_ROOM = "게임 방 정보 수정 성공";
    public static final String UPDATE_ROOM_FAIL = "게임 방 정보 수정 성공";
    public static final String READ_ROOM_LIST= "게임 방 목록 조회 성공";
    public static final String READ_ROOM_LIST_FAIL= "게임 방 목록 조회 실패";
    public static final String READ_ROOM_MEMBER_CALENDARS = "게임 방 회원 일정 전체 조회 성공";
    public static final String READ_ROOM_MEMBER_CALENDARS_FAIL = "게임 방 회원 일정 전체 조회 실패";
    public static final String CREATE_ROOM_MEMBER_CALENDARS = "게임 방 회원 일정 전체 생성 성공";
    public static final String CREATE_ROOM_MEMBER_CALENDARS_FAIL = "게임 방 회원 일정 전체 생성 실패";
    public static final String CREATE_ROOM_MEMBER_CALENDARS_ERROR = "게임 방 회원 일정 생성 시 입력 오류";

    public static final String READ_RULE = "룰 상세 조회 성공";
    public static final String READ_RULE_FAIL = "룰 상세 조회 실패";
    public static final String READ_RULE_JOB = "룰 직업 목록 조회 성공";
    public static final String READ_RULE_JOB_FAIL = "룰 직업 목록 조회 실패";
    public static final String READ_JOB_OPTION = "직업에 따른 선택지 목록 조회 성공";
    public static final String READ_JOB_OPTION_FAIL = "직업에 따른 선택지 목록 조회 실패";
    public static final String READ_COMMON_ACTION = "공통 액션 조회 성공";
    public static final String READ_COMMON_ACTION_FAIL = "공통 액션 조회 실패";
    public static final String READ_RULE_EQUIPMENT = "룰 장비 목록 조회 성공";
    public static final String READ_RULE_EQUIPMENT_FAIL = "룰 장비 목록 조회 실패";

    public static final String READ_MAP_LIST = "맵 목록 조회 성공";
    public static final String READ_MAP_LIST_FAIL = "맵 목록 조회 실패";
    public static final String READ_MAP = "맵 정보 조회 성공";
    public static final String READ_MAP_FAIL = "맵 정보 조회 실패";
    public static final String READ_PLACE_EVENT_LIST = "장소 이벤트 목록 조회 성공";
    public static final String READ_PLACE_EVENT_LIST_FAIL = "장소 이벤트 목록 조회 실패";
    public static final String READ_PLACE_EVENT = "장소 이벤트 조회 성공";
    public static final String READ_PLACE_EVENT_FAIL = "장소 이벤트 조회 실패";
    public static final String READ_NPC_EVENT_LIST = "NPC 정보 이벤트 목록 조회 성공";
    public static final String READ_NPC_EVENT_LIST_FAIL = "NPC 정보 이벤트 목록 조회 실패";
    public static final String READ_NPC_EVENT = "NPC 정보 이벤트 조회 성공";
    public static final String READ_NPC_EVENT_FAIL = "NPC 정보 이벤트 조회 실패";

    public static final String CREATE_CHARACTER_SHEET = "캐릭터 시트 작성 성공";
    public static final String CREATE_CHARACTER_SHEET_FAIL = "캐릭터 시트 작성 실패";
    public static final String UPDATE_CHARACTER_SHEET = "캐릭터 시트 수정 성공(플레이 전)";
    public static final String UPDATE_CHARACTER_SHEET_FAIL = "캐릭터 시트 수정 실패(플레이 전)";
    public static final String UPDATE_CHARACTER_SHEET_IN_PLAYING = "캐릭터 시트 수정 성공(플레이 중)";
    public static final String UPDATE_CHARACTER_SHEET_IN_PLAYING_FAIL = "캐릭터 시트 수정 실패(플레이 중)";
    public static final String READ_CHARACTER_SHEET = "작성한 캐릭터 시트 조회 성공";
    public static final String READ_CHARACTER_SHEET_FAIL = "작성한 캐릭터 시트 조회 실패";
    public static final String DELETE_CHARACTER_SHEET = "작성한 캐릭터 시트 삭제 성공";
    public static final String DELETE_CHARACTER_SHEET_FAIL = "작성한 캐릭터 시트 삭제 실패";
    public static final String READ_CHARACTER_SHEET_EQUIPMENT = "캐릭터 장비 목록 불러오기 성공";
    public static final String READ_CHARACTER_SHEET_EQUIPMENT_FAIL = "캐릭터 장비 목록 불러오기 실패";
    public static final String ADD_CHARACTER_SHEET_EQUIPMENT = "캐릭터 장비 추가 성공";
    public static final String ADD_CHARACTER_SHEET_EQUIPMENT_FAIL = "캐릭터 장비 추가 실패";
    public static final String UPDATE_CHARACTER_SHEET_EQUIPMENT = "캐릭터 장비 수량 수정 성공";
    public static final String UPDATE_CHARACTER_SHEET_EQUIPMENT_FAIL = "캐릭터 장비 수량 수정 실패";
    public static final String DELETE_CHARACTER_SHEET_EQUIPMENT = "캐릭터 장비 삭제 성공";
    public static final String DELETE_CHARACTER_SHEET_EQUIPMENT_FAIL = "캐릭터 장비 삭제 실패";
    public static final String READ_CHARACTER_SHEET_ACTION = "캐릭터 액션 목록 불러오기 성공";
    public static final String READ_CHARACTER_SHEET_ACTION_FAIL = "캐릭터 액션 목록 불러오기 실패";
    public static final String ADD_CHARACTER_SHEET_ACTION = "캐릭터 액션 추가 성공" ;
    public static final String ADD_CHARACTER_SHEET_ACTION_FAIL = "캐릭터 액션 추가 실패" ;
    public static final String DELETE_CHARACTER_SHEET_ACTION = "캐릭터 액션 삭제 성공";
    public static final String DELETE_CHARACTER_SHEET_ACTION_FAIL = "캐릭터 액션 삭제 실패";
    public static final String UPDATE_CHARACTER_GOLD = "캐릭터 골드 수정 성공";
    public static final String UPDATE_CHARACTER_GOLD_FAIL = "캐릭터 골드 수정 실패";


}
