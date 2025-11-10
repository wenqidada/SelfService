package com.zwq.selfservice.vo.yunfei;

import lombok.Data;

@Data
public class QueryShopReviewRequest {
    private String opPoiId;
    private int reviewPlatform;
    private String platform;
    private String beginTime;
    private String endTime;
    private int star;
    private int page;
}