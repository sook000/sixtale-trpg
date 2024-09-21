package org.infinity.sixtalebackend.domain.character_sheet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class CharacterEquipmentRequest {
    @NotNull
    private Long equipmentId;

    @NotNull
    private Integer currentCount;

    @NotNull
    private Integer weight;
}
