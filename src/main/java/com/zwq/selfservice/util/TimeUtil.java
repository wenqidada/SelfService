package com.zwq.selfservice.util;

import java.time.LocalTime;

public class TimeUtil {

    public static boolean isInNightTimeRange() {
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime = LocalTime.of(23, 0);  // 23:00
        LocalTime endTime = LocalTime.of(6, 0);     // 06:00

        // 如果当前时间在23:00之后或6:00之前
        return currentTime.isAfter(startTime) || currentTime.isBefore(endTime);
    }

    // 更严谨的版本，处理边界情况
    public static boolean isInNightTimeRangeStrict() {
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime = LocalTime.of(23, 0);
        LocalTime endTime = LocalTime.of(6, 0);

        if (startTime.isBefore(endTime)) {
            // 正常情况：startTime < endTime
            return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
        } else {
            // 跨天情况：startTime > endTime（如23:00到6:00）
            return currentTime.isAfter(startTime) || currentTime.isBefore(endTime);
        }
    }

    // 使用示例
    public static void main(String[] args) {
        if (isInNightTimeRange()) {
            System.out.println("当前时间在23:00到6:00之间");
        } else {
            System.out.println("当前时间不在23:00到6:00之间");
        }
    }
}