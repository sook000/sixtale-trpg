package org.infinity.sixtalebackend.domain.room.repository;

import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.infinity.sixtalebackend.domain.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface PlayMemberRepository extends JpaRepository<PlayMember, Long> {
    boolean existsByRoomAndMember(Room room, Member member);
    Optional<PlayMember> findByRoomAndMember(Room room, Member member);
    List<PlayMember> findByRoom(Room room);
    Optional<PlayMember> findByMemberIdAndRoomId(Long memberId, Long roomId);

    Optional<PlayMember> findByIdAndRoomId(Long playMemberID, Long roomID);
}
