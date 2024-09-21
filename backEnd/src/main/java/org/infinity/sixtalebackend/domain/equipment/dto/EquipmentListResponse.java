package org.infinity.sixtalebackend.domain.equipment.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EquipmentListResponse {

    private List<EquipmentResponse> equipments;
}
