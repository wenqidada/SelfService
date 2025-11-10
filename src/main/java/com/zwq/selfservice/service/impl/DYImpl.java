package com.zwq.selfservice.service.impl;

import com.zwq.selfservice.entity.TripartiteTable;
import com.zwq.selfservice.service.TripartiteTableService;
import com.zwq.selfservice.util.SendHttpRequestUtil;
import com.zwq.selfservice.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class DYImpl {

    private final SendHttpRequestUtil sendHttpRequestUtil;
    private final TripartiteTableService tripartiteTableService;


    @Autowired
    public DYImpl(SendHttpRequestUtil sendHttpRequestUtil, TripartiteTableService tripartiteTableService) {
        this.sendHttpRequestUtil = sendHttpRequestUtil;
        this.tripartiteTableService = tripartiteTableService;
    }

    @Value("dy.url.validation")
    String validationUrl;

    @Value("dy.url.verify")
    String verifyUrl;

    @Value("dy.url.token")
    String tokenUrl;

    @Value("dy.client_key")
    String clientKey;

    @Value("dy.client_secret")
    String clientSecret;

    @Value("dy.grant_type")
    String grantType;

    @Value("dy.account_id")
    String accountId;

    @Value("dy.poi_id")
    String poiId;



    public Pair<String,Double> dYWriteOff(String code) {
        // 1. 调用接口获取token
        DyAuthRequest build = DyAuthRequest.builder().clientSecret(clientSecret).clientKey(clientKey).grantType(grantType).build();
        HashMap<String, String> headerMap = new HashMap<>();
        String tokenId = "douyin-"+ UUID.randomUUID();
        headerMap.put("Content-Type", "application/json; charset=UTF-8");
        ResponseEntity<DyAccessTokenResponse> tokenResponse = sendHttpRequestUtil.post(tokenUrl, build, DyAccessTokenResponse.class, headerMap);
        DyAccessTokenResponse body = tokenResponse.getBody();
        if (body != null) {
            if (body.getAccessToken() == null) {
                log.error("抖音获取access_token失败,响应体: {}", body);
            }else {
                headerMap.put("access-token", body.getAccessToken());
                headerMap.put("Rpc-Transit-Life-Account", accountId);
                DyWriteOffRequest certificate = DyWriteOffRequest.builder().accountId(accountId).poiId(poiId).code(code).build();
                ResponseEntity<DyCertificateResponse> dyCertificateResponseResponseEntity = sendHttpRequestUtil.get(validationUrl, certificate, DyCertificateResponse.class, headerMap);
                DyCertificateResponse dyCertificateResponse = dyCertificateResponseResponseEntity.getBody();
                if (dyCertificateResponse == null || dyCertificateResponse.getData().getError_code() != 0L) {
                    log.error("抖音验证券码失败,响应体: {}", dyCertificateResponse);
                }else {
                    DyCertificateResponse.Certificate certificate1 = dyCertificateResponse.getData().getCertificates().get(0);
                    String encryptedCode = certificate1.getEncrypted_code();
                    Long originalAmount = certificate1.getAmount().getOriginal_amount();
                    Long payAmount = certificate1.getAmount().getPay_amount();
                    DyVerifyRequest verifyRequest = DyVerifyRequest.builder()
                            .accountId(accountId)
                            .poiId(poiId)
                            .encryptedCodes(List.of(encryptedCode))
                            .verifyToken(tokenId).build();
                    ResponseEntity<VerifyResponse> verifyResponse = sendHttpRequestUtil.post(verifyUrl, verifyRequest, VerifyResponse.class, headerMap);
                    VerifyResponse verifyBody = verifyResponse.getBody();
                    if (verifyBody == null || verifyBody.getData().getError_code() != 0L){
                        log.error("抖音核销券码失败,响应体: {}", verifyBody);
                    }else {
                        // 保存核销记录
                        TripartiteTable tripartiteTable = new TripartiteTable();
                        tripartiteTable.setRequestId(tokenId);
                        tripartiteTable.setCreateTime(LocalDateTime.now());
                        tripartiteTable.setCodes(code);
                        tripartiteTable.setEncryptedCodes(encryptedCode);
                        tripartiteTable.setPlatformType((byte) 1);
                        tripartiteTable.setAmount(BigDecimal.valueOf(originalAmount));
                        tripartiteTable.setActualPayment(BigDecimal.valueOf(payAmount));
                        tripartiteTableService.save(tripartiteTable);
                        log.info("抖音核销券码成功,响应体: {}", verifyBody);
                        return Pair.of(certificate1.getSku().getTitle(),payAmount/100.0);
                    }
                }
            }
        }
        return Pair.of("",0.0);
    }
}
