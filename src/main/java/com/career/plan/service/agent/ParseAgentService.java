package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.ParseRequest;
import com.career.plan.dto.AgentDTO.ParseResponse;

public interface ParseAgentService {
    ParseResponse parseInput(ParseRequest request);
}
