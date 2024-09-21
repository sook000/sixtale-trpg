package org.infinity.sixtalebackend.domain.memberdetail.service;

import org.infinity.sixtalebackend.domain.memberdetail.dto.MemberDetailRequestDto;
import org.infinity.sixtalebackend.domain.memberdetail.dto.MemberDetailResponseDto;
import org.springframework.stereotype.Service;

public interface MemberDetailService {
    void createMemberDetail(MemberDetailRequestDto memberDetailRequestDto, Long memberID);

    void updateMemberDetail(MemberDetailRequestDto memberDetailRequestDto, Long memberID);

    MemberDetailResponseDto readMemberDetail(Long memberID);
}
