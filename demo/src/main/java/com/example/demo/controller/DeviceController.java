package com.example.demo.controller;

// 导入我们之前创建的 DTO 和 Service
import com.example.demo.dto.DeviceDataDto;
import com.example.demo.service.OneNetDataService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * [重点] 这个控制器专门负责提供设备数据
 */
@RestController
@RequestMapping("/api") // 告诉 Spring Boot, 这个类处理 /api 开头的请求
public class DeviceController {

    // 自动注入 OneNET 数据服务
    private final OneNetDataService dataService;

    public DeviceController(OneNetDataService dataService) {
        this.dataService = dataService;
    }

    /**
     * [重点] @GetMapping 告诉 Spring Boot, 这个方法处理 /device-data 的 GET 请求
     * * 完整的 URL: /api + /device-data = /api/device-data
     */
    @GetMapping("/device-data")
    public Mono<DeviceDataDto> getDeviceData() {
        // 调用 Service 层去获取 OneNET 数据
        return dataService.getCombinedDeviceData();
    }
}