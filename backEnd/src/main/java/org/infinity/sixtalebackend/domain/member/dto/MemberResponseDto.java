package org.infinity.sixtalebackend.domain.member.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    @NonNull
    private String nickName;
    private String imageURL;
    private LocalDateTime createdAt;

}
