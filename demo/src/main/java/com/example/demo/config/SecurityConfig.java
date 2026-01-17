package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 开启 CORS，并告诉它去使用 corsConfigurationSource() 这个 Bean 的配置
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. 关闭 CSRF (前后端分离必须关)
                .csrf(csrf -> csrf.disable())

                // 3. 配置路径权限
                .authorizeHttpRequests(auth -> auth
                        // 允许所有人访问图片 (必须与 WebConfig 中的路径一致)
                        .requestMatchers("/images/**").permitAll()
                        // 允许所有人访问 API (登录、注册等)
                        // 如果你想更严格，可以只放行 "/api/login", "/api/register"
                        .requestMatchers("/api/**").permitAll()
                        // 其他请求默认允许 (因为你是自己写登录逻辑，不是用 Spring Security 的拦截)
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    // 4. 定义 CORS 配置源 (替代 WebConfig 里的 addCorsMappings)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的前端地址
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // 允许的方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 允许的头信息
        configuration.setAllowedHeaders(List.of("*"));

        // 允许携带凭证 (Cookie)
        configuration.setAllowCredentials(true);

        // 注册配置：对所有路径生效
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 5. 密码加密工具
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}