package org.infinity.sixtalebackend.domain.member.repository;

import org.infinity.sixtalebackend.domain.member.domain.Calender;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarRepository extends JpaRepository<Calender, Long>{
    List<Calender> findByMember(Member member);

    Optional<Calender> findByIdAndMember(Long id, Member member);

    @Query("SELECT c FROM Calender c JOIN FETCH c.member m WHERE m.id IN :memberIds")
    List<Calender> findByMemberIds(List<Long> memberIds);
}
