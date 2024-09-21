package org.infinity.sixtalebackend.domain.character_sheet.repository;

import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterSheet;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CharacterSheetRepository extends JpaRepository<CharacterSheet, Long> {
    @Query("SELECT cs " +
            "FROM CharacterSheet cs " +
            "JOIN FETCH cs.job j " +
            "JOIN FETCH cs.race r " +
            "JOIN FETCH cs.belief b " +
            "LEFT JOIN FETCH cs.characterStats st " +
            "LEFT JOIN FETCH cs.characterEquipments eq " +
            "LEFT JOIN FETCH cs.characterActions act " +
            "LEFT JOIN FETCH act.jobAction ja " +
            "LEFT JOIN FETCH act.actionOption ao " +
            "LEFT JOIN FETCH eq.equipment e " +
            "LEFT JOIN FETCH e.equipmentType et " +
            "LEFT JOIN FETCH j.jobBeliefs jb " +
            "LEFT JOIN FETCH jb.belief " +
            "LEFT JOIN FETCH j.jobRaces jr " +
            "LEFT JOIN FETCH jr.race " +
            "WHERE cs.playMember.id = :playMemberID")
    Optional<CharacterSheet> findByPlayMemberIdWithFetch(@Param("playMemberID") Long playMemberID);

    Optional<CharacterSheet> findByPlayMember(PlayMember playMember);

    Optional<CharacterSheet> findByPlayMemberId(Long playMemberID);

    @Query("SELECT cs FROM CharacterSheet cs " +
            "JOIN FETCH cs.job j " +
            "LEFT JOIN FETCH cs.characterActions ca " +
            "LEFT JOIN FETCH ca.jobAction ja " +
            "LEFT JOIN FETCH ja.actionOptions ao " + // 모두 다 가져오는게 좋으려나? 오히려 손해?
            "WHERE cs.playMember.id = :playMemberId")
    Optional<CharacterSheet> findByPlayMemberWithActions(@Param("playMemberId") Long playMemberId);
}
