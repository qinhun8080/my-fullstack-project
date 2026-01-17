package com.example.demo.controller;

import com.example.demo.model.Admin; // 你的实体类
import com.example.demo.service.AdminService; // 你的 Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    // 必须要和 WebConfig 里配置的物理路径一致
    private static final String UPLOAD_DIR = "D:/rear/images/";

    // 必须要和 WebConfig 里配置的 URL 前缀一致
    private static final String URL_PREFIX = "http://localhost:8081/images/";

    @Autowired
    private AdminService adminService;

    @PostMapping("/avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file,
                               @RequestParam("userId") Integer userId) {
        if (file.isEmpty()) {
            return "上传失败，文件为空";
        }

        try {
            // 1. 生成唯一文件名 (防止文件名冲突覆盖)
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 2. 保存文件到本地磁盘
            File dest = new File(UPLOAD_DIR + newFileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);

            // 3. 生成访问 URL (例如: http://localhost:8081/images/uuid.jpg)
            String fileUrl = URL_PREFIX + newFileName;

            // 4. 更新数据库
            // 先查出用户
            Admin admin = adminService.getById(userId);
            if (admin != null) {
                admin.setAvatarUrl(fileUrl); // 设置新的头像地址
                adminService.updateById(admin); // 保存到数据库
                return fileUrl; // 返回给前端回显
            } else {
                return "用户不存在";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败: " + e.getMessage();
        }
    }
}