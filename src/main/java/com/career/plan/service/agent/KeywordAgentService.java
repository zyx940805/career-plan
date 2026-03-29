package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.KeywordRequest;
import com.career.plan.dto.AgentDTO.KeywordResponse;

public interface KeywordAgentService {
    KeywordResponse extractKeywords(KeywordRequest request);
}
