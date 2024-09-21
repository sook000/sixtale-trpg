package org.infinity.sixtalebackend.domain.memberdetail.repository;

import org.infinity.sixtalebackend.domain.memberdetail.domain.MemberDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface MemberDetailRepository extends JpaRepository<MemberDetail,Long> {
    
}
