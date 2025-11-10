package com.zwq.selfservice.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeChatResponseVO {

    private int code;
    private String id;
    private String token;

}
