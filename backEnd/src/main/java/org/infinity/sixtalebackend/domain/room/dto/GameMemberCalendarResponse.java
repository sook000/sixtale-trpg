package org.infinity.sixtalebackend.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GameMemberCalendarResponse {
    private Long gameMemberID;
    private List<CalendarEventResponse> dateList;
}
