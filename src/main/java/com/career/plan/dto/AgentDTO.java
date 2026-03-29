package com.career.plan.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class AgentDTO {

    @Data
    public static class ParseRequest {
        private String userQuery;
        private String resumeText;
        private Map<String, Object> extraInfo;
    }

    @Data
    public static class ParseResponse {
        private String userQuery;
        private String resumeText;
        private Map<String, Object> extraInfo;
        private Integer repairAttempts;
        private String intent;
        private Map<String, Object> parsedData;
    }

    @Data
    public static class KeywordRequest {
        private String resumeText;
        private String jobDescription;
    }

    @Data
    public static class KeywordResponse {
        private List<String> hardSkills;
        private List<String> softSkills;
        private List<String> keywords;
        private String targetPosition;
    }

    @Data
    public static class JobRecord {
        private String positionName;
        private String companyName;
        private String location;
        private String salaryRange;
        private String jobDescription;
        private List<String> requiredSkills;
        private List<String> preferredSkills;
    }

    @Data
    public static class JobProfile {
        private String positionName;
        private List<String> requiredSkills;
        private Map<String, Double> dimensionWeights;
        private List<String> responsibilities;
        private String careerPath;
        private Map<String, Object> industryInfo;
    }

    @Data
    public static class StudentProfileResponse {
        private Map<String, Object> basicInfo;
        private List<String> hardSkills;
        private List<String> softSkills;
        private String internshipExperience;
        private Map<String, Object> employmentIntent;
        private Integer completenessScore;
        private Integer competitivenessScore;
        private List<String> missingFields;
        private List<String> strengths;
        private List<String> weaknesses;
    }

    @Data
    public static class MatchRequest {
        private Object studentProfile;
        private Object jobProfile;
    }

    @Data
    public static class MatchResponse {
        private Float totalScore;
        private Map<String, Float> dimensionScores;
        private List<String> strengths;
        private List<String> gaps;
        private List<String> actionSuggestions;
        private String matchConclusion;
    }

    @Data
    public static class PathPlanningRequest {
        private String targetPosition;
        private Object currentProfile;
        private List<String> gaps;
    }

    @Data
    public static class PathPlanningResponse {
        private List<Phase> phases;
        private Integer totalDuration;
        private String summary;
    }

    @Data
    public static class Phase {
        private String phaseName;
        private Integer duration;
        private List<String> tasks;
        private List<String> goals;
        private List<String> resources;
    }

    @Data
    public static class ReportRequest {
        private String taskId;
        private Object studentProfile;
        private List<Object> jobMatches;
        private Object pathPlanning;
        private Object matchAnalysis;
        private String reportType; // markdown/pdf/html/json
        private Boolean includeCharts;
        private String template; // professional/simple/detailed
    }

    @Data
    public static class ReportResponse {
        private String taskId;
        private String markdown;
        private Map<String, Object> structuredReport;
        private String summary;
        private String topRecommendation;
        private String html;
        private String pdfUrl;
        private ReportMetadata metadata;
    }

    @Data
    public static class ReportMetadata {
        private Date generatedAt;
        private Integer wordCount;
        private Integer pageCount;
        private String version;
        private List<String> sections;
        private Map<String, Object> statistics;
    }

    @Data
    public static class QARequest {
        private Object report;
    }

    @Data
    public static class QAResponse {
        private Boolean passed;
        private Integer score;
        private List<QAItem> issues;
        private List<String> suggestions;
    }

    @Data
    public static class QAItem {
        private String dimension;
        private String issue;
        private String suggestion;
        private Integer severity;
    }

    @Data
    public static class RepairRequest {
        private Object originalReport;
        private List<QAItem> issues;
    }

    @Data
    public static class RepairResponse {
        private Object repairedReport;
        private List<String> changes;
        private Boolean success;
    }
}
