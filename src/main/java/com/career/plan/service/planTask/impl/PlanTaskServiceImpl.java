package com.career.plan.service.planTask.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.career.plan.entity.PlanTask;
import com.career.plan.mapper.PlanTaskMapper;
import com.career.plan.service.planTask.PlanTaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/* -------------------------------------------------------------------------- */
/* PlanTaskService 实现                            */
/* -------------------------------------------------------------------------- */
@Service
public class PlanTaskServiceImpl extends ServiceImpl<PlanTaskMapper, PlanTask> implements PlanTaskService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startNewTask(Long userId) {
        String taskId = "TASK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        PlanTask task = new PlanTask();
        task.setTaskId(taskId);
        task.setUserId(userId);
        task.setStatus(0);
        task.setCreatedAt(LocalDateTime.now());
        this.save(task);
        return taskId;
    }

    @Override
    public List<PlanTask> getUserHistory(Long userId) {
        return this.list(new LambdaQueryWrapper<PlanTask>()
                .eq(PlanTask::getUserId, userId)
                .orderByDesc(PlanTask::getCreatedAt));
    }
}
