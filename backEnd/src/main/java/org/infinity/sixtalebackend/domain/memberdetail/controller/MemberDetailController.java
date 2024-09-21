package org.infinity.sixtalebackend.domain.memberdetail.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.sixtalebackend.domain.memberdetail.dto.MemberDetailRequestDto;
import org.infinity.sixtalebackend.domain.memberdetail.dto.MemberDetailResponseDto;
import org.infinity.sixtalebackend.domain.memberdetail.service.MemberDetailService;
import org.infinity.sixtalebackend.global.common.authentication.AuthenticationUtil;
import org.infinity.sixtalebackend.global.common.response.DefaultResponse;
import org.infinity.sixtalebackend.global.common.response.ResponseMessage;
import org.infinity.sixtalebackend.global.common.response.StatusCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members/details")
public class MemberDetailController {
    private final MemberDetailService memberDetailService;

    /**
     * 회원 상세 정보 생성
     */
    @PostMapping("")
    public ResponseEntity<?> createMemberDetail(@RequestBody @Valid MemberDetailRequestDto memberDetailRequestDto){
        try {
            Long memberID = AuthenticationUtil.getMemberId();


            memberDetailService.createMemberDetail(memberDetailRequestDto,memberID);
            return  new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED,ResponseMessage.CREATED_MEMBER_DETAIL,null),HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원 상세 정보 수정
     */
    @PutMapping("")
    public ResponseEntity updateMemberDetail(@RequestBody @Valid MemberDetailRequestDto memberDetailRequestDto){
        try {
            Long memberID = AuthenticationUtil.getMemberId();
//            Long memberID = 1L;
            memberDetailService.updateMemberDetail(memberDetailRequestDto,memberID);
            return  new ResponseEntity(DefaultResponse.res(StatusCode.OK,ResponseMessage.UPDATED_MEMBER_DETAIL,null),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원 상세 정보 조회
     */
    @GetMapping("")
    public ResponseEntity<?> readMemberDetail(){
        try {
            Long memberID = AuthenticationUtil.getMemberId();

            MemberDetailResponseDto memberDetailResponseDto = memberDetailService.readMemberDetail(memberID);
            return  new ResponseEntity<>(DefaultResponse.res(StatusCode.OK,ResponseMessage.READ_MEMBER_DETAIL,memberDetailResponseDto),HttpStatus.OK);
        }catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
