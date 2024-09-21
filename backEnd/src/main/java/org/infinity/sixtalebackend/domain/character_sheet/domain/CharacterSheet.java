package org.infinity.sixtalebackend.domain.character_sheet.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.infinity.sixtalebackend.domain.rule.domain.*;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@Setter
@Table(name = "character_sheet")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharacterSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "play_member_id")
    private PlayMember playMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belief_id", nullable = false)
    private Belief belief;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String appearance;

    @Column(nullable = false)
    private String background;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer currentWeight;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer currentHp;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer currentMoney;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer limitWeight;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer limitHp;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer glove;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer inspirationScore;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer level;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer exp;

    @Column(nullable = false, name="image_url", columnDefinition = "TEXT")
    private String imageURL;

    @OneToMany(mappedBy = "characterSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private Set<CharacterStat> characterStats;

    @OneToMany(mappedBy = "characterSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private Set<CharacterEquipment> characterEquipments;

    @OneToMany(mappedBy = "characterSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private Set<CharacterAction> characterActions;
}
