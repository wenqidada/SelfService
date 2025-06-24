package com.zwq.selfservice.vo;

import lombok.Data;

@Data
public class SwitchRequestVO {


    //设备编码
    public String serialNumber;
    //设备类型
    public String type;
    //指令体
    public String command;


}
