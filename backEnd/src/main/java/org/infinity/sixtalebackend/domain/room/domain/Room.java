package org.infinity.sixtalebackend.domain.room.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.model.BaseTimeEntity;
import org.infinity.sixtalebackend.domain.rule.domain.Rule;
import org.infinity.sixtalebackend.domain.scenario.domain.Scenario;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "room")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private Rule rule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gm_id", nullable = false)
    private Member gm;

    @Column(nullable = false, length = 100)
    private String title;

    private String description;

    @ColumnDefault("1")
    @Column(nullable = false)
    private Byte currentCount;

    @ColumnDefault("8")
    @Column(nullable = false)
    private Byte maxCount;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean isLocked;

    @Column(length = 64)
    private String password;

    @ColumnDefault("true")
    @Column(nullable = false)
    private Boolean isShortStory;

    @ColumnDefault("true")
    @Column(nullable = false)
    private Boolean isVoice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @Column(nullable = false)
    private LocalDateTime nextPlay;

    @Column(nullable = false)
    private LocalTime playTime;

    // getRoomDetails을 위한 매핑
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PlayMember> playMembers;

    // 엔티티가 영속성 컨텍스트에 저장되기 전에 호출
    // 시작시간, 끝시간 default 설정
    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = RoomStatus.WAITING;
        }
        if (nextPlay == null) {
            nextPlay = LocalDateTime.now().plusDays(1); //기본 값 설정
        }
        if (playTime == null) {
            playTime = LocalTime.of(0, 0, 0);
        }
    }
}
