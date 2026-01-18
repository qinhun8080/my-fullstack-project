package com.example.demo.dto;

import lombok.Data;

/**
 * 新建的 GPS 数据类，与原有业务逻辑隔离
 */
@Data
public class GpsDataDto {
    private String latitude;
    private String longitude;

    public GpsDataDto(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}