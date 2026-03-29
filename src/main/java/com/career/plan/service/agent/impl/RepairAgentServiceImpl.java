package com.career.plan.service.agent.impl;

import com.career.plan.dto.AgentDTO.QAItem;
import com.career.plan.dto.AgentDTO.RepairRequest;
import com.career.plan.dto.AgentDTO.RepairResponse;
import com.career.plan.service.agent.RepairAgentService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RepairAgentServiceImpl implements RepairAgentService {

    @Override
    public RepairResponse repairReport(RepairRequest request) {
        RepairResponse response = new RepairResponse();

        Object repairedReport = applyRepairs(request.getOriginalReport(), request.getIssues());
        response.setRepairedReport(repairedReport);

        List<String> changes = recordChanges(request.getIssues());
        response.setChanges(changes);

        response.setSuccess(true);

        return response;
    }

    private Object applyRepairs(Object originalReport, List<QAItem> issues) {
        if (!(originalReport instanceof Map)) {
            return originalReport;
        }

        Map<String, Object> report = new HashMap<>((Map<String, Object>) originalReport);

        for (QAItem issue : issues) {
            if ("完整性".equals(issue.getDimension())) {
                if (issue.getIssue().contains("缺少学生画像")) {
                    report.put("studentProfile", createDefaultStudentProfile());
                } else if (issue.getIssue().contains("缺少岗位匹配")) {
                    report.put("jobMatches", new ArrayList<>());
                } else if (issue.getIssue().contains("缺少路径规划")) {
                    report.put("pathPlanning", createDefaultPathPlanning());
                }
            }
        }

        return report;
    }

    private Map<String, Object> createDefaultStudentProfile() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("basicInfo", new HashMap<>());
        profile.put("hardSkills", new ArrayList<>());
        profile.put("softSkills", new ArrayList<>());
        return profile;
    }

    private Map<String, Object> createDefaultPathPlanning() {
        Map<String, Object> planning = new HashMap<>();
        planning.put("phases", new ArrayList<>());
        planning.put("totalDuration", 6);
        return planning;
    }

    private List<String> recordChanges(List<QAItem> issues) {
        List<String> changes = new ArrayList<>();

        for (QAItem issue : issues) {
            changes.add("修复：" + issue.getIssue() + " - " + issue.getSuggestion());
        }

        return changes;
    }
}
