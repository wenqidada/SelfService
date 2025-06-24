package com.zwq.selfservice.vo;

import lombok.Data;

@Data
public class SwitchResponseVO {

    //状态码
    private int code;
    //返回信息
    private String msg;
    //数据体
    private String data;

}
