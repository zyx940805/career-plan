package com.career.plan.service.planTask.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.career.plan.entity.*;
import com.career.plan.mapper.*;
import com.career.plan.service.planTask.PlanTaskService;
import com.career.plan.service.planTask.ProfileService;
import com.career.plan.service.planTask.UserService;
import com.career.plan.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

/* -------------------------------------------------------------------------- */
/* UserService 实现                              */
/* -------------------------------------------------------------------------- */
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

        String token = jwtUtils.createToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        return result;
    }

    @Override
    public Long register(User user) {
        user.setCreatedAt(LocalDateTime.now());
        this.save(user);
        return user.getId();
    }
}

