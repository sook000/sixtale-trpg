package org.infinity.sixtalebackend.domain.member.service;

import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.member.dto.MemberResponseDto;
import org.infinity.sixtalebackend.domain.scenario.dto.ScenarioListResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MemberService {

    public boolean isNicknameDuplicated(String nickname);
    MemberResponseDto createMemberInfo(Long id, String nickName, MultipartFile[] files) throws IOException;
    MemberResponseDto updateMemberInfo(Long id, String nickName, MultipartFile[] files) throws IOException;
    MemberResponseDto getMemberInfo(Long id) throws IOException;

    Member findByAccessToken(String token);

    ScenarioListResponseDto getScenarioLikeList(Long memberID, Pageable scenarioPageable);
}
