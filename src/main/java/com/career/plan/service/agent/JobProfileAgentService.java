package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.JobProfile;
import com.career.plan.dto.AgentDTO.JobRecord;

public interface JobProfileAgentService {
    JobProfile analyzeJobProfile(JobRecord jobRecord);
}
