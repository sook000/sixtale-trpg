package org.infinity.sixtalebackend.domain.member.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.infinity.sixtalebackend.domain.member.exception.InvalidDateException;
import org.infinity.sixtalebackend.domain.member.service.CalendarServiceImpl;
import org.infinity.sixtalebackend.domain.member.dto.CalendarListResponse;
import org.infinity.sixtalebackend.domain.member.dto.CalendarRequest;
import org.infinity.sixtalebackend.global.common.authentication.AuthenticationUtil;
import org.infinity.sixtalebackend.global.common.response.DefaultResponse;
import org.infinity.sixtalebackend.global.common.response.ResponseMessage;
import org.infinity.sixtalebackend.global.common.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/calendars")
@AllArgsConstructor
public class CalendarController {
    private final CalendarServiceImpl calendarService;

    /**
     *  회원 일정 조회(개인)
     */
    @GetMapping
    public ResponseEntity getAllCalendars() {
        try {
            Long memberID = AuthenticationUtil.getMemberId();

            CalendarListResponse response = calendarService.getAllCalendars(memberID);
            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_USER_CALENDAR, response), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.READ_USER_CALENDAR_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원 일정 생성
     * @param calendarRequest
     */
    @PostMapping
    public ResponseEntity createCalendar(@RequestBody @Valid CalendarRequest calendarRequest) {
        try {
            // bearer 토큰 추출 대신 id = 1인 유저 가정
            Long memberID = AuthenticationUtil.getMemberId();

            calendarService.createCalendar(memberID, calendarRequest);
            return new ResponseEntity(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.CREATE_USER_CALENDAR), HttpStatus.CREATED);
        } catch (InvalidDateException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST,  ResponseMessage.CREATE_USER_CALENDAR_ERROR), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.CREATE_USER_CALENDAR_FAIL),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원 일정 삭제
     * @param calendarId
     */
    @DeleteMapping("/{calendarId}")
    public ResponseEntity deleteCalendar(@PathVariable Long calendarId) {
        try {
            // bearer 토큰 추출 대신 id = 1인 유저 가정
            Long memberID = AuthenticationUtil.getMemberId();
            calendarService.deleteCalendar(calendarId, memberID);
            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.DELETE_USER_CALENDAR), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.CREATE_USER_CALENDAR_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    토큰 있을 때 가정
    @PostMapping
    public ResponseEntity<DefaultResponse<Void>> createCalendar(HttpServletRequest request,
                                                                @RequestBody CalendarRequest calendarRequest) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(DefaultResponse.res(401, "인증 권한 에러"));
            }
            String email = jwtTokenProvider.getUsername(token);
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }*/

}
