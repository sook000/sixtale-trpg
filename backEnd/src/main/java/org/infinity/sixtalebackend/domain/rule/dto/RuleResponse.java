package org.infinity.sixtalebackend.domain.rule.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.infinity.sixtalebackend.domain.room.domain.RoomStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class RuleResponse {
    // 룰
    private Long id;
    private String title;
    private String description;
    // NPC
    private String npc;
    private String npcID;
    private String npcName;
    private String npcDescription;
    private String npcHp;
    private String npcGlove;
    private String npcImageURL;
    // 공통 액션
    private String commonActionID;
    private String commonActionName;
    private String commonActionIsBasic;
    private String commonActionDescription;
    private String commonActionIsDice;
    private String commonActionDiceType;
    private String commonActionDiceCount;
    // 직업
    private String jobID;
    private String jobName;
    private String jobDescription;
    private String jobHp;
    private String jobWeight;
    private String jobDiceType;
    private String jobImageURL;
    //

    private Byte maxCount;
    private Boolean isLocked;
    private String password;
    private Boolean isShortStory;
    private Boolean isVoice;
    private Boolean isCamera;
    private RoomStatus status;
    private LocalDateTime nextPlay;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalTime playTime;
}
