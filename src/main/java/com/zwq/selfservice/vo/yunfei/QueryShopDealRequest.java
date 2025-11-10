package com.zwq.selfservice.vo.yunfei;

import lombok.Data;

@Data
public class QueryShopDealRequest {
    private int page;
    private String opPoiId;
    private String platform;
    private String cursor;
    private String dealType;
}