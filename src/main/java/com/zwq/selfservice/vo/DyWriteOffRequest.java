package com.zwq.selfservice.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DyWriteOffRequest {
    // 核销商户根账户ID（部分场景必传）
    private String accountId;

    // 券码明文（与encrypted_data二选一必传）
    private String code;

    // 从二维码解析出来的标识（与code二选一必传，需URL编码）
    private String encryptedData;

    // 操作核销的抖音门店id（必传）
    private String poiId;
}