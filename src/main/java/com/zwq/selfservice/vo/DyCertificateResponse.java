package com.zwq.selfservice.vo;

import lombok.Data;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DyCertificateResponse {
    private Extra extra;
    private Data data;

    @Builder
    @lombok.Data
    public static class Extra {
        private Long error_code;
        private String description;
        private Long sub_error_code;
        private String sub_description;
        private String logid;
        private Long now;
    }

    @lombok.Data
    @Builder
    public static class Data {
        private List<Certificate> certificates;
        private String order_id;
        private String verify_token;
        private Long error_code;
        private String description;
    }

    @lombok.Data
    @Builder
    public static class Certificate {
        private String encrypted_code;
        private String code;
        private List<String> not_available_poi_list;
        private Map<String, String> additional_map;
        private Long expire_time;
        private Sku sku;
        private TimeCard time_card;
        private Verify verify;
        private ReserveInfo reserve_info;
        private UseTimeInfo use_time_info;
        private Integer used_status_type;
        private OffPeakDiscountInfo off_peak_discount_info;
        private BookInfo book_info;
        private List<VerifyRecord> verify_records;
        private Amount amount;
        private Integer status;
        private PeriodCard period_card;
        private Long start_time;
        private NotAvailableTimeInfo not_available_time_info;
        private Long certificate_id;
    }

    @lombok.Data
    @Builder
    public static class Sku {
        private String suplier_product_out_id;
        private String account_id;
        private Integer voucher_type;
        private String product_out_id;
        private String product_id;
        private String sku_id;
        private Long sold_start_time;
        private String title;
        private Long market_price;
        private String sku_out_id;
        private Integer groupon_type;
        private String third_sku_id;
    }

    @lombok.Data
    @Builder
    public static class TimeCard {
        private Long times_count;
        private Long times_used;
        private List<SerialAmount> serial_amount_list;
        private Integer time_card_type;
    }

    @lombok.Data
    @Builder
    public static class SerialAmount {
        private Long serial_numb;
        private Amount amount;
    }

    @lombok.Data
    @Builder
    public static class Amount {
        private String original_currency;
        private Long brand_ticket_amount;
        private Long platform_discount_amount;
        private Long merchant_ticket_amount;
        private Long coupon_pay_amount;
        private Long origin_list_market_amount;
        private Long payment_discount_amount;
        private Long pay_amount;
        private Long original_amount;
        private Long list_market_amount;
    }

    @lombok.Data
    @Builder
    public static class Verify {
        private String verify_id;
        private String certificate_id;
        private Long verify_time;
        private Boolean can_cancel;
        private Integer verify_type;
        private String verifier_unique_id;
        private Long poi_id;
        private Long times_card_serial_num;
    }

    @lombok.Data
    @Builder
    public static class ReserveInfo {
        private List<OrderReserveUserInfo> order_reserve_user_info_list;
    }

    @lombok.Data
    @Builder
    public static class OrderReserveUserInfo {
        private String credential_numb;
        private Integer credential_type;
        private String name;
        private String phone;
    }

    @lombok.Data
    @Builder
    public static class UseTimeInfo {
        private Integer use_time_type;
        private List<TimePeriod> time_period_list;
    }

    @lombok.Data
    @Builder
    public static class TimePeriod {
        private String end_time;
        private Boolean end_time_is_next_day;
        private String start_time;
    }

    @lombok.Data
    @Builder
    public static class OffPeakDiscountInfo {
        private Integer idle_time_limit_type;
        private Boolean has_off_peak_discount;
        private List<OffPeakTimeRange> off_peak_time_range;
    }

    @lombok.Data
    @Builder
    public static class OffPeakTimeRange {
        private List<Integer> week_day_list;
        private Long start_time;
        private Long end_time;
        private List<DailyTimeRange> daily_time_range_list;
    }

    @lombok.Data
    @Builder
    public static class DailyTimeRange {
        private String end_time;
        private Boolean end_time_is_next_day;
        private String start_time;
    }

    @lombok.Data
    @Builder
    public static class BookInfo {
        private String book_poi_id;
        private Long book_product_number;
        private Long verify_amount;
    }

    @lombok.Data
    @Builder
    public static class VerifyRecord {
        private String verifier_unique_id;
        private Long poi_id;
        private Long times_card_serial_num;
        private String verify_id;
        private String certificate_id;
        private Long verify_time;
        private Boolean can_cancel;
        private Integer verify_type;
    }

    @lombok.Data
    @Builder
    public static class PeriodCard {
        private Integer period_type;
    }

    @lombok.Data
    @Builder
    public static class NotAvailableTimeInfo {
        private List<Long> can_no_use_week_day;
        private List<NoUseDate> can_no_use_date;
        private Boolean fulfil_enable;
    }

    @lombok.Data
    @Builder
    public static class NoUseDate {
        private Long end_time;
        private Long start_time;
    }
}