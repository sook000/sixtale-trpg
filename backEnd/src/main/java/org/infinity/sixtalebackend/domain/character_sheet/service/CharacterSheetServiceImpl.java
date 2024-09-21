package org.infinity.sixtalebackend.domain.character_sheet.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterAction;
import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterEquipment;
import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterSheet;
import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterStat;
import org.infinity.sixtalebackend.domain.character_sheet.dto.*;
import org.infinity.sixtalebackend.domain.character_sheet.repository.CharacterActionRepository;
import org.infinity.sixtalebackend.domain.character_sheet.repository.CharacterEquipmentRepository;
import org.infinity.sixtalebackend.domain.character_sheet.repository.CharacterSheetRepository;
import org.infinity.sixtalebackend.domain.character_sheet.repository.CharacterStatRepository;
import org.infinity.sixtalebackend.domain.character_sheet.util.CustomMultipartFile;
import org.infinity.sixtalebackend.domain.equipment.domain.EquipmentType;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.infinity.sixtalebackend.domain.room.repository.PlayMemberRepository;
import org.infinity.sixtalebackend.domain.rule.domain.*;
import org.infinity.sixtalebackend.domain.rule.repository.*;
import org.infinity.sixtalebackend.domain.scenario.domain.ScenarioEquipment;
import org.infinity.sixtalebackend.domain.scenario.repository.ScenarioEquipmentRepository;
import org.infinity.sixtalebackend.infra.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CharacterSheetServiceImpl implements CharacterSheetService{
    private final PlayMemberRepository playMemberRepository;
    private final CharacterSheetRepository characterSheetRepository;
    private final CharacterStatRepository characterStatRepository;
    private final CharacterActionRepository characterActionRepository;
    private final CharacterEquipmentRepository characterEquipmentRepository;
    private final JobRepository jobRepository;
    private final JobActionRepository jobActionRepository;
    private final ActionOptionRepository actionOptionRepository;
    private final BeliefRepository beliefRepository;
    private final RaceRepository raceRepository;
    private final StatRepository statRepository;
    private final ScenarioEquipmentRepository scenarioEquipmentRepository;
    private final JobRaceRepository jobRaceRepository;
    private final JobBeliefRepository jobBeliefRepository;
    private final S3Service s3Service;

    /**
     * 캐릭터 시트 작성
     * 무게 합 프론트에서 보내줄 것임
     */
    @Override
    @Transactional
    public void createCharacterSheet(Long roomID, CharacterSheetRequest characterSheetRequest, Long memberID) throws IOException {
        PlayMember playMember = playMemberRepository.findByMemberIdAndRoomId(memberID, roomID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PlayMember or Room ID"));

        Job job = jobRepository.findById(characterSheetRequest.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Job ID"));

        Belief belief = beliefRepository.findById(characterSheetRequest.getBeliefId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Belief ID"));

        Race race = raceRepository.findById(characterSheetRequest.getRaceId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Race ID"));

//        URL url = new URL(characterSheetRequest.getImageURL());
//        List<String> listUrl = null;
//        // image url의 input stream, byte 배열로 저장할 output stream 열기
//        try(InputStream inputStream = url.openStream();
//            ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
//            // ImageIO.read()로 image url의 이미지 데이터 읽어오기
//            BufferedImage urlImage = ImageIO.read(inputStream);
//            // 메모리에 로드 된 이미지 데이터를 output stream에 jpg 확장자로 저장
//            ImageIO.write(urlImage, "jpg", bos);
//            // byte 배열로 변환
//            byte[]  byteArray = bos.toByteArray();
//            MultipartFile multipartFile = new CustomMultipartFile(byteArray, characterSheetRequest.getImageURL());
//            MultipartFile[] files = new MultipartFile[1];
//        files[0] = multipartFile;
//        log.info("helllo2");
//        System.out.println(files[0]);
//        System.out.println(files.toString());
//        listUrl = s3Service.upload(files, "room" + "/" + roomID + "/" + "character_img" + "/" + playMember.getId());
//        }
        String s3Url = null;
        try {
            s3Url = s3Service.uploadImageFromUrl(characterSheetRequest.getImageURL());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }


        // 캐릭터 시트 저장
        CharacterSheet characterSheet = CharacterSheet.builder()
                .playMember(playMember)
                .job(job)
                .belief(belief)
                .race(race)
                .name(characterSheetRequest.getName())
                .appearance(characterSheetRequest.getAppearance())
                .background(characterSheetRequest.getBackground())
                .currentWeight(characterSheetRequest.getCurrentWeight())
                .currentHp(characterSheetRequest.getCurrentHp())
                .currentMoney(characterSheetRequest.getCurrentMoney())
                .limitWeight(characterSheetRequest.getLimitWeight())
                .limitHp(characterSheetRequest.getLimitHp())
                .glove(characterSheetRequest.getGlove())
                .inspirationScore(characterSheetRequest.getInspirationScore())
                .level(characterSheetRequest.getLevel())
                .exp(characterSheetRequest.getExp())
                .imageURL(s3Url)
                .build();
        characterSheetRepository.save(characterSheet);

        //캐릭터 스탯 저장
        List<CharacterStat> characterStats = characterSheetRequest.getStat().stream()
                .map(statRequest -> CharacterStat.builder()
                        .playMember(playMember)
                        .characterSheet(characterSheet)
                        .stat(statRepository.findById(statRequest.getStatID())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Stat ID")))
                        .statValue(statRequest.getStatValue())
                        .statWeight(statRequest.getStatWeight())
                        .build())
                .collect(Collectors.toList());
        characterStatRepository.saveAll(characterStats);

        log.info("스탯 저장");

        // 캐릭터 액션 저장
        List<CharacterAction> characterActions = characterSheetRequest.getCharacterAction().stream()
                .map(actionRequest -> CharacterAction.builder()
                        .characterSheet(characterSheet)
                        .playMember(playMember)
                        .jobAction(jobActionRepository.findById(actionRequest.getActionID())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Action ID")))
                        .actionOption(actionRequest.getActionOptionId() != null ?
                                actionOptionRepository.findById(actionRequest.getActionOptionId()).orElse(null) :
                                null)
                        .build())
                .collect(Collectors.toList());
        characterActionRepository.saveAll(characterActions);
        log.info("액션 저장");

        // 캐릭터 장비 저장
        List<CharacterEquipment> characterEquipments = characterSheetRequest.getCharacterEquipment().stream()
                .map(equipmentRequest -> CharacterEquipment.builder()
                        .playMember(playMember)
                        .characterSheet(characterSheet) // PlayMember 대신 CharacterSheet 사용
                        .equipment(scenarioEquipmentRepository.findById(equipmentRequest.getEquipmentId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Equipment ID")))
                        .currentCount(equipmentRequest.getCurrentCount())
                        .weight(equipmentRequest.getWeight())
                        .build())
                .collect(Collectors.toList());

        characterEquipmentRepository.saveAll(characterEquipments);
        log.info("장비 저장");
    }
    /*
    @Override
    @Transactional
    public void createCharacterSheet(Long roomID, CharacterSheetRequest characterSheetRequest, Long memberID, MultipartFile[] files) throws IOException {
        PlayMember playMember = playMemberRepository.findByMemberIdAndRoomId(memberID, roomID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PlayMember or Room ID"));

        Job job = jobRepository.findById(characterSheetRequest.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Job ID"));

        Belief belief = beliefRepository.findById(characterSheetRequest.getBeliefId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Belief ID"));

        Race race = raceRepository.findById(characterSheetRequest.getRaceId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Race ID"));

        // 캐릭터 이미지 저장
        List<String> listUrl = null;
        if (files != null && files.length > 0) {
            listUrl = s3Service.upload(files, "room" + "/" + roomID + "/" + "character_img" + "/" + playMember.getId());
        }

        // 캐릭터 시트 저장
        CharacterSheet characterSheet = CharacterSheet.builder()
                .playMember(playMember)
                .job(job)
                .belief(belief)
                .race(race)
                .name(characterSheetRequest.getName())
                .appearance(characterSheetRequest.getAppearance())
                .background(characterSheetRequest.getBackground())
                .currentWeight(characterSheetRequest.getCurrentWeight())
                .currentHp(characterSheetRequest.getCurrentHp())
                .currentMoney(characterSheetRequest.getCurrentMoney())
                .limitWeight(characterSheetRequest.getLimitWeight())
                .limitHp(characterSheetRequest.getLimitHp())
                .glove(characterSheetRequest.getGlove())
                .inspirationScore(characterSheetRequest.getInspirationScore())
                .level(characterSheetRequest.getLevel())
                .exp(characterSheetRequest.getExp())
                .imageURL(listUrl.get(0))
                .build();
        characterSheetRepository.save(characterSheet);

        //캐릭터 스탯 저장
        List<CharacterStat> characterStats = characterSheetRequest.getStat().stream()
                .map(statRequest -> CharacterStat.builder()
                        .playMember(playMember)
                        .characterSheet(characterSheet)
                        .stat(statRepository.findById(statRequest.getStatID())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Stat ID")))
                        .statValue(statRequest.getStatValue())
                        .statWeight(statRequest.getStatWeight())
                        .build())
                .collect(Collectors.toList());
        characterStatRepository.saveAll(characterStats);

        log.info("스탯 저장");

        // 캐릭터 액션 저장
        List<CharacterAction> characterActions = characterSheetRequest.getCharacterAction().stream()
                .map(actionRequest -> CharacterAction.builder()
                        .characterSheet(characterSheet)
                        .playMember(playMember)
                        .jobAction(jobActionRepository.findById(actionRequest.getActionID())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Action ID")))
                        .actionOption(actionRequest.getActionOptionId() != null ?
                                actionOptionRepository.findById(actionRequest.getActionOptionId()).orElse(null) :
                                null)
                        .build())
                .collect(Collectors.toList());
        characterActionRepository.saveAll(characterActions);
        log.info("액션 저장");

        // 캐릭터 장비 저장
        List<CharacterEquipment> characterEquipments = characterSheetRequest.getCharacterEquipment().stream()
                .map(equipmentRequest -> CharacterEquipment.builder()
                        .playMember(playMember)
                        .characterSheet(characterSheet) // PlayMember 대신 CharacterSheet 사용
                        .equipment(scenarioEquipmentRepository.findById(equipmentRequest.getEquipmentId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Equipment ID")))
                        .currentCount(equipmentRequest.getCurrentCount())
                        .weight(equipmentRequest.getWeight())
                        .build())
                .collect(Collectors.toList());

        characterEquipmentRepository.saveAll(characterEquipments);
        log.info("장비 저장");
    }
     */

    /**
     * 캐릭터 시트 수정(플레이 전)
     */
    @Override
    @Transactional
    public void updateCharacterSheet(Long roomID, Long playMemberID, CharacterSheetUpdateRequest characterSheetUpdateRequest, MultipartFile[] files) throws IOException{
        PlayMember playMember = playMemberRepository.findById(playMemberID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PlayMember"));

        CharacterSheet characterSheet = characterSheetRepository.findByPlayMemberId(playMemberID)
                .orElseThrow(() -> new IllegalArgumentException("Character Sheet not found"));

        Job job = jobRepository.findById(characterSheetUpdateRequest.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Job ID"));

        Belief belief = beliefRepository.findById(characterSheetUpdateRequest.getBeliefId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Belief ID"));

        Race race = raceRepository.findById(characterSheetUpdateRequest.getRaceId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Race ID"));

        // 기존 캐릭터 이미지 삭제
        if (characterSheet.getImageURL() != null) {
            String oldImageName = characterSheet.getImageURL().substring(characterSheet.getImageURL().lastIndexOf("/")+1);
            System.out.println(oldImageName);
            s3Service.delete("room" + "/" + roomID + "/" + "character_img" + "/" + playMember.getId() + "/" + oldImageName);
        }

        // 캐릭터 이미지 저장
        List<String> listUrl = null;
        if (files != null && files.length > 0) {
            listUrl = s3Service.upload(files, "room" + "/" + roomID + "/" + "character_img" + "/" + playMember.getId());
        }

        // 캐릭터 시트 업데이트
        characterSheet.setPlayMember(playMember);
        characterSheet.setJob(job);
        characterSheet.setBelief(belief);
        characterSheet.setRace(race);
        characterSheet.setName(characterSheetUpdateRequest.getName());
        characterSheet.setAppearance(characterSheetUpdateRequest.getAppearance());
        characterSheet.setBackground(characterSheetUpdateRequest.getBackground());
        characterSheet.setCurrentWeight(characterSheetUpdateRequest.getCurrentWeight());
        characterSheet.setCurrentHp(characterSheetUpdateRequest.getCurrentHp());
        characterSheet.setCurrentMoney(characterSheetUpdateRequest.getCurrentMoney());
        characterSheet.setLimitWeight(characterSheetUpdateRequest.getLimitWeight());
        characterSheet.setLimitHp(characterSheetUpdateRequest.getLimitHp());
        characterSheet.setGlove(characterSheetUpdateRequest.getGlove());
        characterSheet.setInspirationScore(characterSheetUpdateRequest.getInspirationScore());
        characterSheet.setLevel(characterSheetUpdateRequest.getLevel());
        characterSheet.setExp(characterSheetUpdateRequest.getExp());
        characterSheet.setImageURL(listUrl.get(0));
        characterSheetRepository.save(characterSheet);

        // 캐릭터 스탯 업데이트
        Map<Long, CharacterStatRequest> statRequestMap = characterSheetUpdateRequest.getStat().stream()
                .collect(Collectors.toMap(CharacterStatRequest::getStatID, Function.identity()));

        characterSheet.getCharacterStats().forEach(stat -> {
            CharacterStatRequest statRequest = statRequestMap.get(stat.getStat().getId());
            if (statRequest != null) {
                stat.setStatValue(statRequest.getStatValue());
                stat.setStatWeight(statRequest.getStatWeight());
            }
        });
    }

    /**
     * 캐릭터 시트 조회
     */
    @Override
    @Transactional(readOnly = true)
    public CharacterSheetResponse getCharacterSheet(Long roomID, Long playMemberID) {
        CharacterSheet characterSheet = characterSheetRepository.findByPlayMemberIdWithFetch(playMemberID)
                .orElseThrow(() -> new IllegalArgumentException("Character Sheet not found"));

        Job job = characterSheet.getJob();
        Race race = characterSheet.getRace();
        Belief belief = characterSheet.getBelief();

        String raceDescription = job.getJobRaces().stream()
                .filter(jr -> jr.getRace().equals(race))
                .map(JobRace::getDescription)
                .findFirst()
                .orElse("");
        String beliefDescription = job.getJobBeliefs().stream()
                .filter(jb -> jb.getBelief().equals(belief))
                .map(JobBelief::getDescription)
                .findFirst()
                .orElse("");

        // stat, actions, equipments 뽑아내기
        Set<CharacterStat> stats = characterSheet.getCharacterStats();
        Set<CharacterAction> actions = characterSheet.getCharacterActions();
        Set<CharacterEquipment> equipments = characterSheet.getCharacterEquipments();

        //Dto로 변환
        List<CharacterStatResponse> statResponses = stats.stream()
                .map(stat -> CharacterStatResponse.builder()
                        .statID(stat.getStat().getId())
                        .statValue(stat.getStatValue())
                        .statWeight(stat.getStatWeight())
                        .build())
                .collect(Collectors.toList());

        List<CharacterActionResponse> actionResponses = actions.stream()
                .map(action -> {
                    JobAction jobAction = action.getJobAction();
                    ActionOption actionOption = action.getActionOption();
                    return CharacterActionResponse.builder()
                            .id(action.getId())
                            .actionID(jobAction.getId())
                            .name(jobAction.getName())
                            .isCore(jobAction.getIsCore())
                            .description(jobAction.getDescription())
                            .isDice(jobAction.getIsDice())
                            .diceType(jobAction.getDiceType())
                            .diceCount(jobAction.getDiceCount())
                            .level(jobAction.getLevel())
                            .actionOption(actionOption != null ?
                                    Collections.singletonList(ActionOptionResponse.builder()
                                            .id(actionOption.getId())
                                            .content(actionOption.getContent())
                                            .build())
                                    : Collections.emptyList())
                            .build();
                })
                .collect(Collectors.toList());

        List<CharacterEquipmentResponse> equipmentResponses = equipments.stream()
                .map(equipment -> {
                    ScenarioEquipment scenarioEquipment = equipment.getEquipment();
                    EquipmentType equipmentType = scenarioEquipment.getEquipmentType();
                    return CharacterEquipmentResponse.builder()
                            .id(equipment.getId())
                            .equipmentID(scenarioEquipment.getId())
                            .name(scenarioEquipment.getName())
                            .description(scenarioEquipment.getDescription())
                            .typeID(equipmentType.getId())
                            .typeName(equipmentType.getName())
                            .weight(equipment.getWeight())
                            .currentCount(equipment.getCurrentCount())
                            .imageURL(scenarioEquipment.getImageURL())
                            .build();
                })
                .collect(Collectors.toList());

        return CharacterSheetResponse.builder()
                .jobId(job.getId())
                .jobName(job.getName())
                .jobDiceType(job.getDiceType())
                .raceId(race.getId())
                .raceName(race.getName())
                .raceDescription(raceDescription) // Fetch from jobRace field
                .beliefId(belief.getId())
                .beliefName(belief.getName())
                .beliefDescription(beliefDescription) // Fetch from jobBelief field
                .name(characterSheet.getName())
                .appearance(characterSheet.getAppearance())
                .background(characterSheet.getBackground())
                .stat(statResponses)
                .characterAction(actionResponses)
                .characterEquipment(equipmentResponses)
                .currentWeight(characterSheet.getCurrentWeight())
                .currentHp(characterSheet.getCurrentHp())
                .currentMoney(characterSheet.getCurrentMoney())
                .limitWeight(characterSheet.getLimitWeight())
                .limitHp(characterSheet.getLimitHp())
                .glove(characterSheet.getGlove())
                .inspirationScore(characterSheet.getInspirationScore())
                .level(characterSheet.getLevel())
                .exp(characterSheet.getExp())
                .imageURL(characterSheet.getImageURL())
                .build();
    }

    /**
     * 캐릭터 시트 작성 취소
     */
    @Override
    @Transactional
    public void deleteCharacterSheet(Long roomID, Long memberID) {
        PlayMember playMember = playMemberRepository.findByMemberIdAndRoomId(memberID, roomID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PlayMember or Room ID"));

        CharacterSheet characterSheet = characterSheetRepository.findByPlayMember(playMember)
                .orElseThrow(() -> new IllegalArgumentException("Character Sheet not found"));

        // 기존 캐릭터 이미지 삭제
        if (characterSheet.getImageURL() != null) {
            String oldImageName = characterSheet.getImageURL().substring(characterSheet.getImageURL().lastIndexOf("/")+1);
            System.out.println(oldImageName);
            s3Service.delete("room" + "/" + roomID + "/" + "character_img" + "/" + playMember.getId() + "/" + oldImageName);
        }

        // CharacterStat 삭제하면 캐스케이드 옵션 사용에 의해 전부 삭제
        characterSheetRepository.delete(characterSheet);
        log.info("캐릭터 시트 및 관련 데이터 삭제 완료");
    }

    /**
     * 골드 수정
     */
    @Override
    @Transactional
    public Map<String, String> updateCharacterGold(Long roomID, Long playMemberID, CharacterGoldUpdateRequest characterGoldUpdateRequest) {
        PlayMember playMember = playMemberRepository.findByIdAndRoomId(playMemberID, roomID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PlayMember or Room ID"));
        CharacterSheet characterSheet = characterSheetRepository.findByPlayMember(playMember)
                .orElseThrow(() -> new IllegalArgumentException("Character Sheet not found"));

        int currentGold = characterSheet.getCurrentMoney();
        int updateGold = characterGoldUpdateRequest.getCurrentMoney();
        characterSheet.setCurrentMoney(updateGold);
        characterSheetRepository.save(characterSheet);

        // 응답을 위한 map 생성
        Map<String, String> result = new HashMap<>();
        result.put("roomID", roomID.toString());
        result.put("sheetID", characterSheet.getId().toString());
        result.put("currentGold", String.valueOf(currentGold));
        result.put("updateGold", String.valueOf(updateGold));

        return result;
    }

    /**
     * 캐릭터 시트 수정(플레이 중)
     */
    @Override
    @Transactional
    public void updateCharacterSheetInPlaying(Long roomID, Long playMemberID, CharacterSheetUpdateRequest characterSheetUpdateRequest) {
        PlayMember playMember = playMemberRepository.findById(playMemberID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PlayMember"));

        CharacterSheet characterSheet = characterSheetRepository.findByPlayMemberId(playMemberID)
                .orElseThrow(() -> new IllegalArgumentException("Character Sheet not found"));

        Job job = jobRepository.findById(characterSheetUpdateRequest.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Job ID"));

        Belief belief = beliefRepository.findById(characterSheetUpdateRequest.getBeliefId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Belief ID"));

        Race race = raceRepository.findById(characterSheetUpdateRequest.getRaceId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Race ID"));

        // 캐릭터 시트 업데이트
        characterSheet.setPlayMember(playMember);
        characterSheet.setJob(job);
        characterSheet.setBelief(belief);
        characterSheet.setRace(race);
        characterSheet.setName(characterSheetUpdateRequest.getName());
        characterSheet.setAppearance(characterSheetUpdateRequest.getAppearance());
        characterSheet.setBackground(characterSheetUpdateRequest.getBackground());
        characterSheet.setCurrentWeight(characterSheetUpdateRequest.getCurrentWeight());
        characterSheet.setCurrentHp(characterSheetUpdateRequest.getCurrentHp());
        characterSheet.setCurrentMoney(characterSheetUpdateRequest.getCurrentMoney());
        characterSheet.setLimitWeight(characterSheetUpdateRequest.getLimitWeight());
        characterSheet.setLimitHp(characterSheetUpdateRequest.getLimitHp());
        characterSheet.setGlove(characterSheetUpdateRequest.getGlove());
        characterSheet.setInspirationScore(characterSheetUpdateRequest.getInspirationScore());
        characterSheet.setLevel(characterSheetUpdateRequest.getLevel());
        characterSheet.setExp(characterSheetUpdateRequest.getExp());
        characterSheet.setImageURL(characterSheet.getImageURL());
        characterSheetRepository.save(characterSheet);

        // 캐릭터 스탯 업데이트
        Map<Long, CharacterStatRequest> statRequestMap = characterSheetUpdateRequest.getStat().stream()
                .collect(Collectors.toMap(CharacterStatRequest::getStatID, Function.identity()));

        characterSheet.getCharacterStats().forEach(stat -> {
            CharacterStatRequest statRequest = statRequestMap.get(stat.getStat().getId());
            if (statRequest != null) {
                stat.setStatValue(statRequest.getStatValue());
                stat.setStatWeight(statRequest.getStatWeight());
            }
        });
        characterStatRepository.saveAll(characterSheet.getCharacterStats());

    }
}
