package org.infinity.sixtalebackend.domain.rule.repository;

import com.jayway.jsonpath.JsonPath;
import org.infinity.sixtalebackend.domain.rule.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobBeliefRepository extends JpaRepository<JobBelief, JobBeliefID> {
    List<JobBelief> findByJob(Job job);
}
