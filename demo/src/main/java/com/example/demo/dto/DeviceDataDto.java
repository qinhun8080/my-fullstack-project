package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeviceDataDto {
    private String deviceStatus;
    private String temp;
    private String humi;
    private String distance;
    private List<String> historyAlerts; // [新增] 存放历史预警列表

    public DeviceDataDto(String deviceStatus, String temp, String humi, String distance, List<String> historyAlerts) {
        this.deviceStatus = deviceStatus;
        this.temp = temp;
        this.humi = humi;
        this.distance = distance;
        this.historyAlerts = historyAlerts;
    }

    // 如果没有使用 Lombok，请手动补全 historyAlerts 的 getter/setter
}