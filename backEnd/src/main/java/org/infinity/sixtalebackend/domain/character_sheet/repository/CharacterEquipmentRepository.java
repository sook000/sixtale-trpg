package org.infinity.sixtalebackend.domain.character_sheet.repository;

import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterEquipment;
import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterSheet;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CharacterEquipmentRepository extends JpaRepository<CharacterEquipment, Long> {
    List<CharacterEquipment> findByPlayMember(PlayMember playMember);

    void deleteByPlayMember(PlayMember playMember);


    // 특정 CharacterSheet 및 ScenarioEquipment에 해당하는 장비
    @Query("SELECT ce FROM CharacterEquipment ce WHERE ce.characterSheet = :characterSheet AND ce.equipment = :equipment")
    CharacterEquipment findByCharacterSheetAndEquipment(@Param("characterSheet") CharacterSheet characterSheet, @Param("equipment") ScenarioEquipment equipment);

    Optional<CharacterEquipment> findByCharacterSheetAndEquipment_Id(CharacterSheet characterSheet, Long equipmentID);
}
