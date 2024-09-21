package org.infinity.sixtalebackend.domain.character_sheet.service;

import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterGoldUpdateRequest;
import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterSheetRequest;
import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterSheetResponse;
import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterSheetUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CharacterSheetService {
//    void createCharacterSheet(Long roomID, CharacterSheetRequest characterSheetRequest, Long memberID, MultipartFile[] files) throws IOException;
    void createCharacterSheet(Long roomID, CharacterSheetRequest characterSheetRequest, Long memberID) throws IOException;
    void updateCharacterSheet(Long roomID, Long playMemberID, CharacterSheetUpdateRequest characterSheetUpdateRequest, MultipartFile[] files) throws IOException;
    CharacterSheetResponse getCharacterSheet(Long roomID, Long playMemberID);
    void deleteCharacterSheet(Long roomID, Long memberID);

    Map<String, String> updateCharacterGold(Long roomID, Long playMemberID, CharacterGoldUpdateRequest characterGoldUpdateRequest);

    void updateCharacterSheetInPlaying(Long roomID, Long playMemberID, CharacterSheetUpdateRequest characterSheetUpdateRequest);
}
