package com.zwq.selfservice.controller;


import com.meituan.sdk.internal.exceptions.MtSdkException;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.refund.model.Refund;
import com.zwq.selfservice.service.impl.DYImpl;
import com.zwq.selfservice.service.impl.MTImpl;
import com.zwq.selfservice.service.impl.WechatServiceImpl;
import com.zwq.selfservice.util.ResponseData;
import com.zwq.selfservice.vo.WeChatResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RestController
@RequestMapping("/tripartite")
@Slf4j
public class TripartiteController {

    private final WechatServiceImpl wechatServiceImpl;

    private final MTImpl mTImpl;

    private final DYImpl dYImpl;

    @Autowired
    public TripartiteController(WechatServiceImpl wechatServiceImpl, MTImpl mTImpl, DYImpl dYImpl) {
        this.wechatServiceImpl = wechatServiceImpl;
        this.mTImpl = mTImpl;
        this.dYImpl = dYImpl;
    }

    /**
     * 获取微信登录信息
     */
    @RequestMapping(method= RequestMethod.GET,path = "/wx/login")
    public WeChatResponseVO weChatLongin(String code, String token){
        log.info("微信登录接口被调用,code: {},token: {}", code,token);
        return wechatServiceImpl.weChatLongin(code,token);
    }


    @RequestMapping(method= RequestMethod.GET,path = "/mt/verify")
    public Pair<String, Double> mtWriteOff(String code) throws MtSdkException {
        log.info("美团核销券码,code: {}", code);
        return mTImpl.mtWriteOff(code);
    }


    @RequestMapping(method= RequestMethod.GET,path = "/dy/verify")
    public Pair<String, Double> dyWriteOff(String code){
        log.info("抖音核销券码,code: {}", code);
        return dYImpl.dYWriteOff(code);
    }


    @RequestMapping(method= RequestMethod.GET,path = "/wx/pay")
    public ResponseData wxPay(BigDecimal total, String token){
        log.info("微信支付,金额为: {},用户ID:{}",total,token);
        return wechatServiceImpl.wxPay(total, token);
    }

    @RequestMapping(method= RequestMethod.GET,path = "/wx/refund")
    public boolean wxRefund(String transactionId,String outTradeNo,Integer total){
        log.info("微信退款,订单Id: {}, 商户Id :{}, 退款金额:{}", transactionId, outTradeNo, total);
        return wechatServiceImpl.wxRefund(transactionId,outTradeNo, total);
    }

    @RequestMapping(method= RequestMethod.POST,path = "/wx/pay/callback")
    public void wxPayCallback(String xmlData) {
        log.info("微信支付回调,xmlData: {}", xmlData);
    }

    @RequestMapping(method= RequestMethod.POST,path = "/wx/refund/callback")
    public void wxRefundCallback(String xmlData) {
        log.info("微信退款回调,xmlData: {}", xmlData);
    }


    @RequestMapping(method= RequestMethod.GET,path = "/wx/payInfo")
    public ResponseData getWxPay(String token, String tableId,@RequestHeader("token") String headerToken){
        String getToken;
        if (headerToken == null){
            getToken = token;
        }else {
            getToken = headerToken;
        }
        log.info("获取微信支付信息,tableId========={},token==========={}",tableId,getToken);
        return wechatServiceImpl.getWxPay(getToken);
    }

    @RequestMapping(method= RequestMethod.GET,path = "/wx/refundInfo")
    public Refund getWxRefund(String refundId){
        log.info("获取微信退款信息====================");
        return wechatServiceImpl.getWxRefund(refundId);
    }

    @RequestMapping(method = RequestMethod.GET,path = "/test")
    public PrepayWithRequestPaymentResponse test(){
        PrepayWithRequestPaymentResponse response = new PrepayWithRequestPaymentResponse();
        response.setPackageVal("11111");
        response.setAppId("123456");
        return response;
    }


}
