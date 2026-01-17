package com.example.demo.controller;

import com.example.demo.model.Admin;
import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 更新个人信息 (包括头像路径)
     * 前端传参 JSON 示例:
     * {
     * "id": 1,
     * "nickname": "新名字",
     * "avatarUrl": "/images/uuid-xxx.jpg"  <-- 接收相对路径
     * }
     */
    @PostMapping("/update")
    public Map<String, Object> updateProfile(@RequestBody Admin admin) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 调用 Service 更新 (MyBatis Plus 会自动处理非空字段)
            boolean success = adminService.updateAdmin(admin);

            if (success) {
                result.put("success", true);
                result.put("message", "个人信息更新成功");
                // 为了让前端更新缓存，把更新后的对象（包含新头像）返回去
                result.put("data", admin);
            } else {
                result.put("success", false);
                result.put("message", "更新失败，ID不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }

        return result;
    }

    // 在 AdminController 类中添加以下方法
    @GetMapping("/user-list")
    public Map<String, Object> getUserList() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用 Service 获取角色为 "user" 的列表
            List<Admin> users = adminService.getUserListByRole("user");

            result.put("success", true);
            result.put("data", users);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取用户列表失败: " + e.getMessage());
        }
        return result;
    }

    // 在 AdminController.java 中追加以下方法

    /**
     * 1. 新增普通用户接口
     */
    @PostMapping("/user/add")
    public Map<String, Object> addUser(@RequestBody Admin admin) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = adminService.addUser(admin);
            if (success) {
                result.put("success", true);
                result.put("message", "用户创建成功");
            } else {
                result.put("success", false);
                result.put("message", "用户名已存在或创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }
        return result;
    }

    /**
     * 2. 编辑用户信息接口 (复用 Service 已有的 updateAdmin)
     */
    @PostMapping("/user/update")
    public Map<String, Object> updateUser(@RequestBody Admin admin) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 注意：Service 的 updateAdmin 已经处理了密码加密逻辑
            // 如果前端传了 password 字段，它会被加密更新；如果传空字符串，则不修改密码
            boolean success = adminService.updateAdmin(admin);
            if (success) {
                result.put("success", true);
                result.put("message", "用户信息更新成功");
            } else {
                result.put("success", false);
                result.put("message", "更新失败，用户ID不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误");
        }
        return result;
    }

    /**
     * 3. 删除用户接口
     */
    @PostMapping("/user/delete")
    public Map<String, Object> deleteUser(@RequestBody Map<String, Integer> body) {
        Map<String, Object> result = new HashMap<>();
        Integer id = body.get("id");
        try {
            boolean success = adminService.deleteUser(id);
            if (success) {
                result.put("success", true);
                result.put("message", "用户删除成功");
            } else {
                result.put("success", false);
                result.put("message", "删除失败，用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误");
        }
        return result;
    }
}