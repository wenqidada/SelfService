package com.zwq.selfservice.service.impl;


import com.meituan.sdk.DefaultMeituanClient;
import com.meituan.sdk.MeituanClient;
import com.meituan.sdk.MeituanResponse;
import com.meituan.sdk.internal.exceptions.MtSdkException;
import com.meituan.sdk.model.ddzh.tuangou.tuangouReceiptConsume.TuangouReceiptConsumeRequest;
import com.meituan.sdk.model.ddzh.tuangou.tuangouReceiptConsume.TuangouReceiptConsumeResponse;
import com.meituan.sdk.model.ddzh.tuangou.tuangouReceiptPrepare.TuangouReceiptPrepareRequest;
import com.meituan.sdk.model.ddzh.tuangou.tuangouReceiptPrepare.TuangouReceiptPrepareResponse;
import com.zwq.selfservice.entity.TripartiteTable;
import com.zwq.selfservice.service.TripartiteTableService;
import com.zwq.selfservice.util.SendHttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class MTImpl {

    private final TripartiteTableService tripartiteTableService;


    @Autowired
    public MTImpl(TripartiteTableService tripartiteTableService) {
        this.tripartiteTableService = tripartiteTableService;
    }

    @Value("meituan.appAuthToken")
    private String appAuthToken;

    @Value("meituan.signKey")
    private String signKey;

    @Value("meituan.developerId")
    private String developerId;

    public boolean mtWriteOff(String code) throws MtSdkException {

        MeituanClient meituanClient = DefaultMeituanClient.builder(Long.valueOf(developerId), signKey).build();

        TuangouReceiptPrepareRequest tuangouReceiptPrepareRequest = new TuangouReceiptPrepareRequest();

        tuangouReceiptPrepareRequest.setReceiptCode("code");

        MeituanResponse<TuangouReceiptPrepareResponse> response = meituanClient.invokeApi(tuangouReceiptPrepareRequest, appAuthToken);

        if (response.isSuccess()) {
            TuangouReceiptPrepareResponse resp = response.getData();
            log.info("美团验证券码接口成功返回====={}",resp);
            MeituanClient client = DefaultMeituanClient.builder(Long.valueOf(developerId), signKey).build();

            TuangouReceiptConsumeRequest tuangouReceiptConsumeRequest = new TuangouReceiptConsumeRequest();

            String requestId = "meituan-"+ UUID.randomUUID();
            tuangouReceiptConsumeRequest.setReceiptCode(code);
            tuangouReceiptConsumeRequest.setCount(1);
            tuangouReceiptConsumeRequest.setRequestId(requestId);
            tuangouReceiptConsumeRequest.setAppShopAccountName("string");
            tuangouReceiptConsumeRequest.setAppShopAccount("string");

            MeituanResponse<TuangouReceiptConsumeResponse> verifyResponse = client.invokeApi(tuangouReceiptConsumeRequest, appAuthToken);

            if (verifyResponse.isSuccess()) {
                TuangouReceiptPrepareResponse verifyResp = response.getData();
                TripartiteTable tripartiteTable = new TripartiteTable();
                tripartiteTable.setRequestId(requestId);
                tripartiteTable.setCreateTime(LocalDateTime.now());
                tripartiteTable.setCodes(code);
                tripartiteTable.setEncryptedCodes(code);
                tripartiteTable.setPlatformType((byte)2);
                tripartiteTable.setAmount(BigDecimal.valueOf(verifyResp.getDealMarketPrice()));
                tripartiteTable.setActualPayment(BigDecimal.valueOf(verifyResp.getDealPrice()));
                tripartiteTableService.save(tripartiteTable);
                log.info("美团核销券码接口成功返回====={}",verifyResp);
                return true;
            } else {
                log.error("美团核销券码调用失败===={}",verifyResponse);
                return false;
            }
        } else {
            log.error("美团验证券码失败===={}", response);
            return false;
        }

    }
    

}
