package com.career.plan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.career.plan.entity.PlanTask;

import java.util.List;

/* -------------------------------------------------------------------------- */
/* 规划任务接口                                 */
/* -------------------------------------------------------------------------- */
public interface PlanTaskService extends IService<PlanTask> {
    String startNewTask(Long userId);
    List<PlanTask> getUserHistory(Long userId);
}
