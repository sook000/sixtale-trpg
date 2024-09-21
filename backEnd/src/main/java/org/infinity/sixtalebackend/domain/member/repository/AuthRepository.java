package org.infinity.sixtalebackend.domain.member.repository;

import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
}
