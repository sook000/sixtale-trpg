package org.infinity.sixtalebackend.domain.scenario.repository;

import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.scenario.domain.Scenario;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioLike;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioLikeID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioLikeRepository extends JpaRepository<ScenarioLike, ScenarioLikeID> {
    Boolean existsByScenarioAndMember(Scenario scenario, Member member);

    void deleteByScenarioAndMember(Scenario scenario, Member member);
}
