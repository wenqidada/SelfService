package com.zwq.selfservice.vo;

import lombok.Data;

@Data
public class WeChatLoginResponse {

    private String openId;
    private String session_Key;
    private String unionId;
    private String errMsg;
    private int errCode;

    public WeChatLoginResponse(){}

}
