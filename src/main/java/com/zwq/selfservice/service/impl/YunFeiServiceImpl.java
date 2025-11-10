package com.zwq.selfservice.service.impl;

import com.zwq.selfservice.service.YunFeiService;
import com.zwq.selfservice.util.SendHttpRequestUtil;
import com.zwq.selfservice.vo.yunfei.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import java.util.Map;


@Service
@Slf4j
@EnableRetry
public class YunFeiServiceImpl implements YunFeiService {

    Triple<String, Long, Long> tokenCache = Triple.of("", 0L, 0L);

    private final SendHttpRequestUtil sendHttpRequestUtil;

    public YunFeiServiceImpl(SendHttpRequestUtil sendHttpRequestUtil) {
        this.sendHttpRequestUtil = sendHttpRequestUtil;
    }

    @Value("${yunfei.url}")
    String yunfeiUrl;

    @Value("${yunfei.appId}")
    String appId;

    @Value("${yunfei.appSecret}")
    String appSecret;

    @Value("${yunfei.mtOpPoiId}")
    String mtOpPoiId;

    @Value("${yunfei.dyOpPoiId}")
    String dyOpPoiId;

    @Retryable(value = {Exception.class}, maxAttempts = 3)
    @Override
    public SubmitResponse appSubmit(SubmitRequest request) {
        log.info("yunfei appSubmit request: {}", request);
        ResponseEntity<SubmitResponse> response = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/partner/apply/app/submit", request,
                SubmitResponse.class, null);
        SubmitResponse body = response.getBody();
        log.info("yunfei appSubmit response: {}", body);
        return body;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3)
    @Override
    public TokenResponse getToken(TokenRequest request) {
        log.info("yunfei getToken request: {}", request);
        ResponseEntity<TokenResponse> response = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/oauth/token", request,
                TokenResponse.class, null);
        TokenResponse body = response.getBody();
        if (body != null) {
            tokenCache = Triple.of(body.getData().getAccessToken(), System.currentTimeMillis(), body.getData().getExpiresIn());
        }
        log.info("yunfei getToken response: {}", body);
        return body;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3)
    @Override
    public BindMtResponse bindMt(BindMtRequest request) {
        log.info("yunfei bindMt request: {}", request);
        ResponseEntity<BindMtResponse> response = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/partner/app/bind/shop", request,
                BindMtResponse.class, buildHeader(), Map.of("access_token", getToken()));
        BindMtResponse body = response.getBody();
        log.info("yunfei bindMt response: {}", body);
        return body;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3)
    @Override
    public BindDyResponse bindDy(BindDyRequest request) {
        log.info("yunfei bindDy request: {}", request);
        ResponseEntity<BindDyResponse> response = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/partner/app/bind/shop", request,
                BindDyResponse.class, buildHeader(), Map.of("access_token", getToken()));
        BindDyResponse body = response.getBody();
        log.info("yunfei bindDy response: {}", body);
        return body;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3)
    @Override
    public void setAppConfig(NotifyUrlRequest request) {
        log.info("yunfei setAppConfig request: {}", request);
        ResponseEntity<String> response = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/partner/set-appconfig", request,
                String.class, buildHeader(), Map.of("access_token", getToken()));
        log.info("yunfei setAppConfig response: {}", response);
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3)
    @Override
    public PageQueryPoiListResponse pageQueryPoiList(PageQueryPoiListRequest request) {
        log.info("yunfei pageQueryPoiList request: {}", request);
        ResponseEntity<PageQueryPoiListResponse> response = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/partner/tuangou/pageQueryPoiList", request,
                PageQueryPoiListResponse.class, buildHeader(), Map.of("access_token", getToken()));
        PageQueryPoiListResponse body = response.getBody();
        log.info("yunfei pageQueryPoiList response: {}", body);
        return body;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3)
    @Override
    public QueryShopDealResponse queryShopDeal(QueryShopDealRequest request) {
        log.info("yunfei queryShopDeal request: {}", request);
        ResponseEntity<QueryShopDealResponse> response = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/partner/tuangou/queryShopDeal", request,
                QueryShopDealResponse.class, buildHeader(), Map.of("access_token", getToken()));
        QueryShopDealResponse body = response.getBody();
        log.info("yunfei queryShopDeal response: {}", body);
        return body;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3)
    @Override
    public ConsumeResponse consume(ConsumeRequest request) {
        if ("douyin".equals(request.getPlatform())){
            request.setOpPoiId(dyOpPoiId);
        }else {
            request.setOpPoiId(mtOpPoiId);
        }
        log.info("yunfei consume request: {}", request);
        ResponseEntity<ConsumeResponse> response = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/partner/tuangou/receipt/consume", request,
                ConsumeResponse.class, buildHeader(), Map.of("access_token", getToken()));
        ConsumeResponse body = response.getBody();
        log.info("yunfei consume query receipt response: {}", body);
        if (body != null && body.getData() !=null && body.getData().getVerifyCount() > 0) {
            request.setType("2");
            ResponseEntity<ConsumeResponse> response1 = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/partner/tuangou/receipt/consume", request,
                    ConsumeResponse.class, buildHeader(), Map.of("access_token", getToken()));
            ConsumeResponse body1 = response1.getBody();
            log.info("yunfei consume response: {}", body1);
            return body1;
        }
        return body;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3)
    @Override
    public QueryShopReviewResponse queryShoPreview(QueryShopReviewRequest request) {
        log.info("yunfei queryShoPreview request: {}", request);
        ResponseEntity<QueryShopReviewResponse> response = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/partner/tuangou/queryShopReview", request,
                QueryShopReviewResponse.class, buildHeader(), Map.of("access_token", getToken()));
        QueryShopReviewResponse body = response.getBody();
        log.info("yunfei queryShoPreview response: {}", body);
        return body;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3)
    @Override
    public String apiBalance(String token) {
        log.info("yunfei apiBalance request token: {}", token);
        ResponseEntity<String> response = sendHttpRequestUtil.post(yunfeiUrl + "/wspace-openapi/partner/api/balance", null,
                String.class, buildHeader(), Map.of("access_token", getToken()));
        String body = response.getBody();
        log.info("yunfei apiBalance response status: {}", response.getStatusCode());
        return body;
    }

    private Map<String,String> buildHeader(){
        return Map.of("Content-Type","application/json");
    }


    private String getToken(){
        long currentTime = System.currentTimeMillis();
        if (tokenCache.getLeft().isEmpty() || currentTime - tokenCache.getMiddle() > (tokenCache.getRight() - 1000)){
            TokenRequest tokenRequest = new TokenRequest();
            tokenRequest.setAppId(appId);
            tokenRequest.setAppSecret(appSecret);
            this.getToken(tokenRequest);
        }
        return tokenCache.getLeft();
    }


}
