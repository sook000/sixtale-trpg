package org.infinity.sixtalebackend.domain.room.repository;

import org.infinity.sixtalebackend.domain.room.domain.Room;
import org.infinity.sixtalebackend.domain.room.domain.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r " +
            "JOIN FETCH r.scenario s " +
            "JOIN FETCH r.rule ru " +
            "JOIN FETCH r.gm g " +
            "LEFT JOIN FETCH r.playMembers pm " +
            "LEFT JOIN FETCH pm.member " +
            "WHERE r.id = :roomId")
    Optional<Room> findByIdWithDetails(Long roomId);

    @Query("SELECT r FROM Room r WHERE r.status != 'COMPLETE' AND (:status IS NULL OR r.status = :status) AND (:title IS NULL OR r.title LIKE %:title%)")
    Page<Room> findByStatusAndTitle(RoomStatus status, String title, Pageable pageable);

    @Query("SELECT r FROM Room r JOIN r.playMembers pm WHERE pm.member.id = :memberId")
    Page<Room> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

}
