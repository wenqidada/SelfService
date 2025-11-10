package com.zwq.selfservice.vo.yunfei;
import lombok.Data;

@Data
public class BindDyResponse {
    private int code;
    private String msg;
    private DataBean data;

    @Data
    public static class DataBean {
        private String opPoiId;
        private String name;
        private String address;
        private String cityName;
    }
}