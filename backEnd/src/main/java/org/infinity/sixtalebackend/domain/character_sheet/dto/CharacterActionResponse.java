package org.infinity.sixtalebackend.domain.character_sheet.dto;

import lombok.Builder;
import lombok.Getter;
import org.infinity.sixtalebackend.domain.model.DiceType;

import java.util.List;

@Getter
@Builder
public class CharacterActionResponse {
    private Long id;
    private Long actionID;
    private String name;
    private Boolean isCore;
    private String description;
    private Boolean isDice;
    private DiceType diceType;
    private Integer diceCount;
    private Integer level;
    private List<ActionOptionResponse> actionOption;
}
