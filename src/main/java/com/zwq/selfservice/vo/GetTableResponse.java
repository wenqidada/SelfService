package com.zwq.selfservice.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetTableResponse {
    private Integer tableNumber;
    private Byte tableType;
    private Byte useType;
}
