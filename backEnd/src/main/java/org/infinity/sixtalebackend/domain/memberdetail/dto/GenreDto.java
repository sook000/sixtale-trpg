package org.infinity.sixtalebackend.domain.memberdetail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GenreDto {
    private Long id;
    private String name;
}
