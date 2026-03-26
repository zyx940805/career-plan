package com.career.plan.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "job_match_record", autoResultMap = true)
public class JobMatchRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskId;
    private String positionName;
    private Float totalScore;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> dimensionScores;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> strengths;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> gaps;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> actionSuggestions;
}