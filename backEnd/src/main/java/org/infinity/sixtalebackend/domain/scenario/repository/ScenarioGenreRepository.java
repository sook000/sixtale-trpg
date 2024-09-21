package org.infinity.sixtalebackend.domain.scenario.repository;


import org.infinity.sixtalebackend.domain.scenario.domain.Scenario;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioGenre;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioGenreID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScenarioGenreRepository extends JpaRepository<ScenarioGenre, ScenarioGenreID> {
    List<ScenarioGenre> findScenarioGenreByScenario(Scenario scenario);
}
