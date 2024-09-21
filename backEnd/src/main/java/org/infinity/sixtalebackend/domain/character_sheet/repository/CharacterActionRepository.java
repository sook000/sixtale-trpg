package org.infinity.sixtalebackend.domain.character_sheet.repository;

import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterAction;
import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterSheet;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.infinity.sixtalebackend.domain.rule.domain.JobAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CharacterActionRepository extends JpaRepository<CharacterAction, Long> {
    void deleteByPlayMember(PlayMember playMember);

    List<CharacterAction> findByPlayMember(PlayMember playMember);
    List<CharacterAction> findByCharacterSheet(CharacterSheet characterSheet);

    Optional<CharacterAction> findByCharacterSheetAndJobActionId(CharacterSheet characterSheet, Long jobActionId);

    Optional<CharacterAction> findByCharacterSheetAndJobAction(CharacterSheet characterSheet, JobAction jobAction);

    Optional<CharacterAction> findByIdAndCharacterSheet(Long characterActionID, CharacterSheet characterSheet);
}
