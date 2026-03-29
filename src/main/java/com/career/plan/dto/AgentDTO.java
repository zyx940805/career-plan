package com.career.plan.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

public class AgentDTO {

    // 1. 解析输入请求
    @Data
    public static class ParseRequest {
        private String userQuery;
        private String resumeText;
        private Map<String, Object> extraInfo;
    }

    // 2. 关键词提取请求/响应
    @Data
    public static class KeywordRequest {
        private String resumeText;
    }

    // 3. 岗位检索响应
    @Data
    public static class JobRecord {
        private String positionName;
        private String companyName;
        private String location;
        private String salaryRange;
        private String jobDescription;
    }

    // 4. 岗位画像响应
    @Data
    public static class JobProfile {
        private String positionName;
        private List<String> requiredSkills;
        private Map<String, Double> dimensionWeights;
    }

    // 5. 人岗匹配请求
    @Data
    public static class MatchRequest {
        private Object studentProfile;
        private Object jobProfile;
    }

    // 6. 质核请求
    @Data
    public static class QARequest {
        private Object report;
    }
}
