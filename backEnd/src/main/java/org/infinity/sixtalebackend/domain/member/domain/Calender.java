package org.infinity.sixtalebackend.domain.member.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Table(name = "calendar")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(length = 30)
    private String title;

    // 엔티티가 영속성 컨텍스트에 저장되기 전에 호출
    // 시작시간, 끝시간 default 설정
    @PrePersist
    public void prePersist() {
        if (startAt == null) {
            startAt = LocalDateTime.now();
        }
        if (endAt == null) {
            endAt = startAt.plusHours(1);
        }
    }
}
