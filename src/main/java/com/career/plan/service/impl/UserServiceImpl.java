package com.career.plan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.career.plan.entity.User;
import com.career.plan.entity.StudentProfile;
import com.career.plan.entity.PlanTask;
import com.career.plan.mapper.UserMapper;
import com.career.plan.mapper.StudentProfileMapper;
import com.career.plan.mapper.PlanTaskMapper;
import com.career.plan.service.impl.UserService;
import com.career.plan.service.impl.ProfileService;
import com.career.plan.service.impl.PlanTaskService;
import com.career.plan.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;

/**
 * 业务实现类整合
 * 注意：为了解决 IDE 报错，建议将这些类分别放入独立文件。
 * 若必须放在一起，请确保包路径和导入完全正确。
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Map<String, Object> login(String username, String password) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getPassword, password));

        if (user == null) return null;

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("access_token", token);
        result.put("user_id", user.getId());
        result.put("username", user.getUsername());
        return result;
    }

    @Override
    public Long register(User user) {
        // 对应接口文档 3.1，保存用户信息
        this.save(user);
        return user.getId();
    }
}

/**
 * 档案业务实现
 * 注意：由于 Java 限制，一个文件只能有一个 public 类。
 * 如果报错持续，请将下方类移动至 ProfileServiceImpl.java
 */
@Service
class ProfileServiceImpl extends ServiceImpl<StudentProfileMapper, StudentProfile> implements ProfileService {
    @Override
    public StudentProfile getByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, userId));
    }

    @Override
    public boolean saveOrUpdateProfile(StudentProfile profile) {
        profile.setUpdatedAt(LocalDateTime.now());
        return this.saveOrUpdate(profile);
    }
}

/**
 * 任务业务实现
 * 如果报错持续，请将下方类移动至 PlanTaskServiceImpl.java
 */
@Service
class PlanTaskServiceImpl extends ServiceImpl<PlanTaskMapper, PlanTask> implements PlanTaskService {
    @Override
    public String startNewTask(Long userId) {
        String taskId = "TASK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        PlanTask task = new PlanTask();
        task.setTaskId(taskId);
        task.setUserId(userId);
        task.setStatus(0); // 0: 进行中
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