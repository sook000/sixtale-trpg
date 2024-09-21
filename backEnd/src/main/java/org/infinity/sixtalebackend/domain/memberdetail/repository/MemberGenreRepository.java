package org.infinity.sixtalebackend.domain.memberdetail.repository;

import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.memberdetail.domain.MemberGenre;
import org.infinity.sixtalebackend.domain.memberdetail.domain.MemberGenreID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface MemberGenreRepository extends JpaRepository<MemberGenre, MemberGenreID> {
    List<MemberGenre> findMemberGenreByMember(Member member);
    void deleteAllByMember(Member member);

}
