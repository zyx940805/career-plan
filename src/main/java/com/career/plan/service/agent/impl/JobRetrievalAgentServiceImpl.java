package com.career.plan.service.agent.impl;

import com.career.plan.dto.AgentDTO.JobRecord;
import com.career.plan.service.agent.JobRetrievalAgentService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobRetrievalAgentServiceImpl implements JobRetrievalAgentService {

    private static final List<JobRecord> MOCK_JOBS = new ArrayList<>();

    static {
        initMockJobs();
    }

    private static void initMockJobs() {
        MOCK_JOBS.add(createJob("后端开发工程师", "阿里巴巴", "杭州", "15-30K",
                "负责后端系统设计与开发",
                Arrays.asList("Java", "Spring Boot", "MySQL"),
                Arrays.asList("Redis", "Docker")));

        MOCK_JOBS.add(createJob("Java 开发工程师", "腾讯", "深圳", "18-35K",
                "参与核心业务开发",
                Arrays.asList("Java", "Spring Cloud", "MySQL"),
                Arrays.asList("Kubernetes", "微服务")));

        MOCK_JOBS.add(createJob("高级后端工程师", "字节跳动", "北京", "25-50K",
                "负责高并发系统设计",
                Arrays.asList("Java", "Go", "Redis", "MySQL"),
                Arrays.asList("Kafka", "Kubernetes")));

        MOCK_JOBS.add(createJob("全栈开发工程师", "美团", "上海", "20-40K",
                "前后端全栈开发",
                Arrays.asList("Java", "Vue", "MySQL"),
                Arrays.asList("Docker", "Git")));

        MOCK_JOBS.add(createJob("Python 开发工程师", "百度", "北京", "16-32K",
                "参与 AI 平台开发",
                Arrays.asList("Python", "Django", "MySQL"),
                Arrays.asList("机器学习", "数据分析")));
    }

    private static JobRecord createJob(String position, String company, String location,
                                       String salary, String description,
                                       List<String> required, List<String> preferred) {
        JobRecord job = new JobRecord();
        job.setPositionName(position);
        job.setCompanyName(company);
        job.setLocation(location);
        job.setSalaryRange(salary);
        job.setJobDescription(description);
        job.setRequiredSkills(required);
        job.setPreferredSkills(preferred);
        return job;
    }

    @Override
    public List<JobRecord> retrieveJobs(String keyword, String location, Integer limit) {
        List<JobRecord> results = new ArrayList<>();

        for (JobRecord job : MOCK_JOBS) {
            boolean matchKeyword = keyword == null ||
                    job.getPositionName().contains(keyword) ||
                    job.getRequiredSkills().stream().anyMatch(s -> s.contains(keyword));

            boolean matchLocation = location == null || job.getLocation().equals(location);

            if (matchKeyword && matchLocation) {
                results.add(job);
            }

            if (results.size() >= (limit != null ? limit : 10)) {
                break;
            }
        }

        return results;
    }
}
