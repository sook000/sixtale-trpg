package org.infinity.sixtalebackend.domain.character_sheet.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CharacterSheetUpdateRequest {
    //    private Long playMemberId;
    @NotNull
    private Long jobId;

    @NotNull
    private Long raceId;

    @NotNull
    private Long beliefId;

    @NotBlank
    private String name;

    @NotBlank
    private String appearance;

    @NotBlank
    private String background;

    @NotEmpty
    private List<CharacterStatRequest> stat;

    @NotNull
    private Integer currentWeight;

    @NotNull
    private Integer currentHp;

    @NotNull
    private Integer currentMoney;

    @NotNull
    private Integer limitWeight;

    @NotNull
    private Integer limitHp;

    @NotNull
    private Integer glove;

    @NotNull
    private Integer inspirationScore;

    @NotNull
    private Integer level;

    @NotNull
    private Integer exp;
}
