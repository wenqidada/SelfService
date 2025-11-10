package com.zwq.selfservice.vo.yunfei;
import lombok.Data;
import java.util.List;

@Data
public class QueryShopReviewResponse {
    private int code;
    private String msg;
    private int count;
    private List<DataBean> data;
    private String tid;

    @Data
    public static class DataBean {
        private String reviewId;
        private int star;
        private int accurateStar;
        private String reviewTime;
        private String consumeOrderId;
        private String consumeAmount;
        private String consumeTime;
        private String serialNumbers;
        private int reviewQuality;
        private List<ScoreDetail> scoreDetails;

        @Data
        public static class ScoreDetail {
            private String title;
            private int score;
            private int accurateScore;
        }
    }
}