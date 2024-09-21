package org.infinity.sixtalebackend.domain.member.service;

import org.infinity.sixtalebackend.domain.member.dto.CalendarListResponse;
import org.infinity.sixtalebackend.domain.member.dto.CalendarRequest;

public interface CalendarService{

    public CalendarListResponse getAllCalendars(Long id);
        public void createCalendar(Long id, CalendarRequest calendarRequest);
        public void deleteCalendar(Long calendarId, Long id);
}
