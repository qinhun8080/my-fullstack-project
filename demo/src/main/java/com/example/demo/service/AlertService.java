package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class AlertService {

    // 使用 LinkedList 作为双端队列，方便从头部添加，从尾部删除
    private final LinkedList<String> alertHistory = new LinkedList<>();
    private static final int MAX_ALERTS = 20;
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    /**
     * 检查距离并添加警告
     * @param distanceStr 传感器传来的距离字符串
     */
    public void checkAndAddAlert(String distanceStr) {
        try {
            // 尝试将字符串转为数字 (例如 "25.0" 或 "25")
            double distance = Double.parseDouble(distanceStr);

            if (distance < 30) {
                String time = sdf.format(new Date());
                String alertMsg = "[" + time + "] 危险警告！距离过近：" + distance + "cm";

                addAlert(alertMsg);
            }
        } catch (NumberFormatException e) {
            // 如果距离不是数字（例如 "--" 或 "null"），忽略，不产生报警
        }
    }

    //以此方法管理队列：最新的在最前面，超过20条删除最后面
    private synchronized void addAlert(String msg) {
        // 1. 添加到列表头部 (最新)
        alertHistory.addFirst(msg);

        // 2. 如果超过 20 条，移除列表尾部 (最旧)
        if (alertHistory.size() > MAX_ALERTS) {
            alertHistory.removeLast();
        }
    }

    public List<String> getAlertHistory() {
        return alertHistory;
    }
}