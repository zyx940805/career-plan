package com.career.plan.service.planTask.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.career.plan.entity.StudentProfile;
import com.career.plan.mapper.StudentProfileMapper;
import com.career.plan.service.planTask.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/* -------------------------------------------------------------------------- */
/* ProfileService 实现                            */
/* -------------------------------------------------------------------------- */
@Service
public class ProfileServiceImpl extends ServiceImpl<StudentProfileMapper, StudentProfile> implements ProfileService {
    @Override
    public StudentProfile getByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateProfile(StudentProfile profile) {
        profile.setUpdatedAt(LocalDateTime.now());
        return this.saveOrUpdate(profile);
    }
}
