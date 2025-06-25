package com.zwq.selfservice.vo;

import lombok.Data;
import lombok.Builder;
import java.util.List;

/**
 * 抖音验券请求实体
 */
@Data
@Builder
public class DyVerifyRequest {
    /**
     * 一次验券的标识 (用于短时间内的幂等)
     * 必填
     */
    private String verifyToken;

    /**
     * 核销的抖音门店id
     * 必填
     */
    private String poiId;

    /**
     * 商户根账户ID（云连锁场景接入需传入，其余场景可不传）
     * 非必填
     */
    private String accountId;

    /**
     * 验券准备接口返回的加密抖音券码
     * 非必填
     */
    private List<String> encryptedCodes;

    /**
     * 三方原始券码值列表
     * 非必填
     */
    private List<String> codes;

    /**
     * 抖音侧的订单号 (非预导码模式的三方券码必需)
     * 非必填
     */
    private String orderId;
}