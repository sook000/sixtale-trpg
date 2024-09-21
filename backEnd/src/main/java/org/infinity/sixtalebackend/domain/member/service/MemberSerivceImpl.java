package org.infinity.sixtalebackend.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.member.dto.MemberResponseDto;
import org.infinity.sixtalebackend.domain.member.repository.MemberRepository;
import org.infinity.sixtalebackend.domain.memberdetail.dto.GenreDto;
import org.infinity.sixtalebackend.domain.scenario.domain.Scenario;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioGenre;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioLike;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioLikeID;
import org.infinity.sixtalebackend.domain.scenario.dto.ScenarioListResponseDto;
import org.infinity.sixtalebackend.domain.scenario.dto.ScenarioResponseDto;
import org.infinity.sixtalebackend.domain.scenario.repository.ScenarioGenreRepository;
import org.infinity.sixtalebackend.domain.scenario.repository.ScenarioLikeRepository;
import org.infinity.sixtalebackend.domain.scenario.repository.ScenarioRepository;
import org.infinity.sixtalebackend.infra.s3.S3Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberSerivceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final ScenarioRepository scenarioRepository;
    private final ScenarioGenreRepository scenarioGenreRepository;
    private final ScenarioLikeRepository scenarioLikeRepository;

    public boolean isNicknameDuplicated(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    /**
     * 회원 정보 생성
     * @param id
     * @param nickName
     * @param files
     */
    @Override
    public MemberResponseDto createMemberInfo(Long id, String nickName, MultipartFile[] files) throws IOException {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + id));

        member.setNickName(nickName); // 닉네임만 수정

        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .nickName(nickName)
                .build();

        // 프로필 이미지 저장
        if (files != null && files.length > 0) {
            List<String> listUrl = s3Service.upload(files, id + "/" + "profile");
            member.setImageURL(listUrl.get(0));
            memberResponseDto.setImageURL(listUrl.get(0));
        }

        memberRepository.save(member); // 변경 사항 저장

        return memberResponseDto;
    }

    /**
     * 회원 정보 수정
     * @param id
     * @param nickName
     * @param files
     * @return
     * @throws IOException
     */
    @Override
    public MemberResponseDto updateMemberInfo(Long id, String nickName, MultipartFile[] files) throws IOException {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + id));

        // 응답 DTO 생성
        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .nickName(member.getNickname())
                .imageURL(member.getImageURL())
                .build();

        // 닉네임 수정
        if(nickName!=null && !nickName.trim().isEmpty()) {
            member.setNickName(nickName);
            memberResponseDto.setNickName(nickName);
        }

        // 기존 프로필 이미지 삭제
        if (member.getImageURL() != null) {
            String oldImageName = member.getImageURL().substring(member.getImageURL().lastIndexOf("/")+1);
            System.out.println(oldImageName);
            s3Service.delete(id + "/"+"profile/" + oldImageName);
        }

        // 프로필 이미지 저장
        if (files != null && files.length > 0) {
            List<String> listUrl = s3Service.upload(files, id + "/" + "profile");
            member.setImageURL(listUrl.get(0));
            memberResponseDto.setImageURL(listUrl.get(0));
        }

        memberRepository.save(member); // 변경 사항 저장

        return memberResponseDto;
    }


    /**
     * 회원 정보 조회
     * @param id
     * @return
     * @throws IOException
     */
    @Override
    public MemberResponseDto getMemberInfo(Long id) throws IOException {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + id));

        return MemberResponseDto.builder()
                .id(id)
                .nickName(member.getNickname())
                .imageURL(member.getImageURL())
                .createdAt(member.getCreatedAt())
                .build();
    }

    @Override
    public Member findByAccessToken(String token) {
        Member member = memberRepository.findByAccessToken(token);
        return member;
    }

    /**
     * 좋아요 한 시나리오 목록 조회
     * @param memberID
     * @param scenarioPageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public ScenarioListResponseDto getScenarioLikeList(Long memberID, Pageable scenarioPageable) {
        Member member = findMember(memberID);

        log.info("like memberID = {}", member.getId());

        // 페이지네이션과 정렬을 포함한 Pageable 객체 생성
        // 좋아요 한 시나리오
        Page<Scenario> scenarioPage = scenarioRepository.findLikedScenariosByMember(member, scenarioPageable);

        // Entity - Dto 변환
        List<ScenarioResponseDto> scenarioResponseDtoList = scenarioPage.getContent().stream()
                .map(scenario -> {
                    List<ScenarioGenre> scenarioGenres = scenarioGenreRepository.findScenarioGenreByScenario(scenario);
//                    Boolean isLiked = scenarioLikeRepository.existsByScenarioAndMember(scenario, member);
                    return convertToDto(scenario, scenarioGenres, true);
                }).toList();

        return ScenarioListResponseDto.builder()
                .scenarioList(scenarioResponseDtoList)
                .totalPages(scenarioPage.getTotalPages())
                .totalElements(scenarioPage.getTotalElements())
                .build();
    }

    private ScenarioResponseDto convertToDto(Scenario scenario, List<ScenarioGenre> scenarioGenres, Boolean isLiked) {
        return ScenarioResponseDto.builder()
                .id(scenario.getId())
                .title(scenario.getTitle())
                .writerID(scenario.getWriter().getId())
                .nickName(scenario.getWriter().getNickname())
                .summary(scenario.getSummary())
                .description(scenario.getDescription())
                .likes(scenario.getLikes())
                .optimalCount(scenario.getOptimalCount())
                .isPublic(scenario.getIsPublic())
                .isOpen(scenario.getIsOpen())
                .ruleID(scenario.getRule().getId())
                .ruleTitle(scenario.getRule().getTitle())
                .updatedAt(scenario.getUpdatedAt())
                .imageURL(scenario.getImageURL())
                .gifURL(scenario.getGifURL())
                .isLiked(isLiked)
                .genreList(scenarioGenres.stream()
                        .map(scenarioGenre -> new GenreDto(scenarioGenre.getGenre().getId(), scenarioGenre.getGenre().getName())) // GenreDto 생성자 필요
                        .collect(Collectors.toList()))
                .build();
    }

    private Member findMember(Long id) {
        return memberRepository.getReferenceById(id);
    }

}
