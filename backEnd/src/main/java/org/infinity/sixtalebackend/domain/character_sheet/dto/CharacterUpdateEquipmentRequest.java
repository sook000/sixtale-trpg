package org.infinity.sixtalebackend.domain.character_sheet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class CharacterUpdateEquipmentRequest {
    @NotNull
    private Long equipmentId;

    @NotNull
    private Integer currentCount;
}
