package org.infinity.sixtalebackend.domain.scenario.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class ScenarioLikeID implements Serializable {
    @Column(name = "scenario_id", nullable = false)
    private Long scenarioID;

    @Column(name = "member_id", nullable = false)
    private Long memberID;

    public ScenarioLikeID(Long scenarioID, Long memberID) {
        this.scenarioID = scenarioID;
        this.memberID = memberID;
    }
}
