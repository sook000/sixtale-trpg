package org.infinity.sixtalebackend.domain.rule.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobRace {
    @EmbeddedId
    private JobRaceID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jobID")
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("raceID")
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    private String description;
}

