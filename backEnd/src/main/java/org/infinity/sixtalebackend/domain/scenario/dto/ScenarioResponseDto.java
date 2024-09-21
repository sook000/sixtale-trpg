package org.infinity.sixtalebackend.domain.scenario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.infinity.sixtalebackend.domain.memberdetail.dto.GenreDto;
import org.infinity.sixtalebackend.domain.scenario.domain.Scenario;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class ScenarioResponseDto {
    private Long id;
    private String title;
    // member
    private Long writerID;
    private String nickName;
    //genre
    private List<GenreDto> genreList;
    private String summary;
    private String description;
    private Integer likes;
    private Byte optimalCount;
    private Boolean isPublic;
    private Boolean isOpen;
    //rule
    private Long ruleID;
    private String ruleTitle;

    private String imageURL;
    private String gifURL;
    private LocalDateTime updatedAt;

    private Boolean isLiked;


}
