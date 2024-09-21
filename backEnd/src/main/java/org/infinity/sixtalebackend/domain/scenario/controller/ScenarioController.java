package org.infinity.sixtalebackend.domain.scenario.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.sixtalebackend.domain.member.dto.MemberResponseDto;
import org.infinity.sixtalebackend.domain.scenario.dto.ScenarioListResponseDto;
import org.infinity.sixtalebackend.domain.scenario.dto.ScenarioResponseDto;
import org.infinity.sixtalebackend.domain.scenario.service.ScenarioService;
import org.infinity.sixtalebackend.global.common.authentication.AuthenticationUtil;
import org.infinity.sixtalebackend.global.common.response.DefaultResponse;
import org.infinity.sixtalebackend.global.common.response.ResponseMessage;
import org.infinity.sixtalebackend.global.common.response.StatusCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scenarios")
@Slf4j
public class ScenarioController {

    private final ScenarioService scenarioService;

    /**
     * 시나리오 목록 조회
     * 정렬 기준 : 좋아요수, 등록 일시
     * 필터링 기준 : 장르
     * 검색 : 시나리오 제목
     * 인증 없이도 보이도록함
     * @return
     */
    @GetMapping("")
    public ResponseEntity<?> getScenarioList(
            @RequestParam(required = false) List<Long> genre,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order){
        try {
            // 로그인 중인지 아닌지의 로직
            Long memberID = AuthenticationUtil.getMemberId();
            if (memberID == -1) memberID = null;

            ScenarioListResponseDto scenarioList = scenarioService.getScenarioList(memberID,genre,title,page,size,sort,order);
            return  new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_SCENARIO_LIST,scenarioList), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 시나리오 상세 정보 조회
     * @return
     */
    @GetMapping("/{scenarioID}")
    public ResponseEntity<?> getScenarioInfo(@PathVariable Long scenarioID){
        try {
            Long memberID = AuthenticationUtil.getMemberId();

            ScenarioResponseDto scenarioResponseDto = scenarioService.getScenarioInfo(scenarioID,null);
            return  new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.CREATED_MEMBER_INFO,scenarioResponseDto), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 시나리오 좋아요
     */
    @PostMapping("/{scenarioID}/like")
    public ResponseEntity<?> likeScenario(@PathVariable Long scenarioID) {
        try {
            // 로그인 중인지 아닌지의 로직
            Long memberID = AuthenticationUtil.getMemberId();
            if (memberID == -1) throw new AuthenticationException();

            boolean success = scenarioService.likeScenario(scenarioID, memberID);
            if (success) {
                return new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.CREATE_SCENARIO_LIKE), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.CREATE_SCENARIO_LIKE_FAIL), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 시나리오 좋아요 취소
     */
    @DeleteMapping("/{scenarioID}/like")
    public ResponseEntity<?> unlikeScenario(@PathVariable Long scenarioID) {
        try {
            // 로그인 중인지 아닌지의 로직
            Long memberID = AuthenticationUtil.getMemberId();
            if (memberID == -1) throw new AuthenticationException();

            boolean success = scenarioService.unlikeScenario(scenarioID, memberID);
            if (success) {
                return new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.DELETE_SCENARIO_LIKE), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.DELETE_SCENARIO_LIKE_FAIL), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
