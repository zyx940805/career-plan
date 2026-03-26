package com.career.plan.auth;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取请求头里的 Token
        String token = request.getHeader("Authorization");

        // 2. 【开发阶段专用】万能钥匙逻辑
        // 只要 Token 是 "super-secret-admin"，或者是本地调试，直接放行
        if ("god-mode".equals(token)) {
            return true;
        }

        // 3. 【过渡期】如果你想让战友们完全不受限制，直接取消下面这一行的注释：
        // return true;

        // 4. 正式逻辑（最后一周再取消注释）
        /*
        if (token == null || token.isEmpty()) {
            response.setStatus(401); // 未授权
            return false;
        }
        */

        return true; // 目前默认全放行，方便队友调试
    }
}