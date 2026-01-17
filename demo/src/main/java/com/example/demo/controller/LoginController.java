package com.example.demo.controller;

import com.example.demo.model.Admin;
import com.example.demo.model.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Map<String, Object> handleLogin(@RequestBody Map<String, String> credentials) {
        // 移除 HttpServletResponse response, HttpSession session 参数

        String username = credentials.get("username");
        String password = credentials.get("password");

        Admin adminUser = adminService.login(username, password);

        if (adminUser != null) {
            // [修改] 生成 Token
            String token = jwtUtils.generateToken(adminUser.getUsername(), adminUser.getId());

            adminUser.setPassword(null);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登录成功！");
            result.put("token", token); // [关键] 返回 Token
            result.put("userData", adminUser);
            return result;
        } else {
            // ... 失败逻辑不变
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }
    }

    // [修改] 替代原有的 session-status，改为获取当前用户信息的接口
    @GetMapping("/user/me")
    public Map<String, Object> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();

        // 由于有 Filter 拦截，能进到这里说明 Token 基本是有效的
        // 你可以通过 SecurityContextHolder 获取用户信息
        // 或者简单解析 Token

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtils.validateToken(token)) {
                // 这里为了简单直接返回成功，实际可以根据 token 里的 username 查数据库
                result.put("loggedIn", true);
                result.put("username", jwtUtils.getUsernameFromToken(token));
                return result;
            }
        }

        result.put("loggedIn", false);
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> handleRegister(@RequestBody Admin admin) {
        // ... 注册逻辑不需要修改，因为注册本来就是校验 username 是否存在的 ...
        Map<String, Object> result = new HashMap<>();
        try {
            if (admin.getUsername() == null || admin.getUsername().isEmpty()) {
                result.put("success", false);
                result.put("message", "用户名不能为空");
                return result;
            }
            if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
                result.put("success", false);
                result.put("message", "密码不能为空");
                return result;
            }
            boolean success = adminService.register(admin);
            if (success) {
                result.put("success", true);
                result.put("message", "注册成功，请前往登录");
            } else {
                result.put("success", false);
                result.put("message", "注册失败，该用户名已存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/user/register")
    public Map<String, Object> handleUserRegister(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 基础校验
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                result.put("success", false);
                result.put("message", "用户名不能为空");
                return result;
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                result.put("success", false);
                result.put("message", "密码不能为空");
                return result;
            }

            // 2. [修改] 调用 Service，接收返回的错误消息
            String errorMessage = userService.register(user);

            // 如果 errorMessage 是 null，说明成功；否则说明有错误
            if (errorMessage == null) {
                result.put("success", true);
                result.put("message", "注册成功，欢迎加入！");
            } else {
                result.put("success", false);
                result.put("message", errorMessage); // 这里会显示"手机号已被绑定"
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }

        return result;
    }

    @PostMapping("/reset-password")
    public Map<String, Object> handleResetPassword(@RequestBody Map<String, String> data) {
        Map<String, Object> result = new HashMap<>();

        String username = data.get("username");
        String phone = data.get("phone");          // [新增] 获取手机号
        String newPassword = data.get("newPassword");

        // 1. 基础非空校验
        if (username == null || username.isEmpty() ||
                phone == null || phone.isEmpty() ||
                newPassword == null || newPassword.isEmpty()) {
            result.put("success", false);
            result.put("message", "请填写完整信息（用户名、手机号、新密码）");
            return result;
        }

        try {
            // 2. 调用带有手机号校验的 Service 方法
            boolean success = adminService.verifyAndResetPassword(username, phone, newPassword);

            if (success) {
                result.put("success", true);
                result.put("message", "验证通过，密码重置成功！");
            } else {
                result.put("success", false);
                result.put("message", "账号与手机号不匹配，或用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }

        return result;
    }

    @PostMapping("/user/reset-password")
    public Map<String, Object> handleUserResetPassword(@RequestBody Map<String, String> data) {
        Map<String, Object> result = new HashMap<>();

        String username = data.get("username");
        String phone = data.get("phone");          // 手机号
        String newPassword = data.get("newPassword");

        // 1. 基础非空校验
        if (username == null || username.isEmpty() ||
                phone == null || phone.isEmpty() ||
                newPassword == null || newPassword.isEmpty()) {
            result.put("success", false);
            result.put("message", "请填写完整信息（用户名、手机号、新密码）");
            return result;
        }

        try {
            // 2. 调用 UserService
            boolean success = userService.verifyAndResetPassword(username, phone, newPassword);

            if (success) {
                result.put("success", true);
                result.put("message", "密码重置成功，请使用新密码登录");
            } else {
                result.put("success", false);
                result.put("message", "账号与预留手机号不匹配，或用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }

        return result;
    }


    private void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}