package com.career.plan.service.agent.impl;

import com.career.plan.dto.AgentDTO.StudentProfileResponse;
import com.career.plan.entity.StudentProfile;
import com.career.plan.service.agent.StudentProfileAgentService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentProfileAgentServiceImpl implements StudentProfileAgentService {

    @Override
    public StudentProfileResponse analyzeStudentProfile(StudentProfile profile) {
        StudentProfileResponse response = new StudentProfileResponse();

        response.setBasicInfo(profile.getBasicInfo());
        response.setHardSkills(profile.getHardSkills());
        response.setSoftSkills(profile.getSoftSkills());
        response.setInternshipExperience(profile.getInternshipExperience());
        response.setEmploymentIntent(profile.getEmploymentIntent());

        int completenessScore = calculateCompletenessScore(profile);
        response.setCompletenessScore(completenessScore);

        int competitivenessScore = calculateCompetitivenessScore(profile);
        response.setCompetitivenessScore(competitivenessScore);

        List<String> missingFields = identifyMissingFields(profile);
        response.setMissingFields(missingFields);

        List<String> strengths = identifyStrengths(profile);
        response.setStrengths(strengths);

        List<String> weaknesses = identifyWeaknesses(profile);
        response.setWeaknesses(weaknesses);

        return response;
    }

    @Override
    public StudentProfileResponse createProfileFromResume(String resumeText) {
        StudentProfileResponse response = new StudentProfileResponse();

        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("source", "resume_parse");
        response.setBasicInfo(basicInfo);

        List<String> hardSkills = extractSkillsFromText(resumeText, true);
        response.setHardSkills(hardSkills);

        List<String> softSkills = extractSkillsFromText(resumeText, false);
        response.setSoftSkills(softSkills);

        response.setInternshipExperience(extractInternship(resumeText));

        Map<String, Object> employmentIntent = new HashMap<>();
        employmentIntent.put("expectedPosition", identifyTargetPosition(resumeText));
        employmentIntent.put("expectedCity", "北京");
        response.setEmploymentIntent(employmentIntent);

        response.setCompletenessScore(calculateResumeCompleteness(resumeText));
        response.setCompetitivenessScore(estimateCompetitiveness(hardSkills, softSkills));

        response.setMissingFields(Arrays.asList("项目经历", "获奖情况"));
        response.setStrengths(Arrays.asList("技能匹配度高", "学习能力强"));
        response.setWeaknesses(Arrays.asList("项目经验不足", "缺少实习经历"));

        return response;
    }

    private int calculateCompletenessScore(StudentProfile profile) {
        int score = 0;
        if (profile.getBasicInfo() != null && !profile.getBasicInfo().isEmpty()) score += 20;
        if (profile.getHardSkills() != null && !profile.getHardSkills().isEmpty()) score += 20;
        if (profile.getSoftSkills() != null && !profile.getSoftSkills().isEmpty()) score += 15;
        if (profile.getInternshipExperience() != null && !profile.getInternshipExperience().isEmpty()) score += 25;
        if (profile.getEmploymentIntent() != null && !profile.getEmploymentIntent().isEmpty()) score += 20;
        return score;
    }

    private int calculateCompetitivenessScore(StudentProfile profile) {
        int baseScore = 60;

        if (profile.getHardSkills() != null) {
            baseScore += Math.min(20, profile.getHardSkills().size() * 3);
        }

        if (profile.getInternshipExperience() != null) {
            baseScore += 15;
        }

        if (profile.getEmploymentIntent() != null) {
            baseScore += 5;
        }

        return Math.min(100, baseScore);
    }

    private List<String> identifyMissingFields(StudentProfile profile) {
        List<String> missing = new ArrayList<>();
        if (profile.getBasicInfo() == null || profile.getBasicInfo().isEmpty()) missing.add("基本信息");
        if (profile.getHardSkills() == null || profile.getHardSkills().isEmpty()) missing.add("专业技能");
        if (profile.getInternshipExperience() == null || profile.getInternshipExperience().isEmpty()) missing.add("实习经历");
        return missing;
    }

    private List<String> identifyStrengths(StudentProfile profile) {
        List<String> strengths = new ArrayList<>();
        if (profile.getHardSkills() != null && profile.getHardSkills().size() >= 5) {
            strengths.add("技能储备充足");
        }
        if (profile.getInternshipExperience() != null) {
            strengths.add("具备实习经验");
        }
        if (profile.getCompetitivenessScore() != null && profile.getCompetitivenessScore() > 70) {
            strengths.add("竞争力较强");
        }
        return strengths;
    }

    private List<String> identifyWeaknesses(StudentProfile profile) {
        List<String> weaknesses = new ArrayList<>();
        if (profile.getHardSkills() == null || profile.getHardSkills().size() < 3) {
            weaknesses.add("专业技能不足");
        }
        if (profile.getInternshipExperience() == null || profile.getInternshipExperience().isEmpty()) {
            weaknesses.add("缺少实习经历");
        }
        return weaknesses;
    }

    private List<String> extractSkillsFromText(String text, boolean isHardSkill) {
        List<String> skills = new ArrayList<>();
        if (isHardSkill) {
            String[] keywords = {"Java", "Python", "Spring", "MySQL", "Redis", "Linux"};
            for (String keyword : keywords) {
                if (text.contains(keyword)) skills.add(keyword);
            }
        } else {
            String[] keywords = {"沟通", "协作", "团队", "学习", "创新"};
            for (String keyword : keywords) {
                if (text.contains(keyword)) skills.add(keyword);
            }
        }
        return skills;
    }

    private String extractInternship(String text) {
        if (text.contains("实习")) {
            return "有相关实习经历";
        }
        return "";
    }

    private String identifyTargetPosition(String text) {
        if (text.contains("Java") || text.contains("后端")) {
            return "后端开发工程师";
        } else if (text.contains("前端") || text.contains("Vue")) {
            return "前端开发工程师";
        }
        return "软件开发工程师";
    }

    private int calculateResumeCompleteness(String text) {
        int score = 40;
        if (text.length() > 200) score += 20;
        if (text.contains("项目")) score += 20;
        if (text.contains("实习")) score += 20;
        return Math.min(100, score);
    }

    private int estimateCompetitiveness(List<String> hardSkills, List<String> softSkills) {
        int score = 50;
        score += Math.min(30, hardSkills.size() * 5);
        score += Math.min(20, softSkills.size() * 4);
        return Math.min(100, score);
    }
}
