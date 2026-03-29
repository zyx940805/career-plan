package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.JobRecord;

import java.util.List;

public interface JobRetrievalAgentService {
    List<JobRecord> retrieveJobs(String keyword, String location, Integer limit);
}
