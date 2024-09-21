package org.infinity.sixtalebackend.domain.friend.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class FriendID implements Serializable {
    private Long friend1ID;
    private Long friend2ID;
}
