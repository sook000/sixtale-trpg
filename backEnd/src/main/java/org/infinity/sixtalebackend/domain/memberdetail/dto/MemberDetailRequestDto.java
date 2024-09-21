package org.infinity.sixtalebackend.domain.memberdetail.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MemberDetailRequestDto {

    private String favorRule;

    @NotBlank // 공백, 빈문자열, null 제한
    private String rpType;
    @NotBlank
    private String chatType;
    @NotBlank
    private String talkType;
    @NotBlank
    private String tasteType;
    @NotBlank
    private String systemType;
    @NotBlank
    private String timeType;
    @NotNull
    @NotEmpty
    private List<Long> genreList;
}
