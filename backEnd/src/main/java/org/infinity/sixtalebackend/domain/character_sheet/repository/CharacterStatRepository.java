package org.infinity.sixtalebackend.domain.character_sheet.repository;

import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterStat;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface CharacterStatRepository extends JpaRepository<CharacterStat, Long> {
    void deleteByPlayMember(PlayMember playMember);

    List<CharacterStat> findByPlayMember(PlayMember playMember);
}
