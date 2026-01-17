package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import java.util.Map;

@RestController
@RequestMapping("/api") // 保持与 LoginController 一致的 /api 基础路径
public class LogoutController {

    // ----------------------------------------------------------------
    // [!!] 退出登录逻辑
    // ----------------------------------------------------------------
    /**
     * 退出登录
     * @param session Spring自动注入的会话
     * @param response 用于清除Cookie
     * @return 包含成功信息的Map
     */
    @PostMapping("/logout")
    public Map<String, Object> handleLogout(HttpSession session, HttpServletResponse response) {
        // 1. 使 Session 失效
        session.invalidate();

        // 2. [!!] 清除前端设置的 "记住我" Cookie
        clearCookie(response, "remember-user");
        clearCookie(response, "remember-pass");

        return Map.of("success", true, "message", "已成功退出登录");
    }

    /**
     * [!!] 辅助方法：清除指定名称的 Cookie
     */
    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0); // 立即过期
        cookie.setPath("/"); // 确保在全站路径清除
        // cookie.setHttpOnly(true); // 如果原来设为true，这里也要设为true
        response.addCookie(cookie);
    }
}
