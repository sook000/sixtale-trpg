package org.infinity.sixtalebackend.domain.character_sheet.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterAction;
import org.infinity.sixtalebackend.domain.character_sheet.domain.CharacterSheet;
import org.infinity.sixtalebackend.domain.character_sheet.dto.ActionOptionResponse;
import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterActionListResponse;
import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterActionRequest;
import org.infinity.sixtalebackend.domain.character_sheet.dto.CharacterActionResponse;
import org.infinity.sixtalebackend.domain.character_sheet.repository.CharacterActionRepository;
import org.infinity.sixtalebackend.domain.character_sheet.repository.CharacterSheetRepository;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.infinity.sixtalebackend.domain.room.repository.PlayMemberRepository;
import org.infinity.sixtalebackend.domain.rule.domain.ActionOption;
import org.infinity.sixtalebackend.domain.rule.domain.JobAction;
import org.infinity.sixtalebackend.domain.rule.repository.ActionOptionRepository;
import org.infinity.sixtalebackend.domain.rule.repository.JobActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CharacterActionServiceImpl implements CharacterActionService{
    private final PlayMemberRepository playMemberRepository;
    private final CharacterSheetRepository characterSheetRepository;
    private final CharacterActionRepository characterActionRepository;
    private final JobActionRepository jobActionRepository;
    private final ActionOptionRepository actionOptionRepository;

    /**
     * 캐릭터 액션 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public CharacterActionListResponse getCharacterActions(Long roomID, Long playMemberID) {
        // playMember와 CharacterSheet 조회
        PlayMember playMember = playMemberRepository.findByIdAndRoomId(playMemberID, roomID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PlayMember or Room ID"));
        CharacterSheet characterSheet = characterSheetRepository.findByPlayMemberWithActions(playMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("Character Sheet not found"));

        // 현재 캐릭터 시트에 들어있는 액션 id 조회
        Set<Long> existingActionIds = characterSheet.getCharacterActions().stream()
                .map(characterAction -> characterAction.getJobAction().getId())
                .collect(Collectors.toSet());

        // 현재 레벨과 같거나 작은 JobActions 조회
        List<JobAction> jobActions = jobActionRepository.findByJob_IdAndLevelLessThanEqual(
                characterSheet.getJob().getId(), characterSheet.getLevel());

        // coreActions와 AdvancedActions로 나누기
        List<CharacterActionResponse> coreActions = jobActions.stream()
                .filter(jobAction -> jobAction.getIsCore())
                .filter(jobAction -> !existingActionIds.contains(jobAction.getId()))// 이미 추가된 액션은 제외
                .map(this::mapToCharacterActionResponse)
                .collect(Collectors.toList());

        List<CharacterActionResponse> advancedActions = jobActions.stream()
                .filter(jobAction -> !jobAction.getIsCore()) // Advanced Actions
                .filter(jobAction -> !existingActionIds.contains(jobAction.getId())) // 이미 추가된 액션 제외
                .map(this::mapToCharacterActionResponse)
                .collect(Collectors.toList());

        return CharacterActionListResponse.builder()
                .coreActions(coreActions)
                .advancedActions(advancedActions)
                .build();
    }

    // CharacteAction 엔티티를 CharacterActionResponse로 변환
    private CharacterActionResponse mapToCharacterActionResponse(JobAction jobAction) {
        List<ActionOptionResponse> actionOptionResponses = jobAction.getActionOptions().stream()
                .map(this::mapToActionOptionResponse)
                .collect(Collectors.toList());

        return CharacterActionResponse.builder()
                .id(jobAction.getId())
                .actionID(jobAction.getId())
                .name(jobAction.getName())
                .isCore(jobAction.getIsCore())
                .description(jobAction.getDescription())
                .isDice(jobAction.getIsDice())
                .diceType(jobAction.getDiceType() != null ? jobAction.getDiceType() : null)
                .diceCount(jobAction.getDiceCount())
                .level(jobAction.getLevel())
                .actionOption(actionOptionResponses)
                .build();
    }

    // ActionOption 엔티티를 ActionOptionResponse로 변환
    private ActionOptionResponse mapToActionOptionResponse(ActionOption option) {
        return ActionOptionResponse.builder()
                .id(option.getId())
                .content(option.getContent())
                .build();
    }

    /**
     * 캐릭터 액션 추가
     */
    @Override
    @Transactional
    public void addCharacterAction(Long roomID, Long playMemberID, CharacterActionRequest actionRequest) {
        // PlayMember와 CharacterSheet 조회
        PlayMember playMember = playMemberRepository.findByIdAndRoomId(playMemberID, roomID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PlayMember or Room ID"));

        CharacterSheet characterSheet = characterSheetRepository.findByPlayMember(playMember)
                .orElseThrow(() -> new IllegalArgumentException("Character Sheet not found"));

        // JobAction 및 ActionOption 조회
        JobAction jobAction = jobActionRepository.findById(actionRequest.getActionID())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Action ID: " + actionRequest.getActionID()));

        // 이미 추가된 액션인지 확인
        boolean actionExists = characterActionRepository.findByCharacterSheetAndJobAction(characterSheet, jobAction).isPresent();
        if (actionExists) {
            throw new IllegalArgumentException("Action already exists for this character.");
        }

        // 액션 옵션 조회
        ActionOption actionOption = null;
        if (actionRequest.getActionOptionId() != null) {
            actionOption = actionOptionRepository.findById(actionRequest.getActionOptionId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Action Option ID: " + actionRequest.getActionOptionId()));
        }

        // 새 캐릭터 액션 생성 및 저장
        CharacterAction newAction = CharacterAction.builder()
                .characterSheet(characterSheet)
                .playMember(playMember)
                .jobAction(jobAction)
                .actionOption(actionOption)
                .build();

        characterActionRepository.save(newAction);
        log.info("액션 추가 완료 - 액션 ID: {}", newAction.getId());
    }

    /**
     * 캐릭터 액션 삭제
     */
    @Transactional
    public void deleteCharacterAction(Long roomID, Long playMemberID, Long characterActionID) {
        // playMember와 CharacterSheet 조회
        PlayMember playMember = playMemberRepository.findByIdAndRoomId(playMemberID, roomID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PlayMember or Room ID"));

        CharacterSheet characterSheet = characterSheetRepository.findByPlayMember(playMember)
                .orElseThrow(() -> new IllegalArgumentException("Character Sheet not found"));

        // CharacterAction 조회
        CharacterAction characterAction = characterActionRepository.findByIdAndCharacterSheet(characterActionID, characterSheet)
                .orElseThrow(() -> new IllegalArgumentException("Character Action not found"));

        // 캐릭터 액션 삭제
        characterActionRepository.delete(characterAction);
    }

}
