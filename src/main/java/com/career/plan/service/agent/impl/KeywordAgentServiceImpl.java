package com.career.plan.service.agent.impl;

import com.career.plan.dto.AgentDTO.KeywordRequest;
import com.career.plan.dto.AgentDTO.KeywordResponse;
import com.career.plan.service.agent.KeywordAgentService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KeywordAgentServiceImpl implements KeywordAgentService {

    private static final Set<String> HARD_SKILL_KEYWORDS = new HashSet<>(Arrays.asList(
            "Java", "Python", "C++", "JavaScript", "TypeScript", "Go", "Rust",
            "Spring", "Spring Boot", "Spring Cloud", "Django", "Flask",
            "MySQL", "PostgreSQL", "Oracle", "Redis", "MongoDB",
            "Linux", "Docker", "Kubernetes", "Git", "Jenkins",
            "Vue", "React", "Angular", "HTML", "CSS",
            "AWS", "Azure", "阿里云", "腾讯云"
    ));

    private static final Set<String> SOFT_SKILL_KEYWORDS = new HashSet<>(Arrays.asList(
            "沟通", "协作", "团队", "领导", "管理",
            "学习", "创新", "分析", "解决问题", "抗压",
            "责任心", "执行力", "时间管理", "逻辑思维"
    ));

    @Override
    public KeywordResponse extractKeywords(KeywordRequest request) {
        KeywordResponse response = new KeywordResponse();

        String text = request.getResumeText() + " " + request.getJobDescription();

        List<String> hardSkills = extractHardSkills(text);
        response.setHardSkills(hardSkills);

        List<String> softSkills = extractSoftSkills(text);
        response.setSoftSkills(softSkills);

        List<String> allKeywords = new ArrayList<>();
        allKeywords.addAll(hardSkills);
        allKeywords.addAll(softSkills);
        response.setKeywords(allKeywords);

        String targetPosition = identifyTargetPosition(text);
        response.setTargetPosition(targetPosition);

        return response;
    }

    private List<String> extractHardSkills(String text) {
        List<String> skills = new ArrayList<>();
        for (String keyword : HARD_SKILL_KEYWORDS) {
            if (text.contains(keyword)) {
                skills.add(keyword);
            }
        }
        return skills;
    }

    private List<String> extractSoftSkills(String text) {
        List<String> skills = new ArrayList<>();
        for (String keyword : SOFT_SKILL_KEYWORDS) {
            if (text.contains(keyword)) {
                skills.add(keyword);
            }
        }
        return skills;
    }

    private String identifyTargetPosition(String text) {
        Map<String, Integer> positionScores = new HashMap<>();
        positionScores.put("后端开发", 0);
        positionScores.put("前端开发", 0);
        positionScores.put("全栈开发", 0);
        positionScores.put("数据分析", 0);
        positionScores.put("人工智能", 0);

        if (text.contains("Java") || text.contains("Spring") || text.contains("微服务")) {
            positionScores.put("后端开发", positionScores.get("后端开发") + 2);
        }
        if (text.contains("Vue") || text.contains("React") || text.contains("前端")) {
            positionScores.put("前端开发", positionScores.get("前端开发") + 2);
        }
        if (text.contains("Python") || text.contains("数据") || text.contains("算法")) {
            positionScores.put("数据分析", positionScores.get("数据分析") + 1);
            positionScores.put("人工智能", positionScores.get("人工智能") + 1);
        }

        String maxPosition = "后端开发";
        int maxScore = 0;
        for (Map.Entry<String, Integer> entry : positionScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                maxPosition = entry.getKey();
            }
        }

        return maxPosition;
    }
}
