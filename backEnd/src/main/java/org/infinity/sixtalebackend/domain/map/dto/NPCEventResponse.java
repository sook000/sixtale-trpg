package org.infinity.sixtalebackend.domain.map.dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.infinity.sixtalebackend.domain.map.domain.Map;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioNPC;

@Getter
@Builder
public class NPCEventResponse {

    private Long id;
    private Long mapId;
    private String description;
    private Integer currentHp;

}
