package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.ReportRequest;
import com.career.plan.dto.AgentDTO.ReportResponse;

public interface ReportAgentService {
    ReportResponse generateReport(ReportRequest request);
}
