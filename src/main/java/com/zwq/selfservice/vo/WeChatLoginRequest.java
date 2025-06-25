package com.zwq.selfservice.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WeChatLoginRequest {

    private String appid;
    private String secret;
    private String jsCode;
    @Builder.Default
    private String grantType = "authorization_code";

}
