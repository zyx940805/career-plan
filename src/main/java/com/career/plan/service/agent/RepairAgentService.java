package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.RepairRequest;
import com.career.plan.dto.AgentDTO.RepairResponse;

public interface RepairAgentService {
    RepairResponse repairReport(RepairRequest request);
}
