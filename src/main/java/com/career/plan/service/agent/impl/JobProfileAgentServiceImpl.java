package com.career.plan.service.agent.impl;

import com.career.plan.dto.AgentDTO.JobProfile;
import com.career.plan.dto.AgentDTO.JobRecord;
import com.career.plan.service.agent.JobProfileAgentService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobProfileAgentServiceImpl implements JobProfileAgentService {

    @Override
    public JobProfile analyzeJobProfile(JobRecord jobRecord) {
        JobProfile profile = new JobProfile();

        profile.setPositionName(jobRecord.getPositionName());

        List<String> allSkills = new ArrayList<>();
        if (jobRecord.getRequiredSkills() != null) {
            allSkills.addAll(jobRecord.getRequiredSkills());
        }
        if (jobRecord.getPreferredSkills() != null) {
            allSkills.addAll(jobRecord.getPreferredSkills());
        }
        profile.setRequiredSkills(allSkills);

        Map<String, Double> weights = calculateDimensionWeights(jobRecord);
        profile.setDimensionWeights(weights);

        List<String> responsibilities = extractResponsibilities(jobRecord.getJobDescription());
        profile.setResponsibilities(responsibilities);

        profile.setCareerPath(generateCareerPath(jobRecord.getPositionName()));

        Map<String, Object> industryInfo = new HashMap<>();
        industryInfo.put("industry", identifyIndustry(jobRecord));
        industryInfo.put("companyScale", estimateCompanyScale(jobRecord.getCompanyName()));
        industryInfo.put("developmentProspect", "良好");
        profile.setIndustryInfo(industryInfo);

        return profile;
    }

    private Map<String, Double> calculateDimensionWeights(JobRecord jobRecord) {
        Map<String, Double> weights = new HashMap<>();

        int totalSkills = jobRecord.getRequiredSkills().size() +
                (jobRecord.getPreferredSkills() != null ? jobRecord.getPreferredSkills().size() : 0);

        weights.put("technicalSkill", Math.min(0.5, totalSkills * 0.05));
        weights.put("experience", 0.2);
        weights.put("education", 0.15);
        weights.put("softSkill", 0.15);

        return weights;
    }

    private List<String> extractResponsibilities(String description) {
        List<String> responsibilities = new ArrayList<>();
        responsibilities.add("参与系统设计与开发");
        responsibilities.add("编写高质量代码");
        responsibilities.add("参与代码评审");
        responsibilities.add("解决技术难题");
        return responsibilities;
    }

    private String generateCareerPath(String position) {
        if (position.contains("后端")) {
            return "初级开发→中级开发→高级开发→技术专家/架构师";
        } else if (position.contains("前端")) {
            return "初级前端→中级前端→高级前端→前端架构师";
        } else if (position.contains("全栈")) {
            return "全栈开发→技术负责人→CTO";
        }
        return "技术路线/管理路线双通道发展";
    }

    private String identifyIndustry(JobRecord jobRecord) {
        String company = jobRecord.getCompanyName();
        if (company.contains("阿里") || company.contains("腾讯") || company.contains("字节")) {
            return "互联网";
        } else if (company.contains("银行") || company.contains("金融")) {
            return "金融科技";
        }
        return "信息技术";
    }

    private String estimateCompanyScale(String companyName) {
        if (companyName.contains("阿里") || companyName.contains("腾讯") ||
                companyName.contains("字节") || companyName.contains("百度")) {
            return "大型 (10000 人以上)";
        } else if (companyName.contains("科技") || companyName.contains("网络")) {
            return "中型 (100-1000 人)";
        }
        return "初创/小型";
    }
}
