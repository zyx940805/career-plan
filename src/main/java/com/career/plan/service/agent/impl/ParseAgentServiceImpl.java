package com.career.plan.service.agent.impl;

import com.career.plan.dto.AgentDTO.ParseRequest;
import com.career.plan.dto.AgentDTO.ParseResponse;
import com.career.plan.service.agent.ParseAgentService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParseAgentServiceImpl implements ParseAgentService {

    @Override
    public ParseResponse parseInput(ParseRequest request) {
        ParseResponse response = new ParseResponse();

        response.setUserQuery(request.getUserQuery());
        response.setResumeText(request.getResumeText());
        response.setExtraInfo(request.getExtraInfo());
        response.setRepairAttempts(0);

        String intent = identifyIntent(request.getUserQuery());
        response.setIntent(intent);

        Map<String, Object> parsedData = extractStructuredData(request);
        response.setParsedData(parsedData);

        return response;
    }

    private String identifyIntent(String query) {
        if (query == null) return "UNKNOWN";

        String lowerQuery = query.toLowerCase();

        if (lowerQuery.contains("规划") || lowerQuery.contains("发展")) {
            return "CAREER_PLANNING";
        } else if (lowerQuery.contains("匹配") || lowerQuery.contains("适合")) {
            return "JOB_MATCHING";
        } else if (lowerQuery.contains("建议") || lowerQuery.contains("提升")) {
            return "ADVICE_REQUEST";
        } else if (lowerQuery.contains("分析") || lowerQuery.contains("评估")) {
            return "ANALYSIS_REQUEST";
        }

        return "GENERAL_INQUIRY";
    }

    private Map<String, Object> extractStructuredData(ParseRequest request) {
        Map<String, Object> data = new HashMap<>();

        String resumeText = request.getResumeText();
        if (resumeText != null && !resumeText.isEmpty()) {
            data.put("hasResume", true);

            List<String> skills = extractSkills(resumeText);
            data.put("extractedSkills", skills);

            String education = extractEducation(resumeText);
            data.put("education", education);

            String experience = extractExperience(resumeText);
            data.put("experience", experience);
        } else {
            data.put("hasResume", false);
        }

        if (request.getExtraInfo() != null) {
            data.putAll(request.getExtraInfo());
        }

        return data;
    }

    private List<String> extractSkills(String text) {
        List<String> skills = new ArrayList<>();
        String[] skillKeywords = {"Java", "Python", "JavaScript", "Spring", "MySQL",
                "Redis", "Linux", "Git", "Docker", "Kubernetes"};

        for (String keyword : skillKeywords) {
            if (text.contains(keyword)) {
                skills.add(keyword);
            }
        }

        return skills;
    }

    private String extractEducation(String text) {
        Pattern pattern = Pattern.compile("(本科 | 硕士 | 博士 | 大专| 大学)", Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "未知";
    }

    private String extractExperience(String text) {
        Pattern pattern = Pattern.compile("(\\d+)[年个]? (实习 | 工作 | 经验)", Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1) + "年";
        }
        return "无相关经验";
    }
}
