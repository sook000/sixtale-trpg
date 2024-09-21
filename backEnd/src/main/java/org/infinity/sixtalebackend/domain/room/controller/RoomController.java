package org.infinity.sixtalebackend.domain.room.controller;

import lombok.AllArgsConstructor;
import org.infinity.sixtalebackend.domain.member.exception.InvalidDateException;
import org.infinity.sixtalebackend.domain.room.domain.Room;
import org.infinity.sixtalebackend.domain.room.domain.RoomStatus;
import org.infinity.sixtalebackend.domain.room.dto.*;
import org.infinity.sixtalebackend.domain.room.exception.IncorrectPasswordException;
import org.infinity.sixtalebackend.domain.room.service.RoomService;
import org.infinity.sixtalebackend.global.common.authentication.AuthenticationUtil;
import org.infinity.sixtalebackend.global.common.response.DefaultResponse;
import org.infinity.sixtalebackend.global.common.response.ResponseMessage;
import org.infinity.sixtalebackend.global.common.response.StatusCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequestMapping("/rooms")
@AllArgsConstructor
public class RoomController {
    private final RoomService roomServiceImpl;

    /**
     * 게임 방 유저 입장
     */
    @PostMapping("/{roomID}/players")
    public ResponseEntity addPlayerToRoom(@PathVariable Long roomID, @RequestBody AddPlayerRequest addPlayerRequest) {
        try {
            // 로그인 유저 아이디 가져오기
            Long gmID = AuthenticationUtil.getMemberId();
            // id = 1 유저 가정
//            Long memberID = 11L;
            RoomResponse roomResponse = roomServiceImpl.addPlayerToRoom(roomID, gmID, addPlayerRequest.getPassword());
            return new ResponseEntity(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.ENTER_USER, roomResponse), HttpStatus.CREATED);
        } catch (IncorrectPasswordException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.UNAUTHORIZED, ResponseMessage.INCORRECT_PASSWORD), HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.ENTER_USER_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * 게임 방 유저 퇴장
     */
    @DeleteMapping("{roomID}/players")
    public ResponseEntity deletePlayerFromRoom(@PathVariable Long roomID) {
        try {
            // 로그인 유저 아이디 가져오기
            Long gmID = AuthenticationUtil.getMemberId();
//            Long memberID = 11L;
            roomServiceImpl.deletePlayerFromRoom(roomID, gmID);
            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.EXIT_USER), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.EXIT_USER_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 플레이 상태 변경
     */
    @PatchMapping("/{roomID}/status")
    public ResponseEntity updateRoomStatus(@PathVariable Long roomID, @RequestBody Room room) {
        try {
            roomServiceImpl.updateRoomStatus(roomID, room.getStatus());
            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.UPDATE_ROOM_STATUS), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.UPDATE_ROOM_STATUS_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게임 방 생성
     */
    @PostMapping
    public ResponseEntity createRoom(@RequestBody RoomCreateRequest roomCreateRequest) {
        try {
            // 로그인 유저 아이디 가져오기
            Long gmID = AuthenticationUtil.getMemberId();
//            Long gmID = 11L;
            RoomResponse roomResponse = roomServiceImpl.createRoom(roomCreateRequest, gmID);
            return new ResponseEntity(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.CREATE_ROOM, roomResponse), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.CREATE_ROOM_FAIL), HttpStatus.BAD_REQUEST);
        }  catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게임 방 정보 조회
     */
    @GetMapping("/{roomID}")
    public ResponseEntity getRoomDetails(@PathVariable Long roomID) {
        try {
            RoomDetailsResponse roomDetails = roomServiceImpl.getRoomDetails(roomID);
            return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_ROOM_INFO, roomDetails));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.READ_ROOM_INFO_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게임 방 정보 수정
     */
    @PatchMapping("/{roomID}")
    public ResponseEntity updateRoom(@PathVariable Long roomID, @RequestBody RoomUpdateRequest roomUpdateRequest) {
        try {
            // 로그인 유저 아이디 가져오기
            Long gmID = AuthenticationUtil.getMemberId();
//            Long gmID = 1L;
            RoomUpdateResponse roomResponse = roomServiceImpl.updateRoom(roomID, gmID, roomUpdateRequest);
            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.UPDATE_ROOM, roomResponse), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("인증 권한 에러")) {
                return new ResponseEntity(DefaultResponse.res(StatusCode.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.UPDATE_ROOM_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게임 방 목록 조회
     */
    @GetMapping
    public ResponseEntity getRoomList(@RequestParam(required = false) RoomStatus status,
                                      @RequestParam(required = false) String title,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "6") int size,
                                      PagedResourcesAssembler<RoomResponse> assembler) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<RoomResponse> rooms = roomServiceImpl.getRoomList(status, title, pageable);

            PagedModel<EntityModel<RoomResponse>> pagedModel = assembler.toModel(rooms, WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RoomController.class)
                    .getRoomList(status, title, page, size, assembler)).withSelfRel());

            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_ROOM_LIST, pagedModel), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.READ_ROOM_LIST_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게임 방 멤버 일정 전체 조회
     */
    @GetMapping("/{roomID}/calendars")
    public ResponseEntity getRoomMemberCalendars(@PathVariable Long roomID) {
        try {
            List<GameMemberCalendarResponse> calendars = roomServiceImpl.getRoomMemberCalendars(roomID);
            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_ROOM_MEMBER_CALENDARS, calendars), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.READ_ROOM_MEMBER_CALENDARS_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게임 방 멤버 전체 일정 생성
     */
    @PostMapping("/{roomID}/calendars")
    public ResponseEntity addEventToRoomMembers(@PathVariable Long roomID, @RequestBody CalendarRequest calendarRequest) {
        try {
            roomServiceImpl.addEventToRoomMembers(roomID, calendarRequest);
            return new ResponseEntity(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.CREATE_ROOM_MEMBER_CALENDARS), HttpStatus.CREATED);
        }  catch (InvalidDateException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST,  ResponseMessage.CREATE_ROOM_MEMBER_CALENDARS_ERROR), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.CREATE_ROOM_MEMBER_CALENDARS_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 사용자의 게임 방 목록 조회
     */
    @GetMapping("/member")
    public ResponseEntity getRoomListByMemberId(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "6") int size,
                                                PagedResourcesAssembler<RoomResponse> assembler) {
        try {
            Long memberId = AuthenticationUtil.getMemberId();
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<RoomResponse> rooms = roomServiceImpl.getRoomListByMemberId(memberId, pageable);
            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_ROOM_LIST, rooms), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.READ_ROOM_LIST_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
