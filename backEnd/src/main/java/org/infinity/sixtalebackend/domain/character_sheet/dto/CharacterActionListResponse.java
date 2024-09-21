package org.infinity.sixtalebackend.domain.character_sheet.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CharacterActionListResponse {
    private List<CharacterActionResponse> coreActions;
    private List<CharacterActionResponse> advancedActions;

}
