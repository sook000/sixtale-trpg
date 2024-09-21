package org.infinity.sixtalebackend.domain.rule.repository;

import org.infinity.sixtalebackend.domain.rule.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRaceRepository extends JpaRepository<JobRace, JobRaceID> {
    List<JobRace> findByJob(Job job);
}
