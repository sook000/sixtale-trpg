package org.infinity.sixtalebackend.domain.character_sheet.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CharacterStatResponse {
    private Long statID;
    private Integer statValue;
    private Integer statWeight;
}
