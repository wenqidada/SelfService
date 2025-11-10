package com.zwq.selfservice.vo.yunfei;


import lombok.Data;

@Data
public class SubmitResponse {
    private int code;
    private String msg;
    private int count;
    private DataBean data;
    private String nextCursor;
    private String tid;

    @Data
    public static class DataBean {
        private String applyNo;
        private int applyStatus;
        private String reason;
        private String appId;
        private String appSecret;
    }
}