package com.career.plan.service.agent.report;

import com.career.plan.dto.AgentDTO.ReportRequest;
import com.career.plan.dto.AgentDTO.ReportResponse;

public interface ReportGenerator {
    ReportResponse generate(ReportRequest request);
    boolean supports(String reportType);
}
