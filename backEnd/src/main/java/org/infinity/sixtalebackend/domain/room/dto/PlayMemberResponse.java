package org.infinity.sixtalebackend.domain.room.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlayMemberResponse {
    private Long playMemberID;
    private Long memberID;
    private String playMemberNickname;
    private String playMemberImageURL;
}
