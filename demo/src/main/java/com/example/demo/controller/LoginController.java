package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        String username = credentials.get("username");
        String password = credentials.get("password");

        // 1. 验证账号密码
        Admin adminUser = adminService.login(username, password);

        if (adminUser != null) {
            // 2. 生成 JWT Token
            String token = jwtUtils.generateToken(adminUser.getUsername(), adminUser.getId());

            // 3. 擦除密码，准备返回数据
            adminUser.setPassword(null);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登录成功！");
            result.put("token", token); // [关键] 返回 Token 给前端存入 localStorage
            result.put("userData", adminUser); // 返回用户信息，方便前端展示名字头像

            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }
    }

    /**
     * [修改] 获取当前用户信息接口 (替代原有的 session-status)
     * 前端 checkSession 会调用此接口，需在 Header 带上 Authorization: Bearer <token>
     */
    @GetMapping("/user/me")
    public Map<String, Object> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();

        // 1. 校验 Token 格式
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 2. 校验 Token 有效性
            if (jwtUtils.validateToken(token)) {
                String username = jwtUtils.getUsernameFromToken(token);

                // 3. [关键修正] 查询数据库获取完整的用户信息
                // 因为 adminService 继承了 ServiceImpl，可以直接使用 getOne + Wrapper 查询
                LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Admin::getUsername, username);
                Admin admin = adminService.getOne(queryWrapper);

                if (admin != null) {
                    admin.setPassword(null); // 擦除密码

                    result.put("loggedIn", true);
                    result.put("username", username);
                    result.put("userData", admin); // [重点] 返回完整对象，前端 checkSession 需要读取 avatarUrl, nickname 等
                    return result;
                }
            }
        }

        // 验证失败或无 Token
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