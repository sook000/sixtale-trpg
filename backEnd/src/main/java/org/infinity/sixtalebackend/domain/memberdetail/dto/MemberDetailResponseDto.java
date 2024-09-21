package org.infinity.sixtalebackend.domain.memberdetail.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MemberDetailResponseDto {
    private String favorRule;
    private String rpType;
    private String chatType;
    private String talkType;
    private String tasteType;
    private String systemType;
    private String timeType;
    private List<GenreDto> genreList;
}
