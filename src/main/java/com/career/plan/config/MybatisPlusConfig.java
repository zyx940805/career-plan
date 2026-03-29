package com.career.plan.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
    // 分页插件暂时禁用，避免版本兼容问题
    // 后续如需使用分页，请升级 mybatis-plus 到 3.5.9+ 并添加 mybatis-plus-jsqlparser 依赖
}