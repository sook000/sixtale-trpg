package org.infinity.sixtalebackend.domain.friend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.infinity.sixtalebackend.domain.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {
    @EmbeddedId
    private FriendID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("friend1ID")
    @JoinColumn(name = "friend1_id", nullable = false)
    private Member member1;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("friend2ID")
    @JoinColumn(name = "friend2_id", nullable = false)
    private Member member2;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }



}
