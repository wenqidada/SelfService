package com.zwq.selfservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// 使用@ConfigurationProperties方式
@Component
@ConfigurationProperties(prefix = "info")
@Data
public class DepositConfig {
    private String desc;
    private Integer deposit;
    private Map<String, Integer> price = new HashMap<>();

    public Integer getBonusAmount(Integer depositAmount) {
        return price.get(String.valueOf(depositAmount));
    }
}