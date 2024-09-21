package org.infinity.sixtalebackend.domain.rule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatResponse {
    private Long id;
    private String name;
    private Long ruleId;
}
