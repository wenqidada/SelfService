package com.zwq.selfservice.vo.yunfei;

import lombok.Data;

@Data
public class ConsumeRequest {
    private String opPoiId;
    private String receiptCode;
    private String platform;
    private String type;
    private Integer verifyCount;
}