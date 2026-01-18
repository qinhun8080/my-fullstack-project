package com.example.demo.service;

import com.example.demo.dto.GpsDataDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GpsDataService {

    public Mono<GpsDataDto> getGpsData() {
        // 这里模拟获取 GPS 数据
        // 未来可以在这里接入你的 GPS 硬件接口
        return Mono.just(new GpsDataDto("39.908823", "116.397470"));
    }
}