package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.MatchRequest;
import com.career.plan.dto.AgentDTO.MatchResponse;

public interface MatchAgentService {
    MatchResponse calculateMatch(Object studentProfile, Object jobProfile);
}
