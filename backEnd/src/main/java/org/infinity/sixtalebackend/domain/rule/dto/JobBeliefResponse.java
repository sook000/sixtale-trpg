package org.infinity.sixtalebackend.domain.rule.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.infinity.sixtalebackend.domain.rule.domain.Belief;
import org.infinity.sixtalebackend.domain.rule.domain.Job;
import org.infinity.sixtalebackend.domain.rule.domain.JobBeliefID;

@Getter
@Builder
public class JobBeliefResponse {
    private Long beliefID;
    private String beliefName;
    private String description;
}
