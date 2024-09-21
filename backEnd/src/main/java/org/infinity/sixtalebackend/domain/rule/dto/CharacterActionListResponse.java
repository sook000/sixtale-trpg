package org.infinity.sixtalebackend.domain.rule.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CharacterActionListResponse {
    private List<CharacterActionResponse> basicActions;
    private List<CharacterActionResponse> specialActions;
}
