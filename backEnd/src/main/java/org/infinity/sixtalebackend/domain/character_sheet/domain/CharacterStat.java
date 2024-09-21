package org.infinity.sixtalebackend.domain.character_sheet.domain;

import jakarta.persistence.*;
import lombok.*;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.infinity.sixtalebackend.domain.rule.domain.Stat;

@Entity
@Getter
@Builder
@Table(name = "character_stat")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharacterStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "play_member_id", nullable = false)
    private PlayMember playMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stat_id", nullable = false)
    private Stat stat;

    @Column(nullable = false)
    private Integer statValue;

    @Column(nullable = false)
    private Integer statWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_sheet_id", nullable = false)
    private CharacterSheet characterSheet;

    public void setStatValue(Integer statValue) {
        this.statValue = statValue;
    }

    public void setStatWeight(Integer statWeight) {
        this.statWeight = statWeight;
    }
}
