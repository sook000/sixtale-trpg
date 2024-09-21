package org.infinity.sixtalebackend.domain.rule.repository;

import org.infinity.sixtalebackend.domain.rule.domain.Job;
import org.infinity.sixtalebackend.domain.rule.domain.JobAction;
import org.infinity.sixtalebackend.domain.rule.domain.Rule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobActionRepository extends JpaRepository<JobAction, Long> {
    List<JobAction> findByRule(Rule rule);

    List<JobAction> findByRuleAndJob(Rule rule, Job job);

    @EntityGraph(attributePaths = {"actionOptions"})
    List<JobAction> findByJob_IdAndLevelLessThanEqual(Long jobId, Integer level);
}
