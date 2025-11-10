package com.zwq.selfservice.vo.yunfei;

import lombok.Data;

@Data
public class SubmitRequest {
    private String corpName;
    private String appType;
    private String developer;
    private String phoneNumber;
    private String wechatNumber;
    private String email;
}