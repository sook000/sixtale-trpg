package org.infinity.sixtalebackend.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CalendarEventResponse {
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String title;
}
