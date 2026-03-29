package com.career.plan.service.agent.match;

import java.util.*;

public class MatchAlgorithmTest {

    public static void main(String[] args) {
        Map<String, Object> student = createStudentProfile();
        Map<String, Object> job = createJobProfile();

        System.out.println("=== 人岗匹配算法测试 ===\n");

        float skillSimilarity = SkillSimilarityCalculator.calculateSimilarity(
                (List<String>) student.get("hardSkills"),
                (List<String>) job.get("requiredSkills")
        );

        System.out.println("技能相似度：" + String.format("%.2f", skillSimilarity));

        Map<String, Object> analysis = SkillSimilarityCalculator.analyzeSkillMatch(
                (List<String>) student.get("hardSkills"),
                (List<String>) job.get("requiredSkills")
        );

        System.out.println("匹配率：" + String.format("%.2f", analysis.get("matchRate")));
        System.out.println("匹配技能：" + analysis.get("matchedSkills"));
        System.out.println("缺失技能：" + analysis.get("missingSkills"));

        System.out.println("\n=== 测试完成 ===");
    }

    private static Map<String, Object> createStudentProfile() {
        Map<String, Object> student = new HashMap<>();
        student.put("hardSkills", Arrays.asList("Java", "Spring Boot", "MySQL", "Redis", "Vue"));
        student.put("softSkills", Arrays.asList("沟通", "协作", "学习能力强"));
        student.put("internshipExperience", "6 个月后端开发实习");

        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("education", "本科");
        basicInfo.put("graduationYear", "2025");
        student.put("basicInfo", basicInfo);

        Map<String, Object> intent = new HashMap<>();
        intent.put("expectedCity", "杭州");
        student.put("employmentIntent", intent);

        return student;
    }

    private static Map<String, Object> createJobProfile() {
        Map<String, Object> job = new HashMap<>();
        job.put("positionName", "后端开发工程师");
        job.put("requiredSkills", Arrays.asList("Java", "Spring Boot", "MySQL", "Redis", "Docker"));
        job.put("softSkills", Arrays.asList("团队协作", "沟通能力"));
        job.put("location", "杭州");
        job.put("educationRequirement", "本科及以上");

        Map<String, Double> weights = new HashMap<>();
        weights.put("technicalSkill", 0.5);
        weights.put("experience", 0.2);
        job.put("dimensionWeights", weights);

        return job;
    }
}
