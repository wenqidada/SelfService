package com.zwq.selfservice.vo.yunfei;

import lombok.Data;
import java.util.List;

@Data
public class ConsumeResponse {
    private int code;
    private int count;
    private DataBean data;
    private String msg;
    private String tid;

    @Data
    public static class DataBean {
        private Long dealId;
        private Long dealGroupId;
        private String dealTitle;
        private String mobile;
        private String receiptCode;
        private String type;
        private Integer verifyCount;
        private String orderId;
        private int payAmount;
        private List<ResultBean> result;


        @Data
        public static class ResultBean {
            private Long dealId;
            private Long dealGroupId;
            private Double dealMarketPrice;
            private Double dealPrice;
            private String dealTitle;
            private String orderId;
            private List<PaymentDetailBean> paymentDetail;
            private Long productItemId;
            private String receiptCode;
            private List<String> verifiedReceiptCodes;
            private Boolean tgTimesCardFlag;

            @Data
            public static class PaymentDetailBean {
                private String amount;
                private Integer amountType;
                private String paymentDetailId;
            }
        }
    }
}