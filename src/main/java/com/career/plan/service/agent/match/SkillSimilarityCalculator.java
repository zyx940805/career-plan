package com.career.plan.service.agent.match;

import java.util.*;

public class SkillSimilarityCalculator {

    private static final Map<String, Set<String>> SKILL_GROUPS = new HashMap<>();

    static {
        initSkillGroups();
    }

    private static void initSkillGroups() {
        SKILL_GROUPS.put("后端开发", new HashSet<>(Arrays.asList(
                "Java", "Spring", "Spring Boot", "Spring Cloud", "Django", "Flask", "Node.js"
        )));

        SKILL_GROUPS.put("数据库", new HashSet<>(Arrays.asList(
                "MySQL", "PostgreSQL", "Oracle", "Redis", "MongoDB"
        )));

        SKILL_GROUPS.put("前端开发", new HashSet<>(Arrays.asList(
                "Vue", "React", "Angular", "HTML", "CSS", "JavaScript", "TypeScript"
        )));

        SKILL_GROUPS.put("DevOps", new HashSet<>(Arrays.asList(
                "Docker", "Kubernetes", "Jenkins", "Git", "Linux"
        )));

        SKILL_GROUPS.put("云计算", new HashSet<>(Arrays.asList(
                "AWS", "Azure", "阿里云", "腾讯云"
        )));
    }

    public static float calculateSimilarity(List<String> studentSkills, List<String> jobSkills) {
        if (jobSkills == null || jobSkills.isEmpty()) {
            return 100.0f;
        }

        if (studentSkills == null || studentSkills.isEmpty()) {
            return 0.0f;
        }

        float totalScore = 0.0f;
        int matchedCount = 0;

        for (String jobSkill : jobSkills) {
            float maxSimilarity = 0.0f;

            for (String studentSkill : studentSkills) {
                float similarity = calculateSingleSkillSimilarity(studentSkill, jobSkill);
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                }
            }

            totalScore += maxSimilarity;
            if (maxSimilarity >= 0.6f) {
                matchedCount++;
            }
        }

        float coverageScore = (matchedCount * 100.0f) / jobSkills.size();
        float averageSimilarity = totalScore / jobSkills.size();

        return coverageScore * 0.6f + averageSimilarity * 0.4f;
    }

    private static float calculateSingleSkillSimilarity(String skill1, String skill2) {
        if (skill1.equalsIgnoreCase(skill2)) {
            return 1.0f;
        }

        if (skill1.contains(skill2) || skill2.contains(skill1)) {
            return 0.8f;
        }

        if (areRelatedSkills(skill1, skill2)) {
            return 0.6f;
        }

        return 0.0f;
    }

    private static boolean areRelatedSkills(String skill1, String skill2) {
        for (Set<String> group : SKILL_GROUPS.values()) {
            boolean contains1 = group.contains(skill1);
            boolean contains2 = group.contains(skill2);
            if (contains1 && contains2) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, Object> analyzeSkillMatch(List<String> studentSkills, List<String> jobSkills) {
        Map<String, Object> analysis = new HashMap<>();

        float similarity = calculateSimilarity(studentSkills, jobSkills);
        analysis.put("similarity", similarity);

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String jobSkill : jobSkills) {
            boolean matched = false;
            for (String studentSkill : studentSkills) {
                if (calculateSingleSkillSimilarity(studentSkill, jobSkill) >= 0.6f) {
                    matched = true;
                    matchedSkills.add(jobSkill);
                    break;
                }
            }
            if (!matched) {
                missingSkills.add(jobSkill);
            }
        }

        analysis.put("matchedSkills", matchedSkills);
        analysis.put("missingSkills", missingSkills);
        analysis.put("matchRate", jobSkills.isEmpty() ? 100.0f : (matchedSkills.size() * 100.0f) / jobSkills.size());

        return analysis;
    }
}

