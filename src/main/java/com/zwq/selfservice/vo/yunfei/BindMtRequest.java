package com.zwq.selfservice.vo.yunfei;

import lombok.Data;

@Data
public class BindMtRequest {
    private String name;
    private String mtShopId;
    private String mtAccountId;
    private String mtAccountName;
    private String address;
    private String cityName;
    private String platform;
    private String businessType;
}