package com.career.plan.controller;

import com.career.plan.common.Result;
import com.career.plan.entity.User;
import com.career.plan.service.planTask.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Map<String, Object> result = userService.login(username, password);
        if (result != null) {
            return Result.success(result);
        }
        return Result.error(401, "用户名或密码错误");
    }

    @PostMapping("/register")
    public Result<Long> register(@RequestBody User user) {
        try {
            Long userId = userService.register(user);
            return Result.success(userId);
        } catch (Exception e) {
            return Result.error(500, "注册失败：" + e.getMessage());
        }
    }
}
