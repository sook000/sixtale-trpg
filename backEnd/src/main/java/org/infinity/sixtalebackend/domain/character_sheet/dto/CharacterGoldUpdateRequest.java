package org.infinity.sixtalebackend.domain.character_sheet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CharacterGoldUpdateRequest {
    @NotNull
    private Integer currentMoney;
}