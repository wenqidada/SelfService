package com.zwq.selfservice.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OpenTableRequest {
    private String code;
    private int time ;
    private int type;
    private int tableId;
    private String token;
    private BigDecimal price;
}
