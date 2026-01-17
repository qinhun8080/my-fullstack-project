package com.example.demo.controller;

import com.example.demo.model.Admin;
import com.example.demo.model.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.UserService;
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

    @PostMapping("/login")
    public Map<String, Object> handleLogin(
            @RequestBody Map<String, String> credentials,
            HttpServletResponse response,
            HttpSession session) {

        // [修改] 获取前端传来的 username (之前可能是作为 id 使用，现在明确它是 username)
        String username = credentials.get("username");
        String password = credentials.get("password");
        boolean rememberMe = Boolean.parseBoolean(credentials.get("rememberMe"));

        // [修改] 调用 Service，现在传入的是 username
        Admin adminUser = adminService.login(username, password);

        if (adminUser != null) {
            // 登录成功，Session 中保存用户名和 ID (ID用于后续的数据库更新操作)
            session.setAttribute("user", adminUser.getUsername());
            session.setAttribute("userid", adminUser.getId());
            session.setMaxInactiveInterval(30 * 60);

            if (rememberMe) {
                // Cookie 中保存 username
                setCookie(response, "remember-user", username, 7 * 24 * 60 * 60);
                setCookie(response, "remember-pass", password, 7 * 24 * 60 * 60);
            } else {
                setCookie(response, "remember-user", null, 0);
                setCookie(response, "remember-pass", null, 0);
            }

            adminUser.setPassword(null);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登录成功！");
            result.put("userData", adminUser);

            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }
    }

    // ... 下面的 handleRegister 和 checkSessionStatus 不需要修改 ...
    // Session 检查依然建议通过 Session 中存储的 userid 来查库 (这是最稳妥的)，所以下面的代码保持不变即可。

    @GetMapping("/session-status")
    public Map<String, Object> checkSessionStatus(HttpSession session) {
        Object useridObj = session.getAttribute("userid");

        if (useridObj != null) {
            Integer userid = (Integer) useridObj;
            Admin currentUser = adminService.getById(userid); // 依然使用 ID 获取详情，没问题

            if (currentUser != null) {
                currentUser.setPassword(null);
                Map<String, Object> result = new HashMap<>();
                result.put("loggedIn", true);
                result.put("userData", currentUser);
                return result;
            }
        }
        Map<String, Object> result = new HashMap<>();
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