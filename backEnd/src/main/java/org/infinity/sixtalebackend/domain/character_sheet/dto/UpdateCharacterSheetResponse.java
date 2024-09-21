package org.infinity.sixtalebackend.domain.character_sheet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateCharacterSheetResponse {
    private Long roomID;
    private String type;
    private String content;
    private String createdAt;
    private Long playMemberID;
    private String sheetID;
    private String characterName;
}
