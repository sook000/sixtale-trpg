package org.infinity.sixtalebackend.domain.room.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUpdateRequest {
    private String title;
    private String description;
    private Boolean isLocked;
    private String password;
    private Boolean isShortStory;
    private Boolean isVoice;
}
