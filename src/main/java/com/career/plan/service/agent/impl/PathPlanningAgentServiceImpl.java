package com.career.plan.service.agent.impl;

import com.career.plan.dto.AgentDTO.PathPlanningRequest;
import com.career.plan.dto.AgentDTO.PathPlanningResponse;
import com.career.plan.dto.AgentDTO.Phase;
import com.career.plan.service.agent.PathPlanningAgentService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PathPlanningAgentServiceImpl implements PathPlanningAgentService {

    @Override
    public PathPlanningResponse generatePath(PathPlanningRequest request) {
        PathPlanningResponse response = new PathPlanningResponse();

        List<Phase> phases = generatePhases(request.getTargetPosition(), request.getCurrentProfile(), request.getGaps());
        response.setPhases(phases);

        int totalDuration = phases.stream().mapToInt(Phase::getDuration).sum();
        response.setTotalDuration(totalDuration);

        response.setSummary(generateSummary(request, phases));

        return response;
    }

    private List<Phase> generatePhases(String targetPosition, Object currentProfile, List<String> gaps) {
        List<Phase> phases = new ArrayList<>();

        Phase phase1 = new Phase();
        phase1.setPhaseName("基础夯实阶段");
        phase1.setDuration(2);
        phase1.setTasks(Arrays.asList(
                "系统学习核心技术",
                "完成基础项目练习",
                "建立知识体系"
        ));
        phase1.setGoals(Arrays.asList(
                "掌握 80% 的核心技能",
                "完成 2-3 个练手项目"
        ));
        phase1.setResources(Arrays.asList(
                "官方文档",
                "在线课程",
                "技术博客"
        ));
        phases.add(phase1);

        Phase phase2 = new Phase();
        phase2.setPhaseName("项目实战阶段");
        phase2.setDuration(3);
        phase2.setTasks(Arrays.asList(
                "参与实际项目",
                "积累项目经验",
                "解决复杂问题"
        ));
        phase2.setGoals(Arrays.asList(
                "完成 1-2 个完整项目",
                "形成项目作品集"
        ));
        phase2.setResources(Arrays.asList(
                "开源项目",
                "实习机会",
                "导师指导"
        ));
        phases.add(phase2);

        Phase phase3 = new Phase();
        phase3.setPhaseName("求职准备阶段");
        phase3.setDuration(1);
        phase3.setTasks(Arrays.asList(
                "优化简历",
                "刷面试题",
                "模拟面试"
        ));
        phase3.setGoals(Arrays.asList(
                "准备完善的简历",
                "掌握常见面试题",
                "获得 3+ 面试机会"
        ));
        phase3.setResources(Arrays.asList(
                "面试题库",
                "简历模板",
                "面试技巧文章"
        ));
        phases.add(phase3);

        return phases;
    }

    private String generateSummary(PathPlanningRequest request, List<Phase> phases) {
        return String.format("针对%s 岗位，制定%d 个月的成长计划，包含%d 个阶段。重点关注：%s",
                request.getTargetPosition(),
                phases.stream().mapToInt(Phase::getDuration).sum(),
                phases.size(),
                String.join(", ", request.getGaps() != null ? request.getGaps() : Arrays.asList("技能提升")));
    }
}
