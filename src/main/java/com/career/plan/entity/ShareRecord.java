package com.career.plan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("share_record")
public class ShareRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskId;
    private String shareType; // pdf / card / link
    private String shareUrl;
    private LocalDateTime createdAt;
}
