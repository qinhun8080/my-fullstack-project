package com.example.demo.controller;

import com.example.demo.dto.DeviceDataDto;
import com.example.demo.dto.GpsDataDto;
import com.example.demo.service.GpsDataService;
import com.example.demo.service.OneNetDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
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

    @GetMapping("/device-data")
    public Mono<Map<String, Object>> getDeviceData() {
        System.out.println("正在请求设备数据..."); // 1. 添加日志，确保请求进来了

        // 1. 获取设备数据 (增加异常捕获)
        Mono<DeviceDataDto> deviceMono = oneNetDataService.getCombinedDeviceData()
                .defaultIfEmpty(new DeviceDataDto(
                        "设备离线(NoData)", "--", "--", "--", Collections.emptyList()
                ))
                .onErrorResume(e -> {
                    // [关键修改] 如果发生报错（如网络断了），也返回默认值，不要崩掉
                    System.err.println("获取设备数据出错，启用兜底: " + e.getMessage());
                    e.printStackTrace(); // 打印详细错误方便排查
                    return Mono.just(new DeviceDataDto(
                            "设备离线(Error)", "--", "--", "--", Collections.emptyList()
                    ));
                });

        // 2. 获取 GPS 数据 (也增加一个保险)
        Mono<GpsDataDto> gpsMono = gpsDataService.getGpsData()
                .defaultIfEmpty(new GpsDataDto("39.9088", "116.3975")) // 防止 GPS 服务也为空
                .onErrorResume(e -> {
                    System.err.println("获取 GPS 出错: " + e.getMessage());
                    return Mono.just(new GpsDataDto("39.9088", "116.3975")); // 出错也返回天安门
                });

        // 3. 合并数据
        return Mono.zip(deviceMono, gpsMono, (deviceDto, gpsDto) -> {
            Map<String, Object> result = new HashMap<>();

            result.put("deviceStatus", deviceDto.getDeviceStatus());
            result.put("temp", deviceDto.getTemp());
            result.put("humi", deviceDto.getHumi());
            result.put("distance", deviceDto.getDistance());
            result.put("historyAlerts", deviceDto.getHistoryAlerts());

            // 确保这里 put 的 key 和前端 vue 里取的一致
            result.put("latitude", gpsDto.getLatitude());
            result.put("longitude", gpsDto.getLongitude());

            System.out.println("数据合并完成，准备返回: " + result); // 打印最终结果
            return result;
        });
    }
}