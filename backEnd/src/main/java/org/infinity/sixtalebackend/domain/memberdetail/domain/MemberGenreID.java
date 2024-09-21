package org.infinity.sixtalebackend.domain.memberdetail.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

// 복합키 설정을 위한 EmbeddedId 설정

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MemberGenreID implements Serializable {
    private Long memberID;
    private Long genreID;

}
