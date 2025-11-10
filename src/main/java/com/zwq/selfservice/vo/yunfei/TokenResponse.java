package com.zwq.selfservice.vo.yunfei;

import lombok.Data;

@Data
public class TokenResponse {
    private int code;
    private String msg;
    private DataBean data;

    @Data
    public static class DataBean {
        private String accessToken;
        private long expiresIn;
    }
}
