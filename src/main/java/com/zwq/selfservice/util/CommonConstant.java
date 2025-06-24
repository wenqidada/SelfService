package com.zwq.selfservice.util;

import com.zwq.selfservice.vo.SwitchRequestVO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CommonConstant {

    //vip开台类型
    public static final Byte OPEN_VIP_TYPE = 1;
    //押金开台类型
    public static final Byte OPEN_DEPOSIT_TYPE = 2;
    //定时开台类型
    public static final Byte OPEN_TIME_TYPE = 3;
    //抖音开台类型
    public static final Byte OPEN_YIN_TYPE = 4;
    //美团开台类型
    public static final Byte OPEN_TUAN_TYPE = 5;
    //台球桌
    public static final Byte BILLIARDS_TYPE = 1;
    //棋牌室
    public static final Byte CARD_TYPE = 2;
    //寄存柜
    public static final Byte DEPOSIT_TYPE = 3;
    //未关台
    public static final Byte NO_DONE = 0;
    //关台
    public static final Byte DONE = 1;


    public static void main(String[] args) {
        Instant instant = Instant.ofEpochSecond(1742320461);

        // 转换为本地时间
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String format = localDateTime.format(formatter);
        // 格式化输出
        System.out.println(format);
        // 转换为秒级时间戳
        long epochSecond = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        System.out.println(epochSecond);
        LocalDateTime now = LocalDateTime.now().plusHours(4);
        long epochSecond1 = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        System.out.println(epochSecond1);

    }


}
