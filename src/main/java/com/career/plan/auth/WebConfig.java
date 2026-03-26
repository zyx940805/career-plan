package com.career.plan.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册你的拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")    // 拦截所有路径
                .excludePathPatterns("/api/user/login", "/api/user/register"); // 排除登录注册
    }
}