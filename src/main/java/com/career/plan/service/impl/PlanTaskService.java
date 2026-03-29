package com.career.plan.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.career.plan.entity.PlanTask;

import java.util.List;

// 3. 规划任务相关业务
public interface PlanTaskService extends IService<PlanTask> {
    String startNewTask(Long userId);
    List<PlanTask> getUserHistory(Long userId);
}
