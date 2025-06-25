package com.zwq.selfservice.vo;

import lombok.Data;
import lombok.Builder;

/**
 * 核销券请求实体
 */
@Data
@Builder
public class MTRequest {
    /**
     * 团购券码，必须未验证
     * 必填，长度大于0
     */
    private String receiptCode;

    /**
     * 验券数量，不可多于100个
     * 必填，数值大于等于1
     * 示例值: 5
     */
    private Integer count;

    /**
     * 请求ID，用于标识幂等性
     * 必填，长度大于0
     */
    private String requestId;

    /**
     * 商家在自研系统或第三方服务商系统内登陆的用户名，仅用于记录验券者的信息
     * 必填，长度大于0
     */
    private String appShopAccountName;

    /**
     * 商家在自研系统或第三方服务商系统内登录的帐号，仅用于记录验券者的信息
     * 必填，长度大于0
     */
    private String appShopAccount;
}