package com.second.hand.trading.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*") // 允许所有本地端口
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 允许的方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许发送Cookie
                .maxAge(3600); // 预检请求的缓存时间
    }
}

