package com.career.plan.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "student_profile", autoResultMap = true)
public class StudentProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String resumeText;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> basicInfo;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> hardSkills;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> softSkills;

    private String internshipExperience;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> employmentIntent;

    private Integer completenessScore;
    private Integer competitivenessScore;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> missingFields;

    private LocalDateTime updatedAt;
}