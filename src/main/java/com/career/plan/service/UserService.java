package com.career.plan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.career.plan.entity.*;
import java.util.List;
import java.util.Map;

/* -------------------------------------------------------------------------- */
/* 用户业务接口                                 */
/* -------------------------------------------------------------------------- */
public interface UserService extends IService<User> {
    Map<String, Object> login(String username, String password);
    Long register(User user);
}

