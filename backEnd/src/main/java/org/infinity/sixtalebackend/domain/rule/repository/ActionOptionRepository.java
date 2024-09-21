package org.infinity.sixtalebackend.domain.rule.repository;

import org.infinity.sixtalebackend.domain.rule.domain.ActionOption;
import org.infinity.sixtalebackend.domain.rule.domain.Job;
import org.infinity.sixtalebackend.domain.rule.domain.JobAction;
import org.infinity.sixtalebackend.domain.rule.domain.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionOptionRepository extends JpaRepository<ActionOption, Long> {
    List<ActionOption> findByJobAction(JobAction jobAction);
}
