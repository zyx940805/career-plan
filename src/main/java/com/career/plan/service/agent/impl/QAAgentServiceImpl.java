package com.career.plan.service.agent.impl;

import com.career.plan.dto.AgentDTO.QAItem;
import com.career.plan.dto.AgentDTO.QARequest;
import com.career.plan.dto.AgentDTO.QAResponse;
import com.career.plan.service.agent.QAAgentService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QAAgentServiceImpl implements QAAgentService {

    @Override
    public QAResponse qualityCheck(QARequest request) {
        QAResponse response = new QAResponse();

        List<QAItem> issues = checkReportQuality(request.getReport());
        response.setIssues(issues);

        int score = calculateQualityScore(issues);
        response.setScore(score);

        boolean passed = score >= 60;
        response.setPassed(passed);

        List<String> suggestions = generateSuggestions(issues);
        response.setSuggestions(suggestions);

        return response;
    }

    private List<QAItem> checkReportQuality(Object reportObj) {
        List<QAItem> issues = new ArrayList<>();

        if (!(reportObj instanceof Map)) {
            issues.add(createIssue("数据格式", "报告格式不正确", "请确保报告为 Map 格式", 3));
            return issues;
        }

        Map<?, ?> report = (Map<?, ?>) reportObj;

        if (!report.containsKey("studentProfile")) {
            issues.add(createIssue("完整性", "缺少学生画像数据", "请补充学生基本信息", 2));
        }

        if (!report.containsKey("jobMatches") || ((List<?>) report.get("jobMatches")).isEmpty()) {
            issues.add(createIssue("完整性", "缺少岗位匹配数据", "请补充岗位匹配结果", 2));
        }

        if (!report.containsKey("pathPlanning")) {
            issues.add(createIssue("完整性", "缺少路径规划", "请补充职业发展路径", 1));
        }

        Object matchAnalysis = report.get("matchAnalysis");
        if (matchAnalysis != null && matchAnalysis instanceof Map) {
            Map<?, ?> analysis = (Map<?, ?>) matchAnalysis;
            if (!analysis.containsKey("totalScore")) {
                issues.add(createIssue("逻辑性", "缺少匹配总分", "请补充匹配分数", 2));
            }
        }

        return issues;
    }

    private QAItem createIssue(String dimension, String issue, String suggestion, Integer severity) {
        QAItem item = new QAItem();
        item.setDimension(dimension);
        item.setIssue(issue);
        item.setSuggestion(suggestion);
        item.setSeverity(severity);
        return item;
    }

    private int calculateQualityScore(List<QAItem> issues) {
        int baseScore = 100;

        for (QAItem issue : issues) {
            if (issue.getSeverity() == 3) {
                baseScore -= 30;
            } else if (issue.getSeverity() == 2) {
                baseScore -= 20;
            } else if (issue.getSeverity() == 1) {
                baseScore -= 10;
            }
        }

        return Math.max(0, baseScore);
    }

    private List<String> generateSuggestions(List<QAItem> issues) {
        List<String> suggestions = new ArrayList<>();

        for (QAItem issue : issues) {
            suggestions.add(issue.getSuggestion());
        }

        if (suggestions.isEmpty()) {
            suggestions.add("报告质量良好，无需修改");
        }

        return suggestions;
    }
}
