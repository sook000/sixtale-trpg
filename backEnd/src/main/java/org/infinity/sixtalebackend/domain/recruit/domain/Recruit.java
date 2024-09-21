package org.infinity.sixtalebackend.domain.recruit.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.model.BaseTimeEntity;
import org.infinity.sixtalebackend.domain.scenario.domain.Scenario;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class Recruit extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = true)
    private Scenario scenario;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ColumnDefault("false")
    private Boolean isDeleted;

}
