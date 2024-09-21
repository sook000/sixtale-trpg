package org.infinity.sixtalebackend.domain.character_sheet.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActionOptionResponse {
    private Long id;
    private String content;
}

