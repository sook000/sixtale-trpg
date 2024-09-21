package org.infinity.sixtalebackend.domain.scenario.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.sixtalebackend.domain.member.domain.Member;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioServiceImpl implements ScenarioService{

    private final ScenarioRepository scenarioRepository;
    private final MemberRepository memberRepository;
    private final ScenarioGenreRepository scenarioGenreRepository;
    private final ScenarioLikeRepository scenarioLikeRepository;

    /**
     * 시나리오 목록 조회
     * @param memberID
     * @param genre
     * @param title
     * @return
     */
    @Override
    public ScenarioListResponseDto getScenarioList(Long memberID, List<Long> genre, String title, int page, int size, String sort, String order) {
        // 회원 처리
        Member member = (memberID == null) ? null : findMember(memberID);

        // Sort 및 Pageable 객체 생성
        Sort pageSort = Sort.by(Sort.Direction.fromString(order), sort);
        Pageable pageable = PageRequest.of(page, size, pageSort);

        // 페이지네이션과 정렬을 포함한 Pageable 객체 생성
        Page<Scenario> scenarioPage;
        if (genre.isEmpty()) { // 장르 ID가 없으면 전체 장르 조회
            scenarioPage = scenarioRepository.findByTitleContaining(title, pageable);
        } else {
            scenarioPage = scenarioRepository.findByGenreIdAndTitleContaining(genre, title, pageable);
            System.out.println(scenarioPage.stream().toList());
        }

        // Entity - Dto 수동 변환 방법
        // 장르 리스트 및 좋아요 여부를 포함하여 DTO로 변환
        List<ScenarioResponseDto> scenarioResponseDtoList = scenarioPage.getContent().stream()
                .map(s -> {
                    List<ScenarioGenre> scenarioGenres = scenarioGenreRepository.findScenarioGenreByScenario(s);
                    Boolean isLiked = (memberID == null) ? null : scenarioLikeRepository.existsByScenarioAndMember(s, member);
                    return convertToDto(s, scenarioGenres, isLiked);
                }).toList();

        return ScenarioListResponseDto.builder()
                .scenarioList(scenarioResponseDtoList)
                .totalPages(scenarioPage.getTotalPages())
                .totalElements(scenarioPage.getTotalElements()).build();
    }

    /**
     * 시나리오 상세 정보 조회
     * @param scenarioID
     * @param memberID
     * @return
     */
    @Override
    public ScenarioResponseDto getScenarioInfo(Long scenarioID, Long memberID) {
        // 회원 처리
        Member member = (memberID == null) ? null : findMember(memberID);

        // 시나리오 조회
        Scenario scenario = scenarioRepository.getReferenceById(scenarioID);
        List<ScenarioGenre> scenarioGenres = scenarioGenreRepository.findScenarioGenreByScenario(scenario);
        Boolean isLiked = (memberID == null) ? null : scenarioLikeRepository.existsByScenarioAndMember(scenario, member);
        return convertToDto(scenario, scenarioGenres, isLiked);
    }

    private ScenarioResponseDto convertToDto(Scenario scenario,List<ScenarioGenre> scenarioGenres,Boolean isLiked) {
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
                .isLiked(isLiked) // 사용자가 로그인하지 않거나 좋아요를 누르지 않은 경우 null
                .genreList(scenarioGenres.stream()
                        .map(scenarioGenre -> new GenreDto(scenarioGenre.getGenre().getId(), scenarioGenre.getGenre().getName())) // GenreDto 생성자 필요
                        .collect(Collectors.toList()))
                .build();
    }
    private Member findMember(Long id) {
        return memberRepository.getReferenceById(id);
    }

    /**
     * 시나리오 좋아요 하기
     */
    @Transactional
    public boolean likeScenario(Long scenarioID, Long memberID) {
        Scenario scenario = scenarioRepository.findById(scenarioID)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id " + scenarioID));
        Member member = memberRepository.findById(memberID)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id " + memberID));

        // 이미 좋아요가 되어있는지 확인
        if (scenarioLikeRepository.existsByScenarioAndMember(scenario, member)) {
            return false;
        }

        // 좋아요 추가
        ScenarioLike scenarioLike = ScenarioLike.builder()
                .id(new ScenarioLikeID(scenarioID, memberID))
                .scenario(scenario)
                .member(member)
                .build();

        scenarioLikeRepository.save(scenarioLike);

        // 좋아요수 증가
        scenario.incrementLikes();

        return true;
    }

    /**
     * 시나리오 좋아요 취소
     */
    @Transactional
    public boolean unlikeScenario(Long scenarioID, Long memberID) {
        Scenario scenario = scenarioRepository.findById(scenarioID)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id " + scenarioID));
        Member member = memberRepository.findById(memberID)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id " + memberID));

        // 이미 좋아요 취소가 되어있는지 확인
        if (!scenarioLikeRepository.existsByScenarioAndMember(scenario, member)) {
            return false;
        }

        scenarioLikeRepository.deleteByScenarioAndMember(scenario, member);

        // 좋아요수 감소
        scenario.decrementLikes();

        return true;
    }
}
