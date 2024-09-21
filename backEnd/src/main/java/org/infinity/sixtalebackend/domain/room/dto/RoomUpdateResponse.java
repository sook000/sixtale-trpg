package org.infinity.sixtalebackend.domain.room.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.infinity.sixtalebackend.domain.room.domain.RoomStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class RoomUpdateResponse {
    private Long id;
    private String title;
    private String description;
    private Byte maxCount;
    private Byte currentCount;
    private Boolean isLocked;
    private String password;
    private Boolean isShortStory;
    private Boolean isVoice;
    private RoomStatus status;
    private LocalDateTime nextPlay;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalTime playTime;
}
