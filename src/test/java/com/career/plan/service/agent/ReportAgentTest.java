package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.ReportRequest;
import com.career.plan.dto.AgentDTO.ReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.util.*;

@SpringBootTest
public class ReportAgentTest {

    @Autowired
    private ReportAgentService reportAgentService;

    @Test
    public void testGenerateReport() {
        ReportRequest request = new ReportRequest();
        request.setTaskId("TASK_TEST_001");
        request.setReportType("markdown");

        Map<String, Object> studentProfile = createStudentProfile();
        request.setStudentProfile(studentProfile);

        List<Object> jobMatches = createJobMatches();
        request.setJobMatches(jobMatches);

        Map<String, Object> pathPlanning = createPathPlanning();
        request.setPathPlanning(pathPlanning);

        ReportResponse response = reportAgentService.generateReport(request);

        System.out.println("=== 报告生成测试 ===");
        System.out.println("任务 ID: " + response.getTaskId());
        System.out.println("摘要：" + response.getSummary());
        System.out.println("推荐：" + response.getTopRecommendation());
        System.out.println("\nMarkdown 预览:");
        System.out.println(response.getMarkdown().substring(0, Math.min(500, response.getMarkdown().length())));
    }

    private Map<String, Object> createStudentProfile() {
        Map<String, Object> profile = new HashMap<>();

        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("education", "本科");
        basicInfo.put("major", "计算机科学与技术");
        basicInfo.put("graduationYear", "2025");
        profile.put("basicInfo", basicInfo);

        profile.put("hardSkills", Arrays.asList("Java", "Spring Boot", "MySQL", "Redis"));
        profile.put("softSkills", Arrays.asList("沟通", "协作", "学习能力强"));
        profile.put("internshipExperience", "6 个月后端开发实习");
        profile.put("completenessScore", 85);
        profile.put("competitivenessScore", 78);

        return profile;
    }

    private List<Object> createJobMatches() {
        List<Object> jobs = new ArrayList<>();

        Map<String, Object> job1 = new HashMap<>();
        job1.put("positionName", "后端开发工程师");
        job1.put("companyName", "阿里巴巴");
        job1.put("totalScore", 85.5f);
        job1.put("dimensionScores", createDimensionScores(85, 80, 90, 85));
        job1.put("strengths", Arrays.asList("技能匹配度高", "有相关实习经验"));
        job1.put("gaps", Arrays.asList("缺少分布式系统经验"));
        jobs.add(job1);

        Map<String, Object> job2 = new HashMap<>();
        job2.put("positionName", "Java 开发工程师");
        job2.put("companyName", "腾讯");
        job2.put("totalScore", 78.2f);
        job2.put("dimensionScores", createDimensionScores(75, 70, 85, 80));
        job2.put("strengths", Arrays.asList("基础扎实"));
        job2.put("gaps", Arrays.asList("项目经验不足", "缺少微服务经验"));
        jobs.add(job2);

        return jobs;
    }

    private Map<String, Float> createDimensionScores(float... scores) {
        Map<String, Float> dimensionScores = new HashMap<>();
        dimensionScores.put("技能匹配", scores[0]);
        dimensionScores.put("经验匹配", scores[1]);
        dimensionScores.put("教育匹配", scores[2]);
        dimensionScores.put("软技能匹配", scores[3]);
        return dimensionScores;
    }

    private Map<String, Object> createPathPlanning() {
        Map<String, Object> planning = new HashMap<>();
        planning.put("totalDuration", 6);

        List<Map<String, Object>> phases = new ArrayList<>();

        Map<String, Object> phase1 = new HashMap<>();
        phase1.put("phaseName", "基础夯实");
        phase1.put("duration", 2);
        phase1.put("tasks", Arrays.asList("学习核心技术", "完成练手项目"));
        phase1.put("goals", Arrays.asList("掌握 80% 核心技能"));
        phase1.put("resources", Arrays.asList("官方文档", "在线课程"));
        phases.add(phase1);

        Map<String, Object> phase2 = new HashMap<>();
        phase2.put("phaseName", "项目实战");
        phase2.put("duration", 3);
        phase2.put("tasks", Arrays.asList("参与实际项目", "积累经验"));
        phase2.put("goals", Arrays.asList("完成 1-2 个完整项目"));
        phase2.put("resources", Arrays.asList("开源项目", "实习机会"));
        phases.add(phase2);

        planning.put("phases", phases);

        return planning;
    }
}
