package org.infinity.sixtalebackend.domain.memberdetail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.sixtalebackend.domain.genre.repository.GenreRepository;
import org.infinity.sixtalebackend.domain.member.domain.Member;
import org.infinity.sixtalebackend.domain.memberdetail.domain.MemberDetail;
import org.infinity.sixtalebackend.domain.memberdetail.domain.MemberGenre;
import org.infinity.sixtalebackend.domain.memberdetail.domain.MemberGenreID;
import org.infinity.sixtalebackend.domain.memberdetail.dto.GenreDto;
import org.infinity.sixtalebackend.domain.memberdetail.dto.MemberDetailRequestDto;
import org.infinity.sixtalebackend.domain.memberdetail.dto.MemberDetailResponseDto;
import org.infinity.sixtalebackend.domain.memberdetail.repository.MemberDetailRepository;
import org.infinity.sixtalebackend.domain.member.repository.MemberRepository;
import org.infinity.sixtalebackend.domain.genre.domain.Genre;
import org.infinity.sixtalebackend.domain.memberdetail.repository.MemberGenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberDetailServiceImpl implements MemberDetailService{

    private final MemberRepository memberRepository;
    private final GenreRepository genreRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final MemberGenreRepository memberGenreRepository;

    /**
     * 회원 상세 정보 생성
     * @param memberDetailRequestDto
     * @param memberID
     */
    @Override
    @Transactional
    public void createMemberDetail(MemberDetailRequestDto memberDetailRequestDto, Long memberID) {
        Member member = findMember(memberID);

        // 멤버 상세 정보 저장
        MemberDetail memberDetail = MemberDetail.builder()
                .member(member)
                .favorRule(memberDetailRequestDto.getFavorRule())
                .rpType(Integer.parseInt(memberDetailRequestDto.getRpType(), 2))
                .chatType(Integer.parseInt(memberDetailRequestDto.getChatType(),2))
                .talkType(Integer.parseInt(memberDetailRequestDto.getTalkType(), 2))
                .tasteType(Integer.parseInt(memberDetailRequestDto.getTasteType(), 2))
                .systemType(Integer.parseInt(memberDetailRequestDto.getSystemType(), 2))
                .timeType(Integer.parseInt(memberDetailRequestDto.getTimeType(), 2))
                .build();

        memberDetailRepository.save(memberDetail);

        // 선호 장르 저장
        List<Long> genreList = memberDetailRequestDto.getGenreList();
        List<MemberGenre> memberGenres = new ArrayList<>();
        for(Long genreId:genreList){
            Genre genre = findGenre(genreId);
            System.out.println(genre.getName());
            // 중복 체크
            MemberGenreID memberGenreID = new MemberGenreID(memberID, genreId);
            if (memberGenreRepository.existsById(memberGenreID)) {
                log.info("MemberGenre already exists with memberID: {} and genreID: {}", memberID, genreId);
                continue; // Skip if already exists
            }

            MemberGenre memberGenre = MemberGenre.builder()
                    .id(memberGenreID)
                    .member(member)
                    .genre(genre)
                    .build();

            memberGenres.add(memberGenre);
        }

        memberGenreRepository.saveAll(memberGenres);
    }

    /**
     * 회원 상세 정보 수정
     * @param memberDetailRequestDto
     * @param memberID
     */
    @Override
    @Transactional
    public void updateMemberDetail(MemberDetailRequestDto memberDetailRequestDto, Long memberID) {
        Member member = findMember(memberID);

        // 멤버 상세 정보 수정
        MemberDetail memberDetail = findMemberDetail(memberID);
        memberDetail.updateDetails(memberDetailRequestDto);
        memberDetailRepository.save(memberDetail);

        // 기존 선호 장르 삭제
       memberGenreRepository.deleteAllByMember(member);

        // 새로운 선호 장르 저장
        List<Long> genreList = memberDetailRequestDto.getGenreList();
        List<MemberGenre> memberGenres = new ArrayList<>();
        for(Long genreId : genreList) {
            Genre genre = findGenre(genreId);
            MemberGenre memberGenre = MemberGenre.builder()
                    .id(new MemberGenreID(memberID, genreId))
                    .member(member)
                    .genre(genre)
                    .build();

            memberGenres.add(memberGenre);
        }

        memberGenreRepository.saveAll(memberGenres);
    }

    /**
     * 회원 상세 정보 조회
     * @param memberID
     * @return
     */
    @Override
    public MemberDetailResponseDto readMemberDetail(Long memberID) {
        // 회원 조회
        Member member =findMember(memberID);

        // 회원 상세 정보 조회
        MemberDetail memberDetail = findMemberDetail(memberID);
        if (memberDetail == null){
            return null;
        }
        // 장르 조회
        List<MemberGenre> memberGenres = memberGenreRepository.findMemberGenreByMember(member); // 특정 회원에 대한 장르만 조회하는 메서드 필요

        // DTO 생성
        return MemberDetailResponseDto.builder()
                .favorRule(memberDetail.getFavorRule())
                .rpType(Integer.toBinaryString(memberDetail.getRpType()))
                .chatType(Integer.toBinaryString(memberDetail.getChatType()))
                .talkType(Integer.toBinaryString(memberDetail.getTalkType()))
                .tasteType(Integer.toBinaryString(memberDetail.getTasteType()))
                .systemType(Integer.toBinaryString(memberDetail.getSystemType()))
                .timeType(Integer.toBinaryString(memberDetail.getTimeType()))
                .genreList(memberGenres.stream()
                        .map(memberGenre -> new GenreDto(memberGenre.getGenre().getId(), memberGenre.getGenre().getName())) // GenreDto 생성자 필요
                        .collect(Collectors.toList()))
                .build();
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    private MemberDetail findMemberDetail(Long id) {
        return null;
//        return memberDetailRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("회원 상세정보가 존재하지 않습니다."));
    }

    private Genre findGenre(Long id) {
        return genreRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("장르가 존재하지 않습니다."));
    }
}
