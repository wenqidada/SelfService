package com.zwq.selfservice.service;

import com.wechat.pay.java.service.refund.model.Refund;
import com.zwq.selfservice.util.ResponseData;
import com.zwq.selfservice.vo.WeChatResponseVO;

import java.math.BigDecimal;

public interface WechatService {

    WeChatResponseVO weChatLongin(String code, String id);

    ResponseData wxPay(BigDecimal total, String token);

    boolean wxRefund(String transactionId,String outTradeNo,Integer total);

    ResponseData getWxPay(String token);

    Refund getWxRefund(String refundId);

    String getOrderCache(String token);

}
