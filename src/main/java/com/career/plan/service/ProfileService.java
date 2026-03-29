package com.career.plan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.career.plan.entity.StudentProfile;

/* -------------------------------------------------------------------------- */
/* 学生档案接口                                 */
/* -------------------------------------------------------------------------- */
public interface ProfileService extends IService<StudentProfile> {
    StudentProfile getByUserId(Long userId);
    boolean saveOrUpdateProfile(StudentProfile profile);
}
