package com.example.demo.controller;

import com.example.demo.dto.DeviceDataDto;
import com.example.demo.dto.GpsDataDto;
import com.example.demo.service.GpsDataService;
import com.example.demo.service.OneNetDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeviceController {

    private final OneNetDataService oneNetDataService;
    private final GpsDataService gpsDataService; // 注入新服务

    public DeviceController(OneNetDataService oneNetDataService, GpsDataService gpsDataService) {
        this.oneNetDataService = oneNetDataService;
        this.gpsDataService = gpsDataService;
    }

    /**
     * 这里返回 Map<String, Object> 而不是具体的 DTO
     * 目的是为了把两份数据合并在一起传给前端，而不破坏原有的类结构
     */
    @GetMapping("/device-data")
    public Mono<Map<String, Object>> getDeviceData() {
        // 1. 获取原有设备数据
        Mono<DeviceDataDto> deviceMono = oneNetDataService.getCombinedDeviceData();

        // 2. 获取新的 GPS 数据
        Mono<GpsDataDto> gpsMono = gpsDataService.getGpsData();

        // 3. 合并两份数据
        return Mono.zip(deviceMono, gpsMono, (deviceDto, gpsDto) -> {
            Map<String, Object> result = new HashMap<>();

            // 放入原有数据
            result.put("deviceStatus", deviceDto.getDeviceStatus());
            result.put("temp", deviceDto.getTemp());
            result.put("humi", deviceDto.getHumi());
            result.put("distance", deviceDto.getDistance());
            result.put("historyAlerts", deviceDto.getHistoryAlerts());

            // 放入新的 GPS 数据
            result.put("latitude", gpsDto.getLatitude());
            result.put("longitude", gpsDto.getLongitude());

            return result;
        });
    }
}