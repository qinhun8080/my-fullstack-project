package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 更新普通用户信息
     * 前端请求示例: { "id": 2, "nickname": "小明", "avatarUrl": "/images/abc.jpg" }
     */
    @PostMapping("/update")
    public Map<String, Object> updateProfile(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 校验 ID 是否存在
            if (user.getId() == null) {
                result.put("success", false);
                result.put("message", "更新失败：用户ID不能为空");
                return result;
            }

            // 调用 Service 更新
            boolean success = userService.updateUser(user);

            if (success) {
                // 查询最新的用户信息返回给前端（用于更新缓存）
                User updatedUser = userService.getById(user.getId());
                updatedUser.setPassword(null); // 擦除密码

                result.put("success", true);
                result.put("message", "个人信息更新成功");
                result.put("data", updatedUser);
            } else {
                result.put("success", false);
                result.put("message", "更新失败，用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取用户详情 (可选，用于进入个人中心时拉取最新数据)
     */
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(@RequestParam Integer id) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.getById(id);

        if (user != null) {
            user.setPassword(null);
            result.put("success", true);
            result.put("data", user);
        } else {
            result.put("success", false);
            result.put("message", "用户未找到");
        }
        return result;
    }

    // src/main/java/com/example/demo/controller/LoginController.java

// ...

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
}