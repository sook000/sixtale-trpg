package org.infinity.sixtalebackend.domain.memberdetail.domain;

import jakarta.persistence.*;
import lombok.*;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.genre.domain.Genre;

@Entity
@Getter
@Builder
@Table(name = "member_genre")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberGenre {
    @EmbeddedId
    private MemberGenreID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberID")
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("genreID")
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;
}
