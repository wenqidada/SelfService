package com.zwq.selfservice.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WeChatLoginRequest {

    private String appid;
    private String secret;
    private String js_Code;
    @Builder.Default
    private String grant_Type = "authorization_code";

}
