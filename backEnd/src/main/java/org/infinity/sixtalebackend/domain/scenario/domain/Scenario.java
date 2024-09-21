package org.infinity.sixtalebackend.domain.scenario.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.model.BaseTimeEntity;
import org.infinity.sixtalebackend.domain.rule.domain.Rule;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Table(name = "scenario")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scenario extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    @Column(nullable = false, length = 512)
    private String summary;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer likes;

    @Column(nullable = false)
    @ColumnDefault("4")
    private Byte optimalCount;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isPublic;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isOpen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private Rule rule;

    @Column(name = "image_url")
    private String imageURL;

    @Column(name = "gif_url")
    private String gifURL;

    // 좋아요수 증가
    public void incrementLikes() {
        this.likes += 1;
    }

    // 좋아요수 감소
    public void decrementLikes() {
        this.likes -= 1;
    }
}
