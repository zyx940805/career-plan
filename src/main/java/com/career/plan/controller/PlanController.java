package com.career.plan.controller;

import com.career.plan.common.Result;
import com.career.plan.dto.AgentDTO.*;
import com.career.plan.entity.PlanTask;
import com.career.plan.service.agent.*;
import com.career.plan.service.planTask.PlanTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 规划任务管理接口
 */
@RestController
@RequestMapping("/api/plan")
public class PlanController {

    @Autowired
    private PlanTaskService planTaskService;

    @Autowired
    private ParseAgentService parseAgentService;

    @Autowired
    private KeywordAgentService keywordAgentService;

    @Autowired
    private JobRetrievalAgentService jobRetrievalAgentService;

    @Autowired
    private JobProfileAgentService jobProfileAgentService;

    @Autowired
    private StudentProfileAgentService studentProfileAgentService;

    @Autowired
    private MatchAgentService matchAgentService;

    @Autowired
    private PathPlanningAgentService pathPlanningAgentService;

    @Autowired
    private ReportAgentService reportAgentService;

    @Autowired
    private QAAgentService qaAgentService;

    @Autowired
    private RepairAgentService repairAgentService;

    /**
     * 发起规划任务
     * POST /api/plan/start
     * 创建任务记录，异步调用综合规划流程，返回 task_id
     */
    @PostMapping("/start")
    public Result<Map<String, Object>> startPlan(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String resumeText = (String) request.get("resumeText");
            String userQuery = (String) request.get("userQuery");
            Map<String, Object> extraInfo = (Map<String, Object>) request.get("extraInfo");

            // 创建任务记录
            String taskId = planTaskService.startNewTask(userId);

            // 异步执行综合规划流程
            executePlanningFlowAsync(taskId, userId, resumeText, userQuery, extraInfo);

            Map<String, Object> result = new HashMap<>();
            result.put("taskId", taskId);
            result.put("status", "processing");
            result.put("message", "规划任务已启动，请稍后查询结果");

            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "启动规划任务失败：" + e.getMessage());
        }
    }

    /**
     * 获取规划结果
     * GET /api/plan/result?task_id=xxx
     */
    @GetMapping("/result")
    public Result<Map<String, Object>> getPlanResult(@RequestParam("task_id") String taskId) {
        try {
            PlanTask task = planTaskService.getOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PlanTask>()
                            .eq(PlanTask::getTaskId, taskId)
            );

            if (task == null) {
                return Result.error(404, "未找到该任务");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("taskId", task.getTaskId());
            result.put("status", getStatusText(task.getStatus()));
            result.put("userId", task.getUserId());
            result.put("createdAt", task.getCreatedAt());
            result.put("completedAt", task.getCompletedAt());

            // 根据状态返回不同内容
            if (task.getStatus() == 1 && task.getFinalReport() != null) {
                // 任务成功，返回完整报告
                result.put("finalReport", task.getFinalReport());
                result.put("finalAnswer", task.getFinalAnswer());
                result.put("qaScore", task.getQaScore());
                result.put("passCheck", task.getPassCheck());
                result.put("summary", task.getSummary());
                result.put("topRole", task.getTopRole());
                result.put("matchScore", task.getMatchScore());
            } else if (task.getStatus() == 2) {
                // 任务失败
                result.put("error", task.getFinalAnswer());
            } else {
                // 任务进行中
                result.put("message", "任务正在处理中，请稍后查询");
            }

            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "获取规划结果失败：" + e.getMessage());
        }
    }

    /**
     * 异步执行综合规划流程
     * 内部按工作流串联 Agent: parse → keyword → retrieval → job_profile →
     * student_profile → match → path_planning → report → qa → repair
     */
    @Async
    public void executePlanningFlowAsync(String taskId, Long userId, String resumeText,
                                         String userQuery, Map<String, Object> extraInfo) {
        try {
            // 1. Parse Input Agent - 解析用户输入
            ParseRequest parseRequest = new ParseRequest();
            parseRequest.setUserQuery(userQuery);
            parseRequest.setResumeText(resumeText);
            parseRequest.setExtraInfo(extraInfo);
            ParseResponse parseResponse = parseAgentService.parseInput(parseRequest);

            // 2. Extract Keyword Agent - 提取关键词
            KeywordRequest keywordRequest = new KeywordRequest();
            keywordRequest.setResumeText(resumeText);
            keywordRequest.setJobDescription(parseResponse.getIntent());
            KeywordResponse keywordResponse = keywordAgentService.extractKeywords(keywordRequest);

            // 3. Job Retrieval Agent - 检索岗位
            java.util.List<JobRecord> jobs = jobRetrievalAgentService.retrieveJobs(
                    keywordResponse.getTargetPosition(), null, 10
            );

            // 4. Job Profile Agent - 生成岗位画像
            JobProfile jobProfile = null;
            if (!jobs.isEmpty()) {
                jobProfile = jobProfileAgentService.analyzeJobProfile(jobs.get(0));
            }

            // 5. Student Profile Agent - 生成学生画像
            StudentProfileResponse studentProfile = studentProfileAgentService
                    .createProfileFromResume(resumeText);

            // 6. Match Agent - 人岗匹配分析
            MatchRequest matchRequest = new MatchRequest();
            matchRequest.setStudentProfile(convertToMap(studentProfile));
            matchRequest.setJobProfile(convertToMap(jobProfile));
            MatchResponse matchResponse = matchAgentService.calculateMatch(
                    convertToMap(studentProfile),
                    convertToMap(jobProfile)
            );

            // 7. Path Planning Agent - 规划职业发展路径
            PathPlanningRequest pathRequest = new PathPlanningRequest();
            pathRequest.setTargetPosition(keywordResponse.getTargetPosition());
            pathRequest.setCurrentProfile(convertToMap(studentProfile));
            pathRequest.setGaps(matchResponse.getGaps());
            PathPlanningResponse pathResponse = pathPlanningAgentService.generatePath(pathRequest);

            // 8. Report Agent - 生成完整报告
            ReportRequest reportRequest = new ReportRequest();
            reportRequest.setTaskId(taskId);
            reportRequest.setStudentProfile(convertToMap(studentProfile));
            reportRequest.setJobMatches(convertToJobMatchesList(matchResponse));
            reportRequest.setPathPlanning(convertToMap(pathResponse));
            reportRequest.setMatchAnalysis(convertToMap(matchResponse));
            reportRequest.setReportType("markdown");
            reportRequest.setIncludeCharts(true);
            reportRequest.setTemplate("professional");
            ReportResponse reportResponse = reportAgentService.generateReport(reportRequest);

            // 9. QA Agent - 质量复核
            QARequest qaRequest = new QARequest();
            qaRequest.setReport(convertToMap(reportResponse));
            QAResponse qaResponse = qaAgentService.qualityCheck(qaRequest);

            // 10. Repair Agent - 修复报告（如果需要）
            RepairResponse repairResponse = null;
            if (!qaResponse.getPassed() && qaResponse.getIssues() != null && !qaResponse.getIssues().isEmpty()) {
                RepairRequest repairRequest = new RepairRequest();
                repairRequest.setOriginalReport(convertToMap(reportResponse));
                repairRequest.setIssues(qaResponse.getIssues());
                repairResponse = repairAgentService.repairReport(repairRequest);
            }

            // 更新任务状态为成功
            updateTaskSuccess(taskId, reportResponse, matchResponse, qaResponse, studentProfile);

        } catch (Exception e) {
            // 更新任务状态为失败
            updateTaskFailure(taskId, e.getMessage());
        }
    }

    /**
     * 更新任务为成功状态
     */
    private void updateTaskSuccess(String taskId, ReportResponse reportResponse,
                                   MatchResponse matchResponse, QAResponse qaResponse,
                                   StudentProfileResponse studentProfile) {
        PlanTask task = planTaskService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PlanTask>()
                        .eq(PlanTask::getTaskId, taskId)
        );

        if (task != null) {
            task.setStatus(1); // 成功
            task.setFinalReport(convertToMap(reportResponse));
            task.setFinalAnswer(reportResponse.getSummary());
            task.setQaScore(qaResponse.getScore());
            task.setPassCheck(qaResponse.getPassed() ? 1 : 0);
            task.setSummary(reportResponse.getSummary());
            task.setTopRole(reportResponse.getTopRecommendation());
            task.setMatchScore(Math.round(matchResponse.getTotalScore()));
            task.setCompletedAt(java.time.LocalDateTime.now());
            planTaskService.updateById(task);
        }
    }

    /**
     * 更新任务为失败状态
     */
    private void updateTaskFailure(String taskId, String errorMsg) {
        PlanTask task = planTaskService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PlanTask>()
                        .eq(PlanTask::getTaskId, taskId)
        );

        if (task != null) {
            task.setStatus(2); // 失败
            task.setFinalAnswer(errorMsg);
            task.setCompletedAt(java.time.LocalDateTime.now());
            planTaskService.updateById(task);
        }
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "进行中";
            case 1: return "成功";
            case 2: return "失败";
            default: return "未知";
        }
    }

    /**
     * 辅助方法：将对象转换为 Map
     */
    @SuppressWarnings("unchecked")
    private <T> Map<String, Object> convertToMap(T obj) {
        if (obj == null) return new HashMap<>();

        Map<String, Object> map = new HashMap<>();
        if (obj instanceof Map) {
            map.putAll((Map<String, Object>) obj);
        } else {
            // 对于 DTO 对象，使用反射获取属性
            try {
                Class<?> clazz = obj.getClass();
                java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    map.put(field.getName(), value);
                }
            } catch (Exception e) {
                // 忽略反射异常
            }
        }
        return map;
    }

    /**
     * 辅助方法：转换岗位匹配列表
     */
    private List<Object> convertToJobMatchesList(MatchResponse matchResponse) {
        List<Object> jobs = new ArrayList<>();
        
        Map<String, Object> job = new HashMap<>();
        job.put("positionName", "推荐岗位");
        job.put("totalScore", matchResponse.getTotalScore());
        job.put("dimensionScores", matchResponse.getDimensionScores());
        job.put("strengths", matchResponse.getStrengths());
        job.put("gaps", matchResponse.getGaps());
        job.put("actionSuggestions", matchResponse.getActionSuggestions());
        jobs.add(job);
        
        return jobs;
    }
}
