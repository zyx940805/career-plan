package com.career.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.career.plan.entity.PlanTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 规划任务表 Mapper 接口
 * 对应数据库表 plan_task
 */
@Mapper
public interface PlanTaskMapper extends BaseMapper<PlanTask> {
    // 继承 BaseMapper 后，MyBatis Plus 会自动提供常用的增删改查方法
}