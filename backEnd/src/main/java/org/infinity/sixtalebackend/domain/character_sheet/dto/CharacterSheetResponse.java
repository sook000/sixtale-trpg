package org.infinity.sixtalebackend.domain.character_sheet.dto;

import lombok.Builder;
import lombok.Getter;
import org.infinity.sixtalebackend.domain.model.DiceType;

import java.util.List;

@Getter
@Builder
public class CharacterSheetResponse {
    private Long jobId;
    private String jobName;
    private DiceType jobDiceType; //피해 주사위 종류
    private Long raceId;
    private String raceName;
    private String raceDescription;
    private Long beliefId;
    private String beliefName;
    private String beliefDescription;
    private String name;
    private String appearance;
    private String background;
    private List<CharacterStatResponse> stat;
    private List<CharacterActionResponse> characterAction;
    private List<CharacterEquipmentResponse> characterEquipment;
    private Integer currentWeight;
    private Integer currentHp;
    private Integer currentMoney;
    private Integer limitWeight;
    private Integer limitHp;
    private Integer glove;
    private Integer inspirationScore;
    private Integer level;
    private Integer exp;
    private String imageURL;
}
