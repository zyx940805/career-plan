package com.career.plan.service.agent.report;

import com.career.plan.dto.AgentDTO.ReportMetadata;
import com.career.plan.dto.AgentDTO.ReportRequest;
import com.career.plan.dto.AgentDTO.ReportResponse;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MarkdownReportGenerator implements ReportGenerator {

    @Override
    public ReportResponse generate(ReportRequest request) {
        ReportResponse response = new ReportResponse();

        response.setTaskId(request.getTaskId());

        StringBuilder markdown = new StringBuilder();

        markdown.append(generateHeader(request));
        markdown.append(generateExecutiveSummary(request));
        markdown.append(generateStudentProfileSection(request));
        markdown.append(generateJobMatchingSection(request));
        markdown.append(generatePathPlanningSection(request));
        markdown.append(generateSuggestionsSection(request));
        markdown.append(generateFooter(request));

        String mdContent = markdown.toString();
        response.setMarkdown(mdContent);

        ReportMetadata metadata = new ReportMetadata();
        metadata.setGeneratedAt(new Date());
        metadata.setWordCount(countWords(mdContent));
        metadata.setSections(Arrays.asList(
                "执行摘要", "个人画像", "岗位匹配", "成长路径", "发展建议"
        ));
        metadata.setStatistics(generateStatistics(request));
        metadata.setVersion("1.0");
        response.setMetadata(metadata);

        Map<String, Object> structuredReport = generateStructuredData(request);
        response.setStructuredReport(structuredReport);

        response.setSummary(generateBriefSummary(request));
        response.setTopRecommendation(generateTopRecommendation(request));

        return response;
    }

    @Override
    public boolean supports(String reportType) {
        return "markdown".equalsIgnoreCase(reportType) ||
                "md".equalsIgnoreCase(reportType) ||
                reportType == null;
    }

    private String generateHeader(ReportRequest request) {
        StringBuilder header = new StringBuilder();

        header.append("# 🎯 职业规划分析报告\n\n");
        header.append("---\n\n");

        header.append("## 📋 报告基本信息\n\n");
        header.append("- **报告编号**: ").append(request.getTaskId()).append("\n");
        header.append("- **生成时间**: ").append(new SimpleDateFormat("yyyy 年 MM 月 dd 日 HH:mm:ss").format(new Date())).append("\n");
        header.append("- **报告类型**: 职业规划与发展建议\n");
        header.append("- **版本**: v1.0\n\n");

        header.append("---\n\n");

        return header.toString();
    }

    private String generateExecutiveSummary(ReportRequest request) {
        StringBuilder summary = new StringBuilder();

        summary.append("## 💡 执行摘要\n\n");

        int jobCount = request.getJobMatches() != null ? ((List<?>) request.getJobMatches()).size() : 0;
        float avgScore = calculateAverageScore(request.getJobMatches());

        summary.append("### 核心发现\n\n");
        summary.append("本次分析共匹配了 **").append(jobCount).append("** 个相关岗位，");
        summary.append("平均匹配度为 **").append(String.format("%.1f", avgScore)).append("%**。\n\n");

        if (avgScore >= 70) {
            summary.append("### 总体评价\n\n");
            summary.append("✅ 你的技能储备与市场需求高度匹配，建议积极投递！\n\n");
        } else if (avgScore >= 50) {
            summary.append("### 总体评价\n\n");
            summary.append("⚠️ 你具备一定的基础，但仍有提升空间。建议针对性学习后投递。\n\n");
        } else {
            summary.append("### 总体评价\n\n");
            summary.append("❌ 当前能力与市场需求差距较大，建议系统学习后再考虑就业。\n\n");
        }

        summary.append("---\n\n");

        return summary.toString();
    }

    private String generateStudentProfileSection(ReportRequest request) {
        StringBuilder section = new StringBuilder();

        section.append("## 👤 个人画像分析\n\n");

        Object profile = request.getStudentProfile();
        if (profile instanceof Map) {
            Map<?, ?> studentProfile = (Map<?, ?>) profile;

            section.append("### 基本信息\n\n");
            Object basicInfo = studentProfile.get("basicInfo");
            if (basicInfo instanceof Map) {
                Map<?, ?> info = (Map<?, ?>) basicInfo;
                if (info.containsKey("education")) {
                    section.append("- 学历：").append(info.get("education")).append("\n");
                }
                if (info.containsKey("major")) {
                    section.append("- 专业：").append(info.get("major")).append("\n");
                }
                if (info.containsKey("graduationYear")) {
                    section.append("- 毕业年份：").append(info.get("graduationYear")).append("\n");
                }
            }
            section.append("\n");

            section.append("### 技能栈\n\n");
            Object hardSkillsObj = studentProfile.get("hardSkills");
            if (hardSkillsObj instanceof List) {
                List<?> hardSkillsList = (List<?>) hardSkillsObj;
                if (!hardSkillsList.isEmpty()) {
                    List<String> hardSkills = new ArrayList<>();
                    for (Object obj : hardSkillsList) {
                        if (obj instanceof String) {
                            hardSkills.add((String) obj);
                        }
                    }
                    section.append("**专业技能**: ");
                    section.append(String.join(", ", hardSkills)).append("\n\n");
                }
            }

            Object softSkillsObj = studentProfile.get("softSkills");
            if (softSkillsObj instanceof List) {
                List<?> softSkillsList = (List<?>) softSkillsObj;
                if (!softSkillsList.isEmpty()) {
                    List<String> softSkills = new ArrayList<>();
                    for (Object obj : softSkillsList) {
                        if (obj instanceof String) {
                            softSkills.add((String) obj);
                        }
                    }
                    section.append("**软技能**: ");
                    section.append(String.join(", ", softSkills)).append("\n\n");
                }
            }

            Integer completenessScore = null;
            Integer competitivenessScore = null;
            if (studentProfile.get("completenessScore") instanceof Integer) {
                completenessScore = (Integer) studentProfile.get("completenessScore");
            }
            if (studentProfile.get("competitivenessScore") instanceof Integer) {
                competitivenessScore = (Integer) studentProfile.get("competitivenessScore");
            }

            section.append("### 综合评估\n\n");
            section.append("| 评估维度 | 得分 | 评级 |\n");
            section.append("|---------|------|------|\n");
            section.append("| 档案完整性 | ").append(completenessScore != null ? completenessScore : 0).append(" | ").append(getRating(completenessScore)).append(" |\n");
            section.append("| 竞争力指数 | ").append(competitivenessScore != null ? competitivenessScore : 0).append(" | ").append(getRating(competitivenessScore)).append(" |\n\n");

            Object internshipObj = studentProfile.get("internshipExperience");
            String internship = internshipObj != null ? internshipObj.toString() : "";
            if (internship != null && !internship.isEmpty()) {
                section.append("### 实习/项目经历\n\n");
                section.append(internship).append("\n\n");
            }
        } else {
            section.append("*暂无详细的个人画像数据*\n\n");
        }

        section.append("---\n\n");

        return section.toString();
    }

    private String generateJobMatchingSection(ReportRequest request) {
        StringBuilder section = new StringBuilder();

        section.append("## 🎯 岗位匹配分析\n\n");

        List<?> jobMatches = request.getJobMatches();
        if (jobMatches == null || jobMatches.isEmpty()) {
            section.append("*暂无岗位匹配数据*\n\n");
        } else {
            section.append("### 推荐岗位 TOP ").append(Math.min(5, jobMatches.size())).append("\n\n");

            for (int i = 0; i < Math.min(5, jobMatches.size()); i++) {
                Map<?, ?> job = (Map<?, ?>) jobMatches.get(i);

                String positionName = (String) job.get("positionName");
                String companyName = (String) job.get("companyName");
                Object totalScoreObj = job.get("totalScore");
                Float totalScore = totalScoreObj instanceof Float ? (Float) totalScoreObj : 0.0f;

                section.append("### ").append(i + 1).append(". ").append(positionName);
                if (companyName != null) {
                    section.append(" - ").append(companyName);
                }
                section.append("\n\n");

                section.append("**匹配度评分**: ");
                section.append(getScoreBadge(totalScore)).append("\n\n");

                Object dimensionScoresObj = job.get("dimensionScores");
                if (dimensionScoresObj instanceof Map) {
                    Map<?, ?> dimensionScores = (Map<?, ?>) dimensionScoresObj;
                    section.append("**维度分析**:\n\n");
                    section.append("| 维度 | 得分 |\n");
                    section.append("|------|------|\n");
                    for (Map.Entry<?, ?> entry : dimensionScores.entrySet()) {
                        section.append("| ").append(entry.getKey()).append(" | ");
                        section.append(String.format("%.1f", entry.getValue())).append(" |\n");
                    }
                    section.append("\n");
                }

                List<String> strengths = (List<String>) job.get("strengths");
                if (strengths != null && !strengths.isEmpty()) {
                    section.append("**你的优势**:\n");
                    for (String strength : strengths) {
                        section.append("- ✅ ").append(strength).append("\n");
                    }
                    section.append("\n");
                }

                List<String> gaps = (List<String>) job.get("gaps");
                if (gaps != null && !gaps.isEmpty()) {
                    section.append("**待提升**:\n");
                    for (String gap : gaps) {
                        section.append("- ⚠️ ").append(gap).append("\n");
                    }
                    section.append("\n");
                }

                section.append("---\n\n");
            }

            section.append("### 匹配趋势分析\n\n");
            section.append("\n\n");
        }

        section.append("---\n\n");

        return section.toString();
    }

    private String generatePathPlanningSection(ReportRequest request) {
        StringBuilder section = new StringBuilder();

        section.append("## 📈 成长路径规划\n\n");

        Object pathPlanning = request.getPathPlanning();
        if (pathPlanning instanceof Map) {
            Map<?, ?> planning = (Map<?, ?>) pathPlanning;

            List<?> phases = (List<?>) planning.get("phases");
            if (phases != null) {
                section.append("### 发展阶段\n\n");

                for (int i = 0; i < phases.size(); i++) {
                    Map<?, ?> phase = (Map<?, ?>) phases.get(i);

                    String phaseName = (String) phase.get("phaseName");
                    Integer duration = (Integer) phase.get("duration");

                    section.append("#### 阶段 ").append(i + 1).append(": ").append(phaseName);
                    if (duration != null) {
                        section.append(" (").append(duration).append("个月)");
                    }
                    section.append("\n\n");

                    List<String> tasks = (List<String>) phase.get("tasks");
                    if (tasks != null && !tasks.isEmpty()) {
                        section.append("**核心任务**:\n");
                        for (String task : tasks) {
                            section.append("1. ").append(task).append("\n");
                        }
                        section.append("\n");
                    }

                    List<String> goals = (List<String>) phase.get("goals");
                    if (goals != null && !goals.isEmpty()) {
                        section.append("**阶段目标**:\n");
                        for (String goal : goals) {
                            section.append("- 🎯 ").append(goal).append("\n");
                        }
                        section.append("\n");
                    }

                    List<String> resources = (List<String>) phase.get("resources");
                    if (resources != null && !resources.isEmpty()) {
                        section.append("**推荐资源**:\n");
                        for (String resource : resources) {
                            section.append("- 📚 ").append(resource).append("\n");
                        }
                        section.append("\n");
                    }
                }
            }

            Integer totalDuration = (Integer) planning.get("totalDuration");
            if (totalDuration != null) {
                section.append("### 总体时间规划\n\n");
                section.append("预计总时长：**").append(totalDuration).append(" 个月**\n\n");
                section.append(generateTimeline(planning)).append("\n\n");
            }
        } else {
            section.append("*暂无详细的成长路径规划*\n\n");
        }

        section.append("---\n\n");

        return section.toString();
    }

    private String generateSuggestionsSection(ReportRequest request) {
        StringBuilder section = new StringBuilder();

        section.append("## 💬 发展建议\n\n");

        section.append("### 短期行动建议（1-3 个月）\n\n");
        section.append("1. **技能提升**: 针对岗位要求的技能缺口，制定系统学习计划\n");
        section.append("2. **项目实践**: 参与至少 1 个完整的实战项目，积累项目经验\n");
        section.append("3. **简历优化**: 根据目标岗位 JD，针对性优化简历内容\n\n");

        section.append("### 中期发展规划（3-6 个月）\n\n");
        section.append("1. **深度学习**: 在核心技能领域达到熟练水平\n");
        section.append("2. **技术广度**: 了解相关技术栈，构建完整知识体系\n");
        section.append("3. **面试准备**: 系统刷面试题，进行模拟面试\n\n");

        section.append("### 长期职业建议（6-12 个月）\n\n");
        section.append("1. **专业深耕**: 在特定领域形成自己的技术特长\n");
        section.append("2. **软实力提升**: 加强沟通协作、项目管理等能力\n");
        section.append("3. **职业规划**: 明确技术专家或管理方向的发展路径\n\n");

        section.append("### 推荐学习资源\n\n");
        section.append("- 📖 **官方文档**: 优先阅读技术官方文档，获取第一手资料\n");
        section.append("- 💻 **GitHub**: 参与开源项目，学习优秀代码实践\n");
        section.append("- 🎓 **在线课程**: 利用 MOOC 平台系统学习\n");
        section.append("- 📝 **技术博客**: 记录学习心得，建立个人品牌\n\n");

        section.append("---\n\n");

        return section.toString();
    }

    private String generateFooter(ReportRequest request) {
        StringBuilder footer = new StringBuilder();

        footer.append("## 📞 附录与说明\n\n");
        footer.append("### 关于本报告\n\n");
        footer.append("本报告基于 AI 算法生成，综合分析了你的个人档案与市场需求，");
        footer.append("旨在为你提供客观、专业的职业发展建议。\n\n");

        footer.append("### 免责声明\n\n");
        footer.append("> 本报告仅供参考，不构成绝对的就业建议。实际求职过程中，");
        footer.append("请结合个人实际情况和市场变化做出决策。\n\n");

        footer.append("### 联系方式\n\n");
        footer.append("如有任何疑问或需要进一步咨询，请联系职业规划顾问。\n\n");

        footer.append("---\n\n");
        footer.append("*报告生成时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("*\n");
        footer.append("*职业规划系统 · 让未来更清晰*\n");

        return footer.toString();
    }

    private Map<String, Object> generateStructuredData(ReportRequest request) {
        Map<String, Object> report = new LinkedHashMap<>();

        report.put("basicInfo", generateBasicInfo(request));
        report.put("executiveSummary", generateExecutiveSummaryData(request));
        report.put("studentProfile", request.getStudentProfile());
        report.put("jobMatching", generateJobMatchingData(request));
        report.put("pathPlanning", request.getPathPlanning());
        report.put("suggestions", generateSuggestionsData());
        report.put("metadata", generateMetadata(request));

        return report;
    }

    private Map<String, Object> generateBasicInfo(ReportRequest request) {
        Map<String, Object> basicInfo = new LinkedHashMap<>();
        basicInfo.put("taskId", request.getTaskId());
        basicInfo.put("generatedAt", new Date());
        basicInfo.put("reportType", "职业规划分析");
        basicInfo.put("version", "1.0");
        return basicInfo;
    }

    private Map<String, Object> generateExecutiveSummaryData(ReportRequest request) {
        Map<String, Object> summary = new LinkedHashMap<>();

        int jobCount = request.getJobMatches() != null ? ((List<?>) request.getJobMatches()).size() : 0;
        float avgScore = calculateAverageScore(request.getJobMatches());

        summary.put("matchedJobsCount", jobCount);
        summary.put("averageMatchScore", avgScore);
        summary.put("overallRating", getRating((int) avgScore));
        summary.put("recommendation", avgScore >= 70 ? "积极投递" : avgScore >= 50 ? "提升后投递" : "系统学习");

        return summary;
    }

    private Map<String, Object> generateJobMatchingData(ReportRequest request) {
        Map<String, Object> matchingData = new LinkedHashMap<>();

        if (request.getJobMatches() != null) {
            List<Map<String, Object>> processedJobs = new ArrayList<>();
            for (Object jobObj : request.getJobMatches()) {
                if (jobObj instanceof Map) {
                    Map<?, ?> job = (Map<?, ?>) jobObj;
                    Map<String, Object> processedJob = new LinkedHashMap<>();
                    processedJob.put("positionName", job.get("positionName") != null ? job.get("positionName").toString() : null);
                    processedJob.put("companyName", job.get("companyName") != null ? job.get("companyName").toString() : null);
                    processedJob.put("totalScore", job.get("totalScore"));
                    processedJob.put("dimensionScores", job.get("dimensionScores"));
                    processedJob.put("strengths", job.get("strengths"));
                    processedJob.put("gaps", job.get("gaps"));
                    processedJob.put("suggestions", job.get("actionSuggestions"));
                    processedJobs.add(processedJob);
                }
            }
            matchingData.put("jobs", processedJobs);
            matchingData.put("topRecommendation", processedJobs.isEmpty() ? null : processedJobs.get(0));
        }

        return matchingData;
    }

    private Map<String, Object> generateSuggestionsData() {
        Map<String, Object> suggestions = new LinkedHashMap<>();

        List<Map<String, String>> shortTerm = new ArrayList<>();
        Map<String, String> s1 = new LinkedHashMap<>();
        s1.put("category", "技能提升");
        s1.put("action", "制定系统学习计划");
        s1.put("priority", "高");
        shortTerm.add(s1);

        Map<String, String> s2 = new LinkedHashMap<>();
        s2.put("category", "项目实践");
        s2.put("action", "参与实战项目");
        s2.put("priority", "高");
        shortTerm.add(s2);

        suggestions.put("shortTerm", shortTerm);

        List<Map<String, String>> midTerm = new ArrayList<>();
        Map<String, String> m1 = new LinkedHashMap<>();
        m1.put("category", "深度学习");
        m1.put("action", "核心技能达到熟练水平");
        m1.put("priority", "中");
        midTerm.add(m1);

        suggestions.put("midTerm", midTerm);

        return suggestions;
    }

    private Map<String, Object> generateMetadata(ReportRequest request) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("taskId", request.getTaskId());
        metadata.put("generatedAt", new Date());
        metadata.put("statistics", generateStatistics(request));
        return metadata;
    }

    private Map<String, Object> generateStatistics(ReportRequest request) {
        Map<String, Object> stats = new LinkedHashMap<>();

        int jobCount = request.getJobMatches() != null ? ((List<?>) request.getJobMatches()).size() : 0;
        float avgScore = calculateAverageScore(request.getJobMatches());

        stats.put("totalJobsAnalyzed", jobCount);
        stats.put("averageMatchScore", avgScore);
        stats.put("highestScore", findHighestScore(request.getJobMatches()));
        stats.put("lowestScore", findLowestScore(request.getJobMatches()));

        return stats;
    }

    private String generateBriefSummary(ReportRequest request) {
        int jobCount = request.getJobMatches() != null ? ((List<?>) request.getJobMatches()).size() : 0;
        float avgScore = calculateAverageScore(request.getJobMatches());

        return String.format("已为您深度分析 %d 个匹配岗位，平均匹配度 %.1f%%。报告包含个人画像、岗位匹配、成长路径等完整建议。",
                jobCount, avgScore);
    }

    private String generateTopRecommendation(ReportRequest request) {
        if (request.getJobMatches() != null && !((List<?>) request.getJobMatches()).isEmpty()) {
            Object firstJob = request.getJobMatches().get(0);
            if (firstJob instanceof Map) {
                Map<?, ?> job = (Map<?, ?>) firstJob;
                String positionName = job.get("positionName") != null ? job.get("positionName").toString() : "";
                String companyName = job.get("companyName") != null ? job.get("companyName").toString() : "";
                return "强烈建议投递：" + positionName + (companyName != null && !companyName.isEmpty() ? " - " + companyName : "");
            }
        }
        return "建议继续完善简历，提升技能后再进行岗位匹配";
    }

    private String getRating(Integer score) {
        if (score == null) return "未评估";
        if (score >= 90) return "优秀 (S)";
        if (score >= 80) return "良好 (A)";
        if (score >= 70) return "中等 (B)";
        if (score >= 60) return "合格 (C)";
        return "待提升 (D)";
    }

    private String getScoreBadge(Float score) {
        if (score == null) return "⚪ 未评分";
        if (score >= 85) return "🟢 优秀 (" + String.format("%.1f", score) + "分)";
        if (score >= 70) return "🔵 良好 (" + String.format("%.1f", score) + "分)";
        if (score >= 60) return "🟡 中等 (" + String.format("%.1f", score) + "分)";
        return " 待提升 (" + String.format("%.1f", score) + "分)";
    }

    private String generateScoreChart(List<?> jobMatches) {
        StringBuilder chart = new StringBuilder();

        chart.append("岗位匹配分数分布:\n\n");

        for (int i = 0; i < Math.min(5, jobMatches.size()); i++) {
            Map<?, ?> job = (Map<?, ?>) jobMatches.get(i);
            Float score = ((Number) job.get("totalScore")).floatValue();
            String position = (String) job.get("positionName");

            int barLength = (int) (score / 5);
            chart.append(String.format("%-20s [", position.length() > 20 ? position.substring(0, 20) : position));

            for (int j = 0; j < 20; j++) {
                if (j < barLength) {
                    chart.append("█");
                } else {
                    chart.append("░");
                }
            }

            chart.append(String.format("] %.1f\n", score));
        }

        return chart.toString();
    }

    private String generateTimeline(Map<?, ?> planning) {
        StringBuilder timeline = new StringBuilder();

        timeline.append("时间轴:\n\n");

        Object phasesObj = planning.get("phases");
        List<?> phases = phasesObj instanceof List ? (List<?>) phasesObj : null;
        
        if (phases != null) {
            int currentMonth = 0;
            for (Object phaseObj : phases) {
                if (phaseObj instanceof Map) {
                    Map<?, ?> phase = (Map<?, ?>) phaseObj;
                    Integer duration = null;
                    if (phase.get("duration") instanceof Integer) {
                        duration = (Integer) phase.get("duration");
                    }
                    String phaseName = phase.get("phaseName") != null ? phase.get("phaseName").toString() : "";

                    if (duration == null) duration = 1;

                    timeline.append("M").append(currentMonth + 1).append("-M").append(currentMonth + duration);
                    timeline.append(": ").append(phaseName).append("\n");

                    currentMonth += duration;
                }
            }
        }

        return timeline.toString();
    }

    private float calculateAverageScore(List<?> jobMatches) {
        if (jobMatches == null || jobMatches.isEmpty()) {
            return 0.0f;
        }

        float total = 0;
        for (Object job : jobMatches) {
            Map<?, ?> jobMap = (Map<?, ?>) job;
            Object score = jobMap.get("totalScore");
            if (score instanceof Float) {
                total += (Float) score;
            } else if (score instanceof Number) {
                total += ((Number) score).floatValue();
            }
        }

        return total / jobMatches.size();
    }

    private Float findHighestScore(List<?> jobMatches) {
        if (jobMatches == null || jobMatches.isEmpty()) {
            return 0.0f;
        }

        float max = 0.0f;
        for (Object job : jobMatches) {
            Map<?, ?> jobMap = (Map<?, ?>) job;
            Object score = jobMap.get("totalScore");
            if (score instanceof Float && (Float) score > max) {
                max = (Float) score;
            }
        }

        return max;
    }

    private Float findLowestScore(List<?> jobMatches) {
        if (jobMatches == null || jobMatches.isEmpty()) {
            return 0.0f;
        }

        float min = 100.0f;
        for (Object job : jobMatches) {
            Map<?, ?> jobMap = (Map<?, ?>) job;
            Object score = jobMap.get("totalScore");
            if (score instanceof Float && (Float) score < min) {
                min = (Float) score;
            }
        }

        return min;
    }

    private int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return text.length();
    }
}
