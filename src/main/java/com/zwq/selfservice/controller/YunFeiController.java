package com.zwq.selfservice.controller;

import com.zwq.selfservice.service.YunFeiService;
import com.zwq.selfservice.vo.yunfei.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/yf")
@Slf4j
public class YunFeiController {

    private final YunFeiService yunFeiService;

    public YunFeiController(YunFeiService yunFeiService) {
        this.yunFeiService = yunFeiService;
    }

    @PostMapping("/app_submit")
    public SubmitResponse appSubmit(SubmitRequest request){
        return yunFeiService.appSubmit(request);
    }

    @PostMapping("/token")
    public TokenResponse getToken(TokenRequest request){
        return yunFeiService.getToken(request);
    }

    @PostMapping("/bind_mt")
    public BindMtResponse bindMt(BindMtRequest request){
        return yunFeiService.bindMt(request);
    }

    @PostMapping("/bind_dy")
    public BindDyResponse bindDy(BindDyRequest request){
        return yunFeiService.bindDy(request);
    }

    @PostMapping("/set_app_config")
    public void setAppConfig(NotifyUrlRequest request){
        yunFeiService.setAppConfig(request);
    }

    @PostMapping("/page_query_poiList")
    public PageQueryPoiListResponse pageQueryPoiList(PageQueryPoiListRequest request){
        return yunFeiService.pageQueryPoiList(request);
    }

    @PostMapping("/consume")
    public ConsumeResponse consume(ConsumeRequest request){
        return yunFeiService.consume(request);
    }

    @PostMapping("/query_shop_deal")
    public QueryShopDealResponse queryShopDeal(QueryShopDealRequest request){
        return yunFeiService.queryShopDeal(request);
    }

    @PostMapping("/query_shop_review")
    public QueryShopReviewResponse queryShoPreview(QueryShopReviewRequest request){
        return yunFeiService.queryShoPreview(request);
    }

    @PostMapping("/api_balance")
    public String apiBalance(String token){
        return yunFeiService.apiBalance(token);
    }


}
