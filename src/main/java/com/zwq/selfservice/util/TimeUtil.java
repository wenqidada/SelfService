package com.zwq.selfservice.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    private static final Map<String, DayOfWeek> WEEKDAY_MAP = new HashMap<>();

    private static final Pattern pattern = Pattern.compile("【(.*?)】");


    static {
        WEEKDAY_MAP.put("周一", DayOfWeek.MONDAY);
        WEEKDAY_MAP.put("周二", DayOfWeek.TUESDAY);
        WEEKDAY_MAP.put("周三", DayOfWeek.WEDNESDAY);
        WEEKDAY_MAP.put("周四", DayOfWeek.THURSDAY);
        WEEKDAY_MAP.put("周五", DayOfWeek.FRIDAY);
        WEEKDAY_MAP.put("周六", DayOfWeek.SATURDAY);
        WEEKDAY_MAP.put("周日", DayOfWeek.SUNDAY);
        WEEKDAY_MAP.put("星期一", DayOfWeek.MONDAY);
        WEEKDAY_MAP.put("星期二", DayOfWeek.TUESDAY);
        WEEKDAY_MAP.put("星期三", DayOfWeek.WEDNESDAY);
        WEEKDAY_MAP.put("星期四", DayOfWeek.THURSDAY);
        WEEKDAY_MAP.put("星期五", DayOfWeek.FRIDAY);
        WEEKDAY_MAP.put("星期六", DayOfWeek.SATURDAY);
        WEEKDAY_MAP.put("星期天", DayOfWeek.SUNDAY);
        WEEKDAY_MAP.put("星期日", DayOfWeek.SUNDAY);
    }

    /**
     * 判断当前时间是否在指定的时间范围内
     *
     * @param timeStr 时间字符串，格式为【周一,周二,周四】或【23:00~15:00】
     * @return 当前时间是否在指定范围内
     */
    public static boolean isCurrentTimeInRange(String timeStr) {
        if (!timeStr.startsWith("【")){
            return true;
        }

        // 提取方括号内的内容
        Matcher matcher = pattern.matcher(timeStr);

        if (!matcher.find()) {
            return false;
        }

        String content = matcher.group(1);
        LocalDateTime now = LocalDateTime.now();

        // 判断是星期格式还是时间格式
        if (content.contains("~")) {
            // 时间格式：23:00~15:00
            return isInTimeRange(content, now);
        } else if (content.contains(",")) {
            // 星期格式：周一,周二,周四
            return isInWeekdayRange(content, now);
        } else {
            return false;
        }
    }

    private static boolean isInTimeRange(String timeRangeStr, LocalDateTime currentTime) {
        try {
            String[] parts = timeRangeStr.split("~");
            if (parts.length != 2) {
                return false;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startTime = LocalTime.parse(parts[0].trim(), formatter);
            LocalTime endTime = LocalTime.parse(parts[1].trim(), formatter);
            LocalTime currentTimeOnly = currentTime.toLocalTime();

            // 处理跨天的情况（如23:00~15:00）
            if (startTime.isAfter(endTime)) {
                // 跨天时间段：当前时间 >= 开始时间 或 当前时间 <= 结束时间
                return currentTimeOnly.isAfter(startTime) || currentTimeOnly.isBefore(endTime)
                        || currentTimeOnly.equals(startTime) || currentTimeOnly.equals(endTime);
            } else {
                // 同一天时间段
                return (currentTimeOnly.isAfter(startTime) || currentTimeOnly.equals(startTime))
                        && (currentTimeOnly.isBefore(endTime) || currentTimeOnly.equals(endTime));
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isInWeekdayRange(String weekdayStr, LocalDateTime currentTime) {
        DayOfWeek currentWeekday = currentTime.getDayOfWeek();
        String[] weekdays = weekdayStr.split(",");

        for (String day : weekdays) {
            day = day.trim();
            if (WEEKDAY_MAP.containsKey(day) && WEEKDAY_MAP.get(day) == currentWeekday) {
                return true;
            }
        }

        return false;
    }

    // 测试方法
    public static void main(String[] args) {
        // 测试星期判断
        String weekdayTest = "【周一,周二,周四】";
        System.out.println("当前时间在 " + weekdayTest + " 内: " + isCurrentTimeInRange(weekdayTest));

        // 测试时间判断
        String timeTest = "【23:00~15:00】";
        System.out.println("当前时间在 " + timeTest + " 内: " + isCurrentTimeInRange(timeTest));

        // 测试同一天时间段
        String timeTest2 = "【09:00~18:00】";
        System.out.println("当前时间在 " + timeTest2 + " 内: " + isCurrentTimeInRange(timeTest2));

        // 测试无效格式
        String invalidTest = "无效格式";
        System.out.println("当前时间在 " + invalidTest + " 内: " + isCurrentTimeInRange(invalidTest));

        if (isInNightTimeRange()) {
            System.out.println("当前时间在23:00到6:00之间");
        } else {
            System.out.println("当前时间不在23:00到6:00之间");
        }
    }

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
        LocalTime endTime = LocalTime.of(15, 0);

        if (startTime.isBefore(endTime)) {
            // 正常情况：startTime < endTime
            return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
        } else {
            // 跨天情况：startTime > endTime（如23:00到6:00）
            return currentTime.isAfter(startTime) || currentTime.isBefore(endTime);
        }
    }
}