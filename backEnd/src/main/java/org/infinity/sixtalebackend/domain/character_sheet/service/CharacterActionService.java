package org.infinity.sixtalebackend.domain.character_sheet.service;

import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterActionListResponse;
import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterActionRequest;

public interface CharacterActionService {
    CharacterActionListResponse getCharacterActions(Long roomID, Long playMemberID);
    void addCharacterAction(Long roomID, Long playMemberID, CharacterActionRequest actionRequest);
    void deleteCharacterAction(Long roomID, Long playMemberID, Long characterActionID);
}
