package org.infinity.sixtalebackend.domain.scenario.domain;

import jakarta.persistence.*;
import lombok.*;
import org.infinity.sixtalebackend.domain.genre.domain.Genre;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScenarioGenre {
    @EmbeddedId
    private ScenarioGenreID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scenarioID")
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("genreID")
    @JoinColumn(name = "genre_id")
    private Genre genre;
}
