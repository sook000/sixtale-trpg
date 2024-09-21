package org.infinity.sixtalebackend.domain.memberdetail.domain;

import jakarta.persistence.*;
import lombok.*;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.memberdetail.dto.MemberDetailRequestDto;

@Entity
@Getter
@Builder
@Table(name = "member_detail")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail {

    @Id
    @Column(name = "member_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    private String favorRule;

    @Column(name = "rp_type",nullable = false)
    private Integer rpType ;

    @Column(name = "chat_type", nullable = false)
    private Integer chatType ;

    @Column(name = "talk_type", nullable = false)
    private Integer talkType ;

    @Column(name = "taste_type", nullable = false)
    private Integer tasteType ;

    @Column(name = "system_type", nullable = false)
    private Integer systemType ;

    @Column(name = "time_type", nullable = false)
    private Integer timeType ;


    private void setFavorRule(String favorRule) {
        this.favorRule = favorRule;
    }

    private void setRpType(Integer rpType) {
        this.rpType = rpType;
    }

    private void setChatType(Integer chatType) {
        this.chatType = chatType;
    }

    private void setTalkType(Integer talkType) {
        this.talkType = talkType;
    }

    private void setTasteType(Integer tasteType) {
        this.tasteType = tasteType;
    }

    private void setSystemType(Integer systemType) {
        this.systemType = systemType;
    }

    private void setTimeType(Integer timeType) {
        this.timeType = timeType;
    }

    // Public update method
    public void updateDetails(MemberDetailRequestDto dto) {
        this.setFavorRule(dto.getFavorRule());
        this.setRpType(Integer.parseInt(dto.getRpType(), 2));
        this.setChatType(Integer.parseInt(dto.getChatType(), 2));
        this.setTalkType(Integer.parseInt(dto.getTalkType(), 2));
        this.setTasteType(Integer.parseInt(dto.getTasteType(), 2));
        this.setSystemType(Integer.parseInt(dto.getSystemType(), 2));
        this.setTimeType(Integer.parseInt(dto.getTimeType(), 2));
    }
}
