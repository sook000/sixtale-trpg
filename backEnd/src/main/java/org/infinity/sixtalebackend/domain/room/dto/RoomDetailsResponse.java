package org.infinity.sixtalebackend.domain.room.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
public class RoomDetailsResponse {
    private Long id;
    private String title;
    private String description;
    private Long scenarioID;
    private String scenarioTitle;
    private String scenarioImageURL;
    private Long ruleID;
    private String ruleTitle;
    private List<PlayMemberResponse> playMemberList;
    private Long gmID;
    private String gmNickname;
    private String gmImageURL;
    private Byte currentCount;
    private Byte maxCount;
    private Boolean isLocked;
    private String password;
    private Boolean isShortStory;
    private Boolean isVoice;
    private String status;
    private LocalDateTime nextPlay;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalTime playTime;
}
