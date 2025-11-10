package com.zwq.selfservice.service;

import com.zwq.selfservice.vo.yunfei.*;

public interface YunFeiService {
    
    SubmitResponse appSubmit(SubmitRequest request);

    TokenResponse getToken(TokenRequest request);

    BindMtResponse bindMt(BindMtRequest request);

    BindDyResponse bindDy(BindDyRequest request);

    void setAppConfig(NotifyUrlRequest request);

    PageQueryPoiListResponse pageQueryPoiList(PageQueryPoiListRequest request);

    ConsumeResponse consume(ConsumeRequest request);

    QueryShopDealResponse queryShopDeal(QueryShopDealRequest request);

    QueryShopReviewResponse queryShoPreview(QueryShopReviewRequest request);

    String apiBalance(String token);
    

}
