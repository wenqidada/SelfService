package com.zwq.selfservice.vo.yunfei;

import lombok.Data;

@Data
public class PageQueryPoiListRequest {
    private int page;
    private int limit;
    private String opPoiId;
}