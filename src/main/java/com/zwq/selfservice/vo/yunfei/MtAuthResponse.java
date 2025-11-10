package com.zwq.selfservice.vo.yunfei;

import lombok.Data;
import java.util.List;

@Data
public class MtAuthResponse {
    private int code;
    private String msg;
    private String type;
    private DataBean data;

    @Data
    public static class DataBean {
        private String appId;
        private String authCode;
        private List<PoiBean> poiList;

        @Data
        public static class PoiBean {
            private String opPoiId;
            private long expiresIn;
            private String expiresTimes;
        }
    }
}