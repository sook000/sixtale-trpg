package org.infinity.sixtalebackend.domain.room.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RoomCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private Long scenarioID;

    private String description;

    @NotBlank
    private Byte maxCount;

    @NotBlank
    private Boolean isLocked;

    @NotBlank
    private String password;

    @NotBlank
    private Boolean isShortStory;

    @NotBlank
    private Boolean isVoice;
}
