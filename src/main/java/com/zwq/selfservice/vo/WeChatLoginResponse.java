package com.zwq.selfservice.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeChatLoginResponse {

    private String openId;
    private String sessionKey;
    private String unionId;
    private String errMsg;
    private int errCode;

}
