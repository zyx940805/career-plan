package com.career.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.career.plan.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生档案表 Mapper 接口
 */
@Mapper
public interface StudentProfileMapper extends BaseMapper<StudentProfile> {
}