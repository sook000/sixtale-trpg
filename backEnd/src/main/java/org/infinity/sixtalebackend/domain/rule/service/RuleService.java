package org.infinity.sixtalebackend.domain.rule.service;

import org.infinity.sixtalebackend.domain.rule.dto.*;

public interface RuleService {
    JobListResponse readJobList(Long ruleID);

    JobOptionListResponse readJobOptionList(Long ruleID, Long jobID);

    public CharacterActionListResponse getCommonActions(Long ruleID);

    RuleInfoResponse readRule(Long ruleID);
}
