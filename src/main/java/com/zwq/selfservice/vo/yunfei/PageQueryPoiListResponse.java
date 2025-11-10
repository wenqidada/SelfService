package com.zwq.selfservice.vo.yunfei;

import lombok.Data;
import java.util.List;

@Data
public class PageQueryPoiListResponse {
    private int code;
    private String msg;
    private List<DataBean> data;

    @Data
    public static class DataBean {
        private String opPoiId;
        private String name;
        private String address;
        private String cityName;
        private long expiresIn;
        private String expiresTime;
        private List<AuthorizePlatform> authorizePlatforms;

        @Data
        public static class AuthorizePlatform {
            private String platform;
            private int status;
            private Long expiresIn;
            private String expiresTime;
        }
    }
}