package com.career.plan.service.agent.impl;

import com.career.plan.dto.AgentDTO.MatchResponse;
import com.career.plan.service.agent.MatchAgentService;
import com.career.plan.service.agent.match.MatchDimension;
import com.career.plan.service.agent.match.SkillSimilarityCalculator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatchAgentServiceImpl implements MatchAgentService {

    private static final Map<MatchDimension, Float> DIMENSION_WEIGHTS = new HashMap<>();

    static {
        for (MatchDimension dimension : MatchDimension.values()) {
            DIMENSION_WEIGHTS.put(dimension, dimension.getDefaultWeight());
        }
    }

    @Override
    public MatchResponse calculateMatch(Object studentProfileObj, Object jobProfileObj) {
        Map<String, Object> studentProfile = (Map<String, Object>) studentProfileObj;
        Map<String, Object> jobProfile = (Map<String, Object>) jobProfileObj;

        MatchResponse response = new MatchResponse();

        Map<String, Float> dimensionScores = calculateAllDimensions(studentProfile, jobProfile);
        response.setDimensionScores(dimensionScores);

        Float totalScore = calculateWeightedTotalScore(dimensionScores);
        response.setTotalScore(totalScore);

        Map<String, Object> skillAnalysis = SkillSimilarityCalculator.analyzeSkillMatch(
            getStudentSkills(studentProfile),
            getJobSkills(jobProfile)
        );

        List<String> strengths = identifyStrengths(studentProfile, jobProfile, skillAnalysis);
        response.setStrengths(strengths);

        List<String> gaps = identifyGaps(studentProfile, jobProfile, skillAnalysis);
        response.setGaps(gaps);

        List<String> suggestions = generateActionableSuggestions(gaps, skillAnalysis);
        response.setActionSuggestions(suggestions);

        response.setMatchConclusion(generateDetailedConclusion(totalScore, dimensionScores));

        return response;
    }

    private Map<String, Float> calculateAllDimensions(Map<String, Object> student, Map<String, Object> job) {
        Map<String, Float> scores = new HashMap<>();

        List<String> studentSkills = getStudentSkills(student);
        List<String> jobSkills = getJobSkills(job);

        float skillScore = SkillSimilarityCalculator.calculateSimilarity(studentSkills, jobSkills);
        scores.put(MatchDimension.SKILL_MATCH.getName(), skillScore);

        float experienceScore = calculateExperienceMatch(student, job);
        scores.put(MatchDimension.EXPERIENCE_MATCH.getName(), experienceScore);

        float educationScore = calculateEducationMatch(student, job);
        scores.put(MatchDimension.EDUCATION_MATCH.getName(), educationScore);

        float softSkillScore = calculateSoftSkillMatch(student, job);
        scores.put(MatchDimension.SOFT_SKILL_MATCH.getName(), softSkillScore);

        float cultureFitScore = calculateCultureFit(student, job);
        scores.put(MatchDimension.CULTURE_FIT.getName(), cultureFitScore);

        return scores;
    }

    private List<String> getStudentSkills(Map<String, Object> student) {
        List<String> skills = new ArrayList<>();
        if (student.containsKey("hardSkills")) {
            Object obj = student.get("hardSkills");
            if (obj instanceof List) {
                skills.addAll((List<String>) obj);
            }
        }
        return skills;
    }

    private List<String> getJobSkills(Map<String, Object> job) {
        List<String> skills = new ArrayList<>();
        if (job.containsKey("requiredSkills")) {
            Object obj = job.get("requiredSkills");
            if (obj instanceof List) {
                skills.addAll((List<String>) obj);
            }
        }
        return skills;
    }

    private float calculateExperienceMatch(Map<String, Object> student, Map<String, Object> job) {
        float score = 50.0f;

        String internship = (String) student.getOrDefault("internshipExperience", "");
        if (internship != null && !internship.isEmpty()) {
            score += 30.0f;
            
            if (internship.contains("年") || internship.contains("个")) {
                score += 20.0f;
            }
        }

        Map<String, Object> basicInfo = (Map<String, Object>) student.getOrDefault("basicInfo", new HashMap<>());
        String graduationYear = (String) basicInfo.getOrDefault("graduationYear", "");
        if (graduationYear != null && !graduationYear.isEmpty()) {
            try {
                int year = Integer.parseInt(graduationYear);
                int currentYear = 2026;
                int experienceYears = currentYear - year;
                if (experienceYears >= 3) {
                    score += 20.0f;
                } else if (experienceYears >= 1) {
                    score += 10.0f;
                }
            } catch (NumberFormatException e) {
            }
        }

        return Math.min(100.0f, score);
    }

    private float calculateEducationMatch(Map<String, Object> student, Map<String, Object> job) {
        Map<String, Object> basicInfo = (Map<String, Object>) student.getOrDefault("basicInfo", new HashMap<>());
        String education = (String) basicInfo.getOrDefault("education", "本科");

        float score = 70.0f;

        if ("博士".equals(education)) {
            score = 100.0f;
        } else if ("硕士".equals(education)) {
            score = 90.0f;
        } else if ("本科".equals(education)) {
            score = 80.0f;
        } else if ("大专".equals(education)) {
            score = 65.0f;
        }

        if (job.containsKey("educationRequirement")) {
            String requiredEdu = (String) job.get("educationRequirement");
            if ("硕士及以上".equals(requiredEdu) && !"硕士".equals(education) && !"博士".equals(education)) {
                score -= 20.0f;
            }
        }

        return Math.max(0.0f, Math.min(100.0f, score));
    }

    private float calculateSoftSkillMatch(Map<String, Object> student, Map<String, Object> job) {
        List<String> studentSoftSkills = getSoftSkills(student);
        List<String> jobSoftSkills = getJobSoftSkills(job);

        if (jobSoftSkills.isEmpty()) {
            return studentSoftSkills.isEmpty() ? 60.0f : 80.0f;
        }

        if (studentSoftSkills.isEmpty()) {
            return 40.0f;
        }

        int matchedCount = 0;
        for (String jobSkill : jobSoftSkills) {
            for (String studentSkill : studentSoftSkills) {
                if (jobSkill.contains(studentSkill) || studentSkill.contains(jobSkill)) {
                    matchedCount++;
                    break;
                }
            }
        }

        return (matchedCount * 100.0f) / jobSoftSkills.size();
    }

    private List<String> getSoftSkills(Map<String, Object> student) {
        List<String> skills = new ArrayList<>();
        if (student.containsKey("softSkills")) {
            Object obj = student.get("softSkills");
            if (obj instanceof List) {
                skills.addAll((List<String>) obj);
            }
        }
        return skills;
    }

    private List<String> getJobSoftSkills(Map<String, Object> job) {
        List<String> skills = new ArrayList<>();
        if (job.containsKey("softSkills")) {
            Object obj = job.get("softSkills");
            if (obj instanceof List) {
                skills.addAll((List<String>) obj);
            }
        }
        return skills;
    }

    private float calculateCultureFit(Map<String, Object> student, Map<String, Object> job) {
        float score = 70.0f;

        Map<String, Object> studentIntent = (Map<String, Object>) student.getOrDefault("employmentIntent", new HashMap<>());
        String expectedLocation = (String) studentIntent.getOrDefault("expectedCity", "");
        
        if (job.containsKey("location")) {
            String jobLocation = (String) job.get("location");
            if (expectedLocation != null && expectedLocation.equals(jobLocation)) {
                score += 20.0f;
            }
        }

        if (job.containsKey("companySize")) {
            score += 10.0f;
        }

        return Math.min(100.0f, score);
    }

    private float calculateWeightedTotalScore(Map<String, Float> dimensionScores) {
        float total = 0.0f;
        
        for (Map.Entry<String, Float> entry : dimensionScores.entrySet()) {
            float weight = getWeightForDimension(entry.getKey());
            total += entry.getValue() * weight;
        }
        
        return total;
    }

    private float getWeightForDimension(String dimensionName) {
        for (Map.Entry<MatchDimension, Float> entry : DIMENSION_WEIGHTS.entrySet()) {
            if (entry.getKey().getName().equals(dimensionName)) {
                return entry.getValue();
            }
        }
        return 0.2f;
    }

    private List<String> identifyStrengths(Map<String, Object> student, Map<String, Object> job, Map<String, Object> skillAnalysis) {
        List<String> strengths = new ArrayList<>();

        List<String> matchedSkills = (List<String>) skillAnalysis.getOrDefault("matchedSkills", new ArrayList<>());
        for (String skill : matchedSkills) {
            strengths.add("掌握核心技能：" + skill);
        }

        float similarity = (Float) skillAnalysis.getOrDefault("similarity", 0.0f);
        if (similarity >= 80.0f) {
            strengths.add("技能匹配度很高（" + String.format("%.1f", similarity) + "%）");
        }

        if (student.get("internshipExperience") != null && !((String) student.get("internshipExperience")).isEmpty()) {
            strengths.add("具备相关实习经验");
        }

        Map<String, Object> basicInfo = (Map<String, Object>) student.getOrDefault("basicInfo", new HashMap<>());
        String education = (String) basicInfo.getOrDefault("education", "");
        if ("硕士".equals(education) || "博士".equals(education)) {
            strengths.add("学历优势明显");
        }

        List<String> softSkills = getSoftSkills(student);
        if (!softSkills.isEmpty()) {
            strengths.add("软技能突出：" + String.join(", ", softSkills));
        }

        return strengths;
    }

    private List<String> identifyGaps(Map<String, Object> student, Map<String, Object> job, Map<String, Object> skillAnalysis) {
        List<String> gaps = new ArrayList<>();

        List<String> missingSkills = (List<String>) skillAnalysis.getOrDefault("missingSkills", new ArrayList<>());
        for (String skill : missingSkills) {
            gaps.add("缺少核心技能：" + skill);
        }

        float experienceScore = (Float) skillAnalysis.getOrDefault("experienceScore", 0.0f);
        if (experienceScore < 60.0f) {
            gaps.add("项目/实习经验不足");
        }

        Map<String, Object> basicInfo = (Map<String, Object>) student.getOrDefault("basicInfo", new HashMap<>());
        String education = (String) basicInfo.getOrDefault("education", "");
        if (job.containsKey("educationRequirement")) {
            String requiredEdu = (String) job.get("educationRequirement");
            if ("硕士及以上".equals(requiredEdu) && !"硕士".equals(education) && !"博士".equals(education)) {
                gaps.add("学历未达到岗位要求");
            }
        }

        List<String> studentSoftSkills = getSoftSkills(student);
        List<String> jobSoftSkills = getJobSoftSkills(job);
        if (!jobSoftSkills.isEmpty() && studentSoftSkills.isEmpty()) {
            gaps.add("缺少软技能描述");
        }

        return gaps;
    }

    private List<String> generateActionableSuggestions(List<String> gaps, Map<String, Object> skillAnalysis) {
        List<String> suggestions = new ArrayList<>();

        for (String gap : gaps) {
            if (gap.contains("缺少核心技能")) {
                String skill = gap.replace("缺少核心技能：", "");
                suggestions.add("建议系统学习 " + skill + "，可通过官方文档或实战项目");
            } else if (gap.contains("经验不足")) {
                suggestions.add("参与开源项目或实习，积累实战经验");
                suggestions.add("在 GitHub 上建立个人项目作品集");
            } else if (gap.contains("学历未达")) {
                suggestions.add("考虑继续深造或考取相关技术认证");
            } else if (gap.contains("软技能")) {
                suggestions.add("加强沟通协作能力培养，参与团队项目");
            }
        }

        if (suggestions.isEmpty()) {
            suggestions.add("保持当前学习节奏，准备简历投递");
        }

        suggestions.add("建议进行模拟面试，提升面试技巧");
        suggestions.add("关注行业动态，持续学习新技术");

        return suggestions;
    }

    private String generateDetailedConclusion(Float totalScore, Map<String, Float> dimensionScores) {
        StringBuilder conclusion = new StringBuilder();

        if (totalScore >= 85.0f) {
            conclusion.append("匹配度极高（").append(String.format("%.1f", totalScore)).append("分），强烈建议投递！");
            conclusion.append("\n你的技能与岗位要求高度契合，竞争力很强。");
        } else if (totalScore >= 70.0f) {
            conclusion.append("匹配度良好（").append(String.format("%.1f", totalScore)).append("分），建议投递。");
            conclusion.append("\n你具备岗位所需的大部分技能，有一定竞争力。");
        } else if (totalScore >= 55.0f) {
            conclusion.append("匹配度中等（").append(String.format("%.1f", totalScore)).append("分），可尝试投递。");
            conclusion.append("\n建议针对缺失技能进行补充后再投递，成功率更高。");
        } else if (totalScore >= 40.0f) {
            conclusion.append("匹配度较低（").append(String.format("%.1f", totalScore)).append("分），谨慎投递。");
            conclusion.append("\n建议先系统学习相关技能，提升匹配度后再考虑。");
        } else {
            conclusion.append("匹配度很低（").append(String.format("%.1f", totalScore)).append("分），不建议投递。");
            conclusion.append("\n当前能力与岗位要求差距较大，建议重新考虑岗位方向。");
        }

        return conclusion.toString();
    }
}
