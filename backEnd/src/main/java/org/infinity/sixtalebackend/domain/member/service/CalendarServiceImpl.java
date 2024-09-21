package org.infinity.sixtalebackend.domain.member.service;

import lombok.AllArgsConstructor;
import org.infinity.sixtalebackend.domain.member.exception.InvalidDateException;
import org.infinity.sixtalebackend.domain.member.repository.CalendarRepository;
import org.infinity.sixtalebackend.domain.member.repository.MemberRepository;
import org.infinity.sixtalebackend.domain.member.domain.Calender;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.member.dto.CalendarListResponse;
import org.infinity.sixtalebackend.domain.member.dto.CalendarRequest;
import org.infinity.sixtalebackend.domain.member.dto.CalendarResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CalendarServiceImpl implements CalendarService{
    private final CalendarRepository calendarRepository;
    private final MemberRepository memberRepository;

    public CalendarListResponse getAllCalendars(Long id) {
        Member member = findMember(id);
        List<Calender> calenders = calendarRepository.findByMember(member);
        List<CalendarResponse> dataList = calenders.stream().map(calendar -> {
            CalendarResponse response = new CalendarResponse();
            response.setStartAt(calendar.getStartAt());
            response.setEndAt(calendar.getEndAt());
            response.setTitle(calendar.getTitle());
            return response;
        }).collect(Collectors.toList());

        CalendarListResponse response = new CalendarListResponse();
        response.setDateList(dataList);
        return response;
    }

    @Transactional
    public void createCalendar(Long id, CalendarRequest calendarRequest) {
        Member member = findMember(id);
        if (calendarRequest.getStartAt().isAfter(calendarRequest.getEndAt())) {
            throw new InvalidDateException("시작 시간이 종료 시간보다 나중일 수 없습니다.");
        }
        Calender calender = Calender.builder()
                .member(member)
                .startAt(calendarRequest.getStartAt())
                .endAt(calendarRequest.getEndAt())
                .title(calendarRequest.getTitle())
                .build();
        calendarRepository.save(calender);

    }

    @Transactional
    public void deleteCalendar(Long calendarId, Long id) {
        Member member = findMember(id);
        Calender calender = calendarRepository.findByIdAndMember(calendarId, member).orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        calendarRepository.delete(calender);
    }
    private Member findMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return member;
    }
}
