package org.infinity.sixtalebackend.domain.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.member.service.AuthServiceImpl;
import org.infinity.sixtalebackend.domain.member.service.MemberSerivceImpl;
import org.infinity.sixtalebackend.domain.member.util.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.infinity.sixtalebackend.global.common.authentication.AuthenticationUtil;
import org.infinity.sixtalebackend.global.common.response.DefaultResponse;
import org.infinity.sixtalebackend.global.common.response.ResponseMessage;
import org.infinity.sixtalebackend.global.common.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final JWTUtil jwtUtil;
    private final MemberSerivceImpl memberSerivceImpl;

    @Value("${redirect.url}")
    private String redirectUrl;
    /**
     * 로그인
     */
    @GetMapping("/auth/login/{registrationID}")
    public ResponseEntity<?> login(@RequestParam String code, @PathVariable String registrationID,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            Member member = authService.socialLogin(code, registrationID);
            String accessToken = jwtUtil.generateToken(member.getId());
            authService.saveAccessToken(member, accessToken);
            
            // HTTP-Only 및 Secure 속성 사용해서 보안 강화해야함
            Cookie cookie = new Cookie("access-token", accessToken);
            cookie.setHttpOnly(false);
            cookie.setSecure(false); // HTTPS를 사용하는 경우에만 설정
            cookie.setPath("/");
            cookie.setMaxAge(86400); // 1일

            response.addCookie(cookie);
            response.sendRedirect(redirectUrl);

            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.LOGIN_SUCCESS, accessToken), HttpStatus.OK);
        } catch(Exception e){
            log.info(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 로그아웃
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 현재 SecurityContext의 인증 정보 제거
            SecurityContextHolder.clearContext();

            // 세션 무효화
            request.getSession().invalidate();

            Cookie cookie = new Cookie("access-token", "");
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // HTTPS를 사용하는 경우에만 설정
            cookie.setPath("/");
            cookie.setMaxAge(0); // 쿠키 삭제
            response.addCookie(cookie);

            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.LOGOUT_SUCCESS), HttpStatus.OK);
        } catch(Exception e){
            log.info(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원탈퇴
     */
    @PatchMapping("/withdraw")
    public ResponseEntity<?> withdraw(@CookieValue(value = "access-token", required = false) String token,
                                      HttpServletRequest request, HttpServletResponse response) {
        try {
            // 현재 SecurityContext의 인증 정보 제거
            SecurityContextHolder.clearContext();

            // 세션 무효화
            request.getSession().invalidate();

            // 토큰에서 유저 뽑아내기
            Member member = memberSerivceImpl.findByAccessToken(token);

            // 로그아웃 처리
            Cookie cookie = new Cookie("access-token", "");
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // HTTPS를 사용하는 경우에만 설정
            cookie.setPath("/");
            cookie.setMaxAge(0); // 쿠키 삭제
            response.addCookie(cookie);

            // 탈퇴
            authService.withdraw(member);

            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.WITHDRAWAL_USER), HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
