package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public static final String UPLOAD_PATH = "D:/rear/images/";
    public static final String ACCESS_PATH_PATTERN = "/images/**";

    //提交测试

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(ACCESS_PATH_PATTERN)
                .addResourceLocations("file:" + UPLOAD_PATH);
    }
}