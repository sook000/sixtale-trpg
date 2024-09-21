package org.infinity.sixtalebackend.domain.friend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.model.BaseTimeEntity;

@Entity
@Getter
@Builder
@Table(name = "friend_request")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequest extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sendMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiveMember;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = FriendRequestStatus.PENDING;
        }
    }




}
