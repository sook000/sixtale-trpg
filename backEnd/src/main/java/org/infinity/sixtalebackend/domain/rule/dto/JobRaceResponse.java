package org.infinity.sixtalebackend.domain.rule.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.infinity.sixtalebackend.domain.rule.domain.Job;
import org.infinity.sixtalebackend.domain.rule.domain.JobRaceID;
import org.infinity.sixtalebackend.domain.rule.domain.Race;

@Getter
@Builder
public class JobRaceResponse {
    private Long raceID;
    private String raceName;
    private String description;
}
