package org.infinity.sixtalebackend.domain.scenario.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ScenarioListResponseDto {
    private List<ScenarioResponseDto> scenarioList;
    private Integer totalPages;
    private Long totalElements;
}
