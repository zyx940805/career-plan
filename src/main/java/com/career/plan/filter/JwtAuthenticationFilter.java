package com.career.plan.filter;

import com.career.plan.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 登录认证过滤器
 * 负责拦截请求并校验 Token 有效性
 * 已适配 Jakarta EE (Spring Boot 3.x+)
 */
@Component
public class JwtAuthenticationFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 不需要拦截的白名单路径（如登录、注册）
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/user/login",
            "/api/user/register"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        // 1. 白名单直接放行
        if (WHITE_LIST.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 获取请求头中的 Authorization
        String authHeader = httpRequest.getHeader("Authorization");

        // 3. 校验 Token 格式 (Bearer <token>)
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // TODO: 这里调用你的 JwtUtils 校验 token 是否合法/过期
            boolean isValid = validateToken(token);

            if (isValid) {
                // 校验成功，放行
                chain.doFilter(request, response);
                return;
            }
        }

        // 4. 校验失败，返回统一结果 Result.error
        renderErrorResponse(httpResponse, 401, "Unauthorized: 请先登录");
    }

    /**
     * 模拟 Token 校验逻辑
     * 实际开发中请配合 jjwt 或 auth0 库实现
     */
    private boolean validateToken(String token) {
        // 演示逻辑：非空即认为有效
        return StringUtils.hasText(token);
    }

    /**
     * 使用 Result 类渲染 JSON 错误响应
     */
    private void renderErrorResponse(HttpServletResponse response, int code, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);

        Result<Object> result = Result.error(code, msg);
        String json = objectMapper.writeValueAsString(result);

        response.getWriter().write(json);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}