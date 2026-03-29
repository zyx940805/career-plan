package com.career.plan.controller;

import com.career.plan.common.Result;
import com.career.plan.dto.AgentDTO.*;
import com.career.plan.service.agent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/agent")
public class AgentController {

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

    @PostMapping("/parse")
    public Result<ParseResponse> parseInput(@RequestBody ParseRequest request) {
        ParseResponse response = parseAgentService.parseInput(request);
        return Result.success(response);
    }

    @PostMapping("/extract_keyword")
    public Result<KeywordResponse> extractKeyword(@RequestBody KeywordRequest request) {
        KeywordResponse response = keywordAgentService.extractKeywords(request);
        return Result.success(response);
    }

    @PostMapping("/job_retrieval")
    public Result<List<JobRecord>> jobRetrieval(@RequestBody Map<String, String> request) {
        String keyword = request.get("keyword");
        String location = request.get("location");
        String limitStr = request.get("limit");
        Integer limit = limitStr != null ? Integer.parseInt(limitStr) : 10;
        
        List<JobRecord> jobs = jobRetrievalAgentService.retrieveJobs(keyword, location, limit);
        return Result.success(jobs);
    }

    @PostMapping("/job_profile")
    public Result<JobProfile> jobProfile(@RequestBody JobRecord request) {
        JobProfile profile = jobProfileAgentService.analyzeJobProfile(request);
        return Result.success(profile);
    }

    @PostMapping("/student_profile")
    public Result<StudentProfileResponse> studentProfile(@RequestBody Map<String, Object> request) {
        String resumeText = (String) request.get("resumeText");
        
        StudentProfileResponse response;
        if (resumeText != null && !resumeText.isEmpty()) {
            response = studentProfileAgentService.createProfileFromResume(resumeText);
        } else {
            response = new StudentProfileResponse();
            response.setCompletenessScore(0);
            response.setCompetitivenessScore(0);
        }
        
        return Result.success(response);
    }

    @PostMapping("/match")
    public Result<MatchResponse> match(@RequestBody MatchRequest request) {
        MatchResponse response = matchAgentService.calculateMatch(
            request.getStudentProfile(), 
            request.getJobProfile()
        );
        return Result.success(response);
    }

    @PostMapping("/path_planning")
    public Result<PathPlanningResponse> pathPlanning(@RequestBody PathPlanningRequest request) {
        PathPlanningResponse response = pathPlanningAgentService.generatePath(request);
        return Result.success(response);
    }

    @PostMapping("/report")
    public Result<ReportResponse> generateReport(@RequestBody ReportRequest request) {
        if (request.getReportType() == null) {
            request.setReportType("markdown");
        }
        if (request.getIncludeCharts() == null) {
            request.setIncludeCharts(true);
        }
        if (request.getTemplate() == null) {
            request.setTemplate("professional");
        }
        
        ReportResponse response = reportAgentService.generateReport(request);
        return Result.success(response);
    }

    @PostMapping("/qa")
    public Result<QAResponse> qaCheck(@RequestBody QARequest request) {
        QAResponse response = qaAgentService.qualityCheck(request);
        return Result.success(response);
    }

    @PostMapping("/repair")
    public Result<RepairResponse> repairReport(@RequestBody RepairRequest request) {
        RepairResponse response = repairAgentService.repairReport(request);
        return Result.success(response);
    }
}