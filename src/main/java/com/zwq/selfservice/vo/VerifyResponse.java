package com.zwq.selfservice.vo;

import lombok.Data;
import java.util.List;

@Data
public class VerifyResponse {
    private DataContent data;
    private Extra extra;

    @lombok.Data
    public static class DataContent {
        private Long error_code;
        private String description;
        private List<VerifyResult> verify_results;
    }

    @lombok.Data
    public static class VerifyResult {
        private Long result;
        private String msg;
        private String code;
        private String verify_id;
        private String certificate_id;
        private String origin_code;
        private String account_id;
    }

    @lombok.Data
    public static class Extra {
        private Long error_code;
        private String description;
        private Long sub_error_code;
        private String sub_description;
        private String logid;
        private Long now;
    }
}