package org.infinity.sixtalebackend.domain.member.controller;

import com.nimbusds.oauth2.sdk.auth.JWTAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.sixtalebackend.domain.member.dto.MemberResponseDto;
import org.infinity.sixtalebackend.domain.member.service.MemberSerivceImpl;
import org.infinity.sixtalebackend.domain.member.dto.MemberNicknameCheckResponse;
import org.infinity.sixtalebackend.domain.scenario.dto.ScenarioListResponseDto;
import org.infinity.sixtalebackend.global.common.authentication.AuthenticationUtil;
import org.infinity.sixtalebackend.global.common.response.DefaultResponse;
import org.infinity.sixtalebackend.global.common.response.ResponseMessage;
import org.infinity.sixtalebackend.global.common.response.StatusCode;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {
    private final MemberSerivceImpl memberService;

    /**
     * 회원 정보 생성(닉네임, 프로필 이미지)
     */
    @PostMapping(value = "",consumes = "multipart/*")
    public ResponseEntity<?> createMemberInfo(String nickName, @RequestPart("files") MultipartFile[] files){
        try {

            Long memberId = AuthenticationUtil.getMemberId();

            MemberResponseDto memberResponseDto = memberService.createMemberInfo(memberId,nickName,files);
            return  new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.CREATED_MEMBER_INFO,memberResponseDto),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원 정보 조회(id, 닉네임, 프로필 이미지, 가입 날짜)
     */
    @GetMapping("")
    public ResponseEntity<?> getMemberInfo(){
        try {
            Long memberId = AuthenticationUtil.getMemberId();
//            Long memberId = 1L;
            MemberResponseDto memberResponseDto = memberService.getMemberInfo(memberId);
            return  new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_MEMBER_INFO,memberResponseDto),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원 정보 수정(닉네임, 프로필 이미지)
     */
    @PutMapping(value = "",consumes = "multipart/*")
    public ResponseEntity<?> updateMemberInfo(String nickName, @RequestPart("files")MultipartFile[] files){
        try {
            Long memberId = AuthenticationUtil.getMemberId();

            MemberResponseDto memberResponseDto = memberService.updateMemberInfo(memberId,nickName,files);
            return  new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.UPDATED_MEMBER_INFO,memberResponseDto),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 닉네임 중복 검사
     * @param nickname
     */
    @GetMapping("/check-nickname")
    public ResponseEntity checkNickname(@RequestParam String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return new ResponseEntity<>(new MemberNicknameCheckResponse(400, "잘못된 요청", null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            boolean isDuplicated = memberService.isNicknameDuplicated(nickname);
            if (isDuplicated) {
                return new ResponseEntity<>(new MemberNicknameCheckResponse(200, "닉네임 중복 검사 성공 (중복O)", new MemberNicknameCheckResponse.Data(false)),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(
                        new MemberNicknameCheckResponse(200, "닉네임 중복 검사 성공 (중복X)", new MemberNicknameCheckResponse.Data(true)),
                        HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MemberNicknameCheckResponse(500, "서버 에러", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 좋아요한 시나리오 조회
     */
    @GetMapping("/liked-scenarios")
    public ResponseEntity getLikedScenarioList (Pageable scenarioPageable) {
        try {
            Long memberID = AuthenticationUtil.getMemberId();
            log.info("memberID = {}", memberID);

            ScenarioListResponseDto scenarioList = memberService.getScenarioLikeList(memberID, scenarioPageable);
            return  new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_SCENARIO_LIKE_LIST,scenarioList), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.READ_SCENARIO_LIKE_LIST_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 시나리오 좋아요
     */


}
