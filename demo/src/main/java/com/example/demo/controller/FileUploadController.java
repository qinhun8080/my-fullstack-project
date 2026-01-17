package com.example.demo.controller;

import com.example.demo.config.WebConfig;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "上传文件不能为空");
            return result;
        }

        try {
            // 1. 获取原文件名和后缀
            String originalFilename = file.getOriginalFilename();
            String suffixName = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";

            // 2. 生成唯一文件名 (UUID)
            String fileName = UUID.randomUUID() + suffixName;

            // 3. 确定保存的物理路径 (D:/rear/images/uuid.jpg)
            File dest = new File(WebConfig.UPLOAD_PATH + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }

            // 4. 执行保存
            file.transferTo(dest);

            // 5. 生成相对路径 (这就是我们要存数据库的格式)
            // 格式：/images/uuid.jpg
            String relativePath = "/images/" + fileName;

            // 6. 生成完整 URL (仅供前端回显预览使用，不建议存数据库)
            // 假设后端端口是 8081
            String fullUrl = "http://localhost:8081" + relativePath;

            result.put("success", true);
            result.put("message", "上传成功");

            // 返回给前端的数据
            result.put("filePath", relativePath); // 前端拿到这个，赋值给 form.avatarUrl，然后调用 update 接口
            result.put("previewUrl", fullUrl);    // 前端拿到这个，用于 img src 立即显示

        } catch (IOException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "上传失败: " + e.getMessage());
        }

        return result;
    }
}