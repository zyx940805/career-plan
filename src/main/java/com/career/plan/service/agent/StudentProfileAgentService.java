package com.career.plan.service.agent;

import com.career.plan.dto.AgentDTO.StudentProfileResponse;
import com.career.plan.entity.StudentProfile;

public interface StudentProfileAgentService {
    StudentProfileResponse analyzeStudentProfile(StudentProfile profile);
    StudentProfileResponse createProfileFromResume(String resumeText);
}
