package com.example.demo.service;

import com.example.demo.dto.DeviceDataDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OneNetDataService {

    private final WebClient webClient;
    private final OneNetTokenService tokenService;
    private final AlertService alertService; // [新增] 注入报警服务

    @Value("${onet.product-id}")
    private String productId;

    @Value("${onet.device-name}")
    private String deviceName;

    // TODO: 请确保下面这些字符串与 OneNET 平台“功能定义”中的【标识符】完全一致
    private static final String KEY_TEMP = "temp";         // 温度标识符
    private static final String KEY_HUMI = "humi";         // 湿度标识符
    private static final String KEY_DISTANCE = "distance"; // 超声波测距标识符

    // 构造函数注入
    public OneNetDataService(WebClient.Builder webClientBuilder,
                             OneNetTokenService tokenService,
                             AlertService alertService, // [新增] 注入
                             @Value("${onet.api-url}") String oneNetApiUrl) {
        this.webClient = webClientBuilder.baseUrl(oneNetApiUrl).build();
        this.tokenService = tokenService;
        this.alertService = alertService;
    }

    /**
     * 获取设备状态和属性，并组合成 DTO 返回
     * 同时触发距离报警检查
     */
    public Mono<DeviceDataDto> getCombinedDeviceData() {
        Mono<String> statusMono = fetchDeviceStatus();
        Mono<Map<String, String>> propertiesMono = fetchDeviceProperties();

        return Mono.zip(statusMono, propertiesMono,
                (status, propertiesMap) -> {
                    String temp = "--";
                    String humi = "--";
                    String distance = "--";

                    // 【修改点】只有当设备在线时，才解析数据
                    // 注意：这里要确保你的 status 字符串和 fetchDeviceStatus 返回的一致
                    if ("在线".equals(status)) {
                        temp = propertiesMap.getOrDefault(KEY_TEMP, "--");
                        humi = propertiesMap.getOrDefault(KEY_HUMI, "--");
                        distance = propertiesMap.getOrDefault(KEY_DISTANCE, "--");

                        // 只有在线时才进行报警检查，避免离线时一直重复报警最后一次的距离
                        alertService.checkAndAddAlert(distance);
                    } else {
                        // 可选：如果是离线，你甚至可以在这里记录一条日志
                        System.out.println("设备离线，忽略云端缓存数据");
                    }

                    return new DeviceDataDto(
                            status,
                            temp,
                            humi,
                            distance,
                            alertService.getAlertHistory()
                    );
                });
    }

    /**
     * 获取设备在线状态
     */
    private Mono<String> fetchDeviceStatus() {
        String token = tokenService.createCommonToken();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/devices/status")
                        .queryParam("product_id", productId)
                        .queryParam("device_name", deviceName)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("online") != null) {
                        return (Boolean) data.get("online") ? "在线" : "离线";
                    }
                    return "未知";
                })
                .onErrorReturn("N/A");
    }

    /**
     * 获取设备属性（温度、湿度、距离）
     */
    private Mono<Map<String, String>> fetchDeviceProperties() {
        String token = tokenService.createCommonToken();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/thingmodel/query-device-property")
                        .queryParam("product_id", productId)
                        .queryParam("device_name", deviceName)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
                    Map<String, String> resultMap = new HashMap<>();

                    if (dataList != null) {
                        // 遍历列表，将 {identifier: "temp", value: 25} 转为 map key-value
                        for (Map<String, Object> item : dataList) {
                            String identifier = (String) item.get("identifier");
                            Object val = item.get("value");
                            if (identifier != null && val != null) {
                                resultMap.put(identifier, String.valueOf(val));
                            }
                        }
                    }
                    return resultMap;
                })
                .onErrorReturn(Map.of());
    }
}