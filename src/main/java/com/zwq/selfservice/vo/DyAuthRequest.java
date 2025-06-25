package com.zwq.selfservice.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DyAuthRequest {
    /**
     * 应用唯一标识，即AppID
     * 必填
     */
    private String clientKey;

    /**
     * 应用唯一标识对应的密钥，即Appsecret
     * 必填
     */
    private String clientSecret;

    /**
     * 固定值“client_credential”
     * 必填
     * 示例值: client_credential
     */
    private String grantType;
}
