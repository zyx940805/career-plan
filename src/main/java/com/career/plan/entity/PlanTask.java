package com.career.plan.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "plan_task", autoResultMap = true)
public class PlanTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskId;
    private Long userId;
    private Integer status; // 0:进行中 1:成功 2:失败

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> finalReport;

    private String finalAnswer;
    private Integer qaScore;
    private Integer passCheck; // 0否, 1是
    private String summary;
    private String topRole;
    private Integer matchScore;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}