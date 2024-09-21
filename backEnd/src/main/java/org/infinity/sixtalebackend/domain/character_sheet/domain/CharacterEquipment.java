package org.infinity.sixtalebackend.domain.character_sheet.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioEquipment;

@Entity
@Getter
@Setter
@Builder
@Table(name = "character_equipment")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharacterEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private ScenarioEquipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "play_member_id", nullable = false)
    private PlayMember playMember;

    private Integer currentCount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_sheet_id", nullable = false)
    private CharacterSheet characterSheet;

}
