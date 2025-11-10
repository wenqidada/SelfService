package com.zwq.selfservice.vo.yunfei;

import lombok.Data;

@Data
public class BindDyRequest {
    private String opPoiId;
    private String dyShopId;
    private String dyAccountId;
    private String platform;
    private String businessType;
}