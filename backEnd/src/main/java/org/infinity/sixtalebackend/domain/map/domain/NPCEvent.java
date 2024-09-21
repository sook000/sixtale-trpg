package org.infinity.sixtalebackend.domain.map.domain;

import jakarta.persistence.*;
import lombok.*;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioNPC;

@Entity
@Getter
@Setter
@Builder
@Table(name = "npc_event")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NPCEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id", nullable = false)
    private Map map;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "npc_id")
    private ScenarioNPC scenarioNPC;

    private Integer currentHp;

}
