package org.infinity.sixtalebackend.domain.member.repository;

import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByNickname(String nickname);

    Member findByAccessToken(String token);
}
