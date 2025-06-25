package com.zwq.selfservice.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DyAccessTokenResponse {
    /**
     * access-token 接口调用凭证超时时间，单位（秒）
     * 示例值: 86400
     */
    private Long expiresIn;

    /**
     * access-token 接口调用凭证
     * 示例值: clt.5a14a88ef6ebcdc4688220d6976510f2aqU4F9S4OudWYC8dCJKLPZ******
     */
    private String accessToken;

    /**
     * 错误码描述
     * 示例值: ""
     */
    private String description;

    /**
     * 错误码
     * 示例值: 0
     */
    private Long errorCode;

    /**
     * 响应消息
     * 示例值: success
     */
    private String message;
}