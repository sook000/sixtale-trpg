package org.infinity.sixtalebackend.domain.character_sheet.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterActionListResponse;
import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterActionRequest;
import org.infinity.sixtalebackend.domain.character_sheet.service.CharacterActionService;
import org.infinity.sixtalebackend.global.common.response.DefaultResponse;
import org.infinity.sixtalebackend.global.common.response.ResponseMessage;
import org.infinity.sixtalebackend.global.common.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms/{roomID}/sheets/{playMemberID}/actions")
@Slf4j
@AllArgsConstructor
public class CharacterActionController {

    private final CharacterActionService characterActionService;

    /**
     * 캐릭터 액션 목록 조회
     */
    @GetMapping
    public ResponseEntity<CharacterActionListResponse> getCharacterActions(@PathVariable Long roomID,
                                                                           @PathVariable Long playMemberID) {
        try {
            CharacterActionListResponse response = characterActionService.getCharacterActions(roomID, playMemberID);
            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_CHARACTER_SHEET_ACTION, response), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.READ_CHARACTER_SHEET_ACTION_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 캐릭터 액션 추가
     */
    @PostMapping
    public ResponseEntity<String> addCharacterAction(@PathVariable Long roomID,
                                                     @PathVariable Long playMemberID,
                                                     @RequestBody CharacterActionRequest actionRequest) {
        try {
            characterActionService.addCharacterAction(roomID, playMemberID, actionRequest);
            return new ResponseEntity(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.ADD_CHARACTER_SHEET_ACTION), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.ADD_CHARACTER_SHEET_ACTION_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 캐릭터 액션 삭제
     */
    @DeleteMapping("/{characterActionID}")
    public ResponseEntity<String> deleteCharacterAction(@PathVariable Long roomID,
                                                        @PathVariable Long playMemberID,
                                                        @PathVariable Long characterActionID) {

        try {
            characterActionService.deleteCharacterAction(roomID, playMemberID, characterActionID);
            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.DELETE_CHARACTER_SHEET_ACTION), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.DELETE_CHARACTER_SHEET_ACTION_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
