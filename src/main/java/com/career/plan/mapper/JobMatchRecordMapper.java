package com.career.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.career.plan.entity.JobMatchRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 岗位匹配记录表 Mapper 接口
 */
@Mapper
public interface JobMatchRecordMapper extends BaseMapper<JobMatchRecord> {
}