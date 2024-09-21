package org.infinity.sixtalebackend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class CalendarResponse {
    @NotBlank
    private LocalDateTime startAt;

    @NotBlank
    private LocalDateTime endAt;

    private String title;
}
