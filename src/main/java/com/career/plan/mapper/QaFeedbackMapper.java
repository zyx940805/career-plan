package com.career.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.career.plan.entity.QaFeedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问答/反馈表 Mapper 接口
 */
@Mapper
public interface QaFeedbackMapper extends BaseMapper<QaFeedback> {
}