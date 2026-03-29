package com.career.plan.service.agent.impl;

import com.career.plan.dto.AgentDTO.ReportRequest;
import com.career.plan.dto.AgentDTO.ReportResponse;
import com.career.plan.service.agent.ReportAgentService;
import com.career.plan.service.agent.report.ReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportAgentServiceImpl implements ReportAgentService {

    @Autowired
    private List<ReportGenerator> reportGenerators;

    @Override
    public ReportResponse generateReport(ReportRequest request) {
        String reportType = request.getReportType() != null ? request.getReportType() : "markdown";
        
        for (ReportGenerator generator : reportGenerators) {
            if (generator.supports(reportType)) {
                return generator.generate(request);
            }
        }
        
        return generateDefaultReport(request);
    }

    private ReportResponse generateDefaultReport(ReportRequest request) {
        ReportResponse response = new ReportResponse();
        response.setTaskId(request.getTaskId());
        response.setSummary("不支持的报告类型：" + request.getReportType());
        return response;
    }
}
