/*
package org.infinity.sixtalebackend.domain.member.repository;

import org.infinity.sixtalebackend.domain.member.service.CalendarServiceImpl;
import org.infinity.sixtalebackend.domain.member.domain.Calender;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.member.domain.Provider;
import org.infinity.sixtalebackend.domain.member.dto.CalendarListResponse;
import org.infinity.sixtalebackend.domain.member.dto.CalendarRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class CalendarServiceTest {

    @Autowired
    private CalendarServiceImpl calendarService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    private Member member1;
    private Member member2;

    @BeforeEach
    public void setUp() {
        member1 = Member.builder().
                email("test@example.com")
                .nickname("testuser")
                .accessToken("dummyToken")
                .provider(Provider.GOOGLE)
                .providerUserID("providerUserId")
                .isWithdrawn(true)
                .lastLoginAt(LocalDateTime.now())
                .build();
        member2 = Member.builder()
                .email("test2@example.com")
                .nickname("testuser2")
                .accessToken("dummyToken2")
                .provider(Provider.GOOGLE)
                .providerUserID("providerUserId2")
                .isWithdrawn(true)
                .lastLoginAt(LocalDateTime.now())
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);


        Calender calendar1 = Calender.builder()
                .member(member1)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusHours(1))
                .title("Test Event 1")
                .build();

        Calender calendar2 = Calender.builder()
                .member(member2)
                .startAt(LocalDateTime.now().plusDays(1))
                .endAt(LocalDateTime.now().plusDays(1).plusHours(1))
                .title("Test Event 2")
                .build();

        calendarRepository.save(calendar1);
        calendarRepository.save(calendar2);
    }
    @Test
    public void testGetAllCalendars() {
        CalendarListResponse response = calendarService.getAllCalendars(member1.getId());
        assertThat(response.getDateList()).hasSize(1);
        assertThat(response.getDateList().get(0).getTitle()).isEqualTo("Test Event 1");
    }
//    @Test
//    public void testCreateCalendar() {
//        CalendarRequest request = new CalendarRequest();
//        request.setStartAt(LocalDateTime.now());
//        request.setEndAt(LocalDateTime.now().plusHours(2));
//        request.setTitle("New Event");
//
//        calendarService.createCalendar(member1.getId(), request);
//
//        List<Calender> calendars = calendarRepository.findByMember(member1);
//        assertThat(calendars).hasSize(2);
//        assertThat(calendars.get(1).getTitle()).isEqualTo("New Event");
//    }
    @Test
    public void testDeleteCalendar() {
        Calender calendar = calendarRepository.findByMember(member1).get(0);
        calendarService.deleteCalendar(calendar.getId(), member1.getId());

        List<Calender> calendars = calendarRepository.findByMember(member1);
        assertThat(calendars).isEmpty();
    }
    @Test
    public void testGetAllCalendarsForNonExistentMember() {
        assertThrows(IllegalArgumentException.class, () -> {
            calendarService.getAllCalendars(999L);
        });


    }
    @Test
    public void testDeleteNonExistentCalendar() {
        assertThrows(IllegalArgumentException.class, () -> {
            calendarService.deleteCalendar(999L, member1.getId());
        });
    }
}
*/