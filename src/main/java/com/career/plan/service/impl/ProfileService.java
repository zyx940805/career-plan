package com.career.plan.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.career.plan.entity.StudentProfile;

// 2. 档案相关业务
public interface ProfileService extends IService<StudentProfile> {
    StudentProfile getByUserId(Long userId);
    boolean saveOrUpdateProfile(StudentProfile profile);
}
