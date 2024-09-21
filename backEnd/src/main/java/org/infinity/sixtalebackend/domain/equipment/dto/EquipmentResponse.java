package org.infinity.sixtalebackend.domain.equipment.dto;

import lombok.Builder;
import lombok.Getter;
import org.infinity.sixtalebackend.domain.equipment.domain.EquipmentType;
import org.infinity.sixtalebackend.domain.rule.domain.Job;

@Getter
@Builder
public class EquipmentResponse {
    private Long id;
    private String name;
    private String description;
    private EquipmentType equipmentType;
    private Integer weight;
    private Integer count;
    private Long ruleId;
    private Long jobId;
    private String imageUrl;
}
