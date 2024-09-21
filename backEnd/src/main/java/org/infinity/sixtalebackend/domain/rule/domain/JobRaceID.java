package org.infinity.sixtalebackend.domain.rule.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JobRaceID implements Serializable {
    private Long jobID;
    private Long raceID;

}

