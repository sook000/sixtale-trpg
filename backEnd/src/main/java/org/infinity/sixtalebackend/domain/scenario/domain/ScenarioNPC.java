package org.infinity.sixtalebackend.domain.scenario.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name="scenario_npc")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScenarioNPC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer hp;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer glove;

    @Column(name = "image_url")
    private String imageURL;
}
