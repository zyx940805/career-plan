package com.career.plan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("qa_feedback")
public class QaFeedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String taskId;
    private String question;
    private String answer;
    private LocalDateTime createdAt;
}