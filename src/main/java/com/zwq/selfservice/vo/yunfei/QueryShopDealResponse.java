package com.zwq.selfservice.vo.yunfei;

import lombok.Data;
import java.util.List;

@Data
public class QueryShopDealResponse {
    private int code;
    private String msg;
    private int count;
    private String nextCursor;
    private List<DataBean> data;
    private String tid;

    @Data
    public static class DataBean {
        private Long dealId;
        private Long dealGroupId;
        private String skuId;
        private String beginDate;
        private String endDate;
        private String title;
        private Double price;
        private Double marketPrice;
        private String receiptBeginDate;
        private String receiptEndDate;
        private String saleStatus;
        private String dealGroupStatus;
        private String saleChannelName;
        private String dealType;
    }
}