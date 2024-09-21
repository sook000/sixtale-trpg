package org.infinity.sixtalebackend.domain.rule.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.infinity.sixtalebackend.domain.equipment.domain.Equipment;
import org.infinity.sixtalebackend.domain.equipment.dto.EquipmentResponse;
import org.infinity.sixtalebackend.domain.rule.domain.*;

import java.util.List;

@Getter
@Setter
@Builder
public class JobOptionListResponse {
    private List<JobRaceResponse> jobRaceList;
    private List<JobBeliefResponse> jobBeliefList;
    private List<EquipmentResponse> jobEquipmentList;
    private List<JobActionResponse> jobActionList;
    private List<List<ActionOption>> actionOptionList;
}
