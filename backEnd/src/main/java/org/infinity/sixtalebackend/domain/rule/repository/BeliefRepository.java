package org.infinity.sixtalebackend.domain.rule.repository;

import org.infinity.sixtalebackend.domain.rule.domain.Belief;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeliefRepository extends JpaRepository<Belief, Long> {
}
