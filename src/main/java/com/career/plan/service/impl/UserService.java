package com.career.plan.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.career.plan.entity.User;
import com.career.plan.entity.StudentProfile;
import com.career.plan.entity.PlanTask;
import java.util.Map;
import java.util.List;

/**
 * 整个项目的核心业务接口整合
 */

// 1. 用户相关业务
public interface UserService extends IService<User> {
    Map<String, Object> login(String username, String password);
    Long register(User user);
}

