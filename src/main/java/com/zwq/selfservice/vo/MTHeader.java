package com.zwq.selfservice.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MTHeader {
    /**
     * 门店令牌，接口需要授权时必填
     * 示例值: abc
     */
    private String appAuthToken;

    /**
     * 数字签名（必填）
     * 示例值: dwe
     */
    private String sign;

    /**
     * 时间戳，单位秒（必填）
     * 示例值: 123
     */
    private Long timestamp;

    /**
     * 字符编码（必填）
     * 示例值: utf-8
     */
    @Builder.Default
    private String charset = "utf-8";

    /**
     * 服务商身份标识（必填）
     */
    private Long developerId;

    /**
     * 版本号，固定传2（必填）
     */
    @Builder.Default
    private String version = "2";

    /**
     * 业务类型id（必填）
     */
    @Builder.Default
    private Integer businessId = 58;

}
