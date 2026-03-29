package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.QARequest;
import com.career.plan.dto.AgentDTO.QAResponse;

public interface QAAgentService {
    QAResponse qualityCheck(QARequest request);
}
