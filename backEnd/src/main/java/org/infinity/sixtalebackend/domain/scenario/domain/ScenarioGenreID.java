package org.infinity.sixtalebackend.domain.scenario.domain;

import jakarta.persistence.*;
import lombok.*;
import org.infinity.sixtalebackend.domain.genre.domain.Genre;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class ScenarioGenreID implements Serializable {

    @Column(name = "scenario_id", nullable = false)
    private Long scenarioID;

    @Column(name = "genre_id", nullable = false)
    private Long genreID;

}
