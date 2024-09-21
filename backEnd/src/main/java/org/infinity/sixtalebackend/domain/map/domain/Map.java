package org.infinity.sixtalebackend.domain.map.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.infinity.sixtalebackend.domain.scenario.domain.Scenario;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Table(name = "map")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Map {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isNpc;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isPlace;

    @Column(name="image_url")
    private String imageURL;

    @Column(name="bgm_url")
    private String bgmURL;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}
