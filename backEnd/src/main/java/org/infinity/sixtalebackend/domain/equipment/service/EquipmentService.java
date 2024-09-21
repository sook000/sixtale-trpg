package org.infinity.sixtalebackend.domain.equipment.service;

import org.infinity.sixtalebackend.domain.equipment.dto.EquipmentListResponse;

public interface EquipmentService {
    EquipmentListResponse readEquipmentList(Long ruleID);
}
