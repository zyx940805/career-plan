package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.PathPlanningRequest;
import com.career.plan.dto.AgentDTO.PathPlanningResponse;

public interface PathPlanningAgentService {
    PathPlanningResponse generatePath(PathPlanningRequest request);
}
