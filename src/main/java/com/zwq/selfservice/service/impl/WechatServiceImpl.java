package com.zwq.selfservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.*;
import com.zwq.selfservice.entity.WechatTable;
import com.zwq.selfservice.service.WechatTableService;
import com.zwq.selfservice.util.SendHttpRequestUtil;
import com.zwq.selfservice.vo.WeChatLoginRequest;
import com.zwq.selfservice.vo.WeChatLoginResponse;
import com.zwq.selfservice.vo.WeChatResponseVO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;



@Slf4j
@Service
public class WechatServiceImpl {

    private final WechatTableService wechatService;
    private final SendHttpRequestUtil sendHttpRequestUtil;

    @Autowired
    public WechatServiceImpl(WechatTableService wechatService, SendHttpRequestUtil sendHttpRequestUtil) {
        this.wechatService = wechatService;
        this.sendHttpRequestUtil = sendHttpRequestUtil;
    }

    @Value("${wx.merchantId}")
    public static String merchantId;

    @Value("${wx.privateKeyPath}")
    public static String privateKeyPath;

    @Value("${wx.merchantSerialNumber}")
    public static String merchantSerialNumber;

    @Value("${wx.apiV3Key}")
    public static String apiV3Key;

    @Value("${wx.sp_mchid}")
    public static String spMchid;

    public static JsapiService service;

    public static RefundService refundService;

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.secret}")
    private String secret;

    @Value("${wx.login_url}")
    private String loginUrl;

    public WeChatResponseVO weChatLongin(String code, String id){

        if (id != null){
            WechatTable wechatTable = wechatService.getById(id);
            if (wechatTable != null) {
                log.info("登录微信用户，openid: {}", wechatTable.getOpenId());
                return WeChatResponseVO.builder().id(String.valueOf(wechatTable.getId())).token(wechatTable.getToken()).build();
            }
        }
        WeChatLoginRequest build = WeChatLoginRequest.builder().appid(appid).secret(secret).jsCode(code).build();
        ResponseEntity<WeChatLoginResponse> weChatLoginResponseVOResponseEntity = sendHttpRequestUtil.get(loginUrl, build, WeChatLoginResponse.class, null);
        WeChatLoginResponse body = weChatLoginResponseVOResponseEntity.getBody();
        if (weChatLoginResponseVOResponseEntity.getStatusCode().is2xxSuccessful()) {
            if (body != null && body.getOpenId() != null) {
                WechatTable wechatTable = new WechatTable();
                wechatTable.setOpenId(body.getOpenId());
                wechatTable.setSessionKey(body.getSessionKey());
                wechatTable.setToken(UUID.randomUUID().toString());
                wechatTable.setLoginTime(LocalDateTime.now());
                wechatTable.setCreateTime(LocalDateTime.now());
                wechatService.save(wechatTable);
                log.info("微信登录成功，openid: {}", body.getOpenId());
                QueryWrapper<WechatTable> wrapper = new QueryWrapper<>();
                wrapper.eq("open_id",wechatTable.getOpenId()).eq("token",wechatTable.getToken());
                WechatTable loginData = wechatService.getOne(wrapper);
                return WeChatResponseVO.builder().id(String.valueOf(loginData.getId())).token(loginData.getToken()).build();
            } else {
                log.error("微信登录失败，响应体为空或openid为空");
                return WeChatResponseVO.builder().build();
            }
        } else {
            if (body != null) {
                log.error("微信登录请求失败，状态码: {},错误信息为: {} ,网络状态码为 :{}",
                        body.getErrCode(), body.getErrMsg(), weChatLoginResponseVOResponseEntity.getStatusCode());
            }
            return WeChatResponseVO.builder().build();
        }
    }

    public String wxPay(Integer total,String openid) {
        PrepayResponse prepay = null;
        String outTradeNo = "QI-PAY" + UUID.randomUUID().toString().replace("-", "");
        try {
            PrepayRequest prepayRequest = createPrepayRequest(total, openid,outTradeNo);
            prepay = service.prepay(prepayRequest);
            log.error("微信支付预支付请求失败，响应体为空");
        } catch (HttpException e) { // 发送HTTP请求失败
            log.error("微信支付Http请求异常: {}", e.getHttpRequest());
        } catch (ServiceException e) { // 服务返回状态小于200或大于等于300，例如500
            log.error("微信支付请求异常: {}, 状态码: {}, 错误信息: {}",
                     e.getResponseBody(),e.getErrorCode(), e.getErrorMessage());
        } catch (MalformedMessageException e) {
            log.error("微信支付请求异常: {}", e.getMessage());
        } catch (Exception e){
            log.error("微信支付异常: {}", e.getMessage());
        }
        if (prepay != null) {
            log.info("微信支付预支付成功，prepay:{},openid: {}", prepay, openid);
            return "prepay_id="+prepay.getPrepayId();
        }else {
            CloseOrderRequest closeOrderRequest = new CloseOrderRequest();
            closeOrderRequest.setMchid(spMchid);
            closeOrderRequest.setOutTradeNo(outTradeNo);
            service.closeOrder(closeOrderRequest);
            log.error("微信支付预支付失败，关闭订单，商户订单号: {}", outTradeNo);
        }
        return "";
    }

    public boolean wxRefund(String transactionId,String outTradeNo,Integer total) {
        try {
            CreateRequest refundRequest = createRefundRequest(transactionId,outTradeNo,total);
            Refund response = refundService.create(refundRequest);
            if (response != null && response.getRefundId() != null) {
                log.info("微信退款发起成功，交易事务号: {}, 退款金额: {},退款去向: {}",
                        response.getTransactionId(), response.getAmount().getTotal(),response.getUserReceivedAccount());
                return true;
            } else {
                log.error("微信退款请求失败，响应体为空或交易信息为空");
            }
        } catch (HttpException e) { // 发送HTTP请求失败
            log.error("微信退款Http请求异常: {}", e.getHttpRequest());
        } catch (ServiceException e) { // 服务返回状态小于200或大于等于300，例如500
            log.error("微信退款请求异常: {}, 状态码: {}, 错误信息: {}",
                    e.getResponseBody(),e.getErrorCode(), e.getErrorMessage());
        } catch (MalformedMessageException e) {
            log.error("微信退款请求异常: {}", e.getMessage());
        } catch (Exception e){
            log.error("微信退款异常: {}", e.getMessage());
        }
        return  false;
    }

    @PostConstruct
    private void initConfig(){
        // 使用 com.wechat.pay.java.core.util 中的函数从本地文件中加载商户私钥，商户私钥会用来生成请求的签名
        Config config = new RSAAutoCertificateConfig.Builder()
                .merchantId(merchantId)
                // 使用 com.wechat.pay.java.core.util 中的函数从本地文件中加载商户私钥，商户私钥会用来生成请求的签名
                .privateKeyFromPath(privateKeyPath)
                .merchantSerialNumber(merchantSerialNumber)
                .apiV3Key(apiV3Key)
                .build();
        service = new JsapiService.Builder().config(config).build();
        refundService = new RefundService.Builder().config(config).build();
    }


    private CreateRequest createRefundRequest(String transactionId,String outTradeNo,Integer total){
        CreateRequest request = new CreateRequest();
        request.setSubMchid(spMchid);
        //微信支付订单号
        request.setTransactionId(transactionId);
        //商户订单号
        request.setOutTradeNo(outTradeNo);
        request.setOutRefundNo("QI-REFUND" + UUID.randomUUID().toString().replace("-", ""));
        request.setReason("商品已售完");
        request.setNotifyUrl("https://weixin.qq.com/tripartite/wx/refund/callback");
        request.setFundsAccount(ReqFundsAccount.AVAILABLE);
        request.setAmount(new AmountReq());
        request.getAmount().setRefund(Long.valueOf(total));
        request.getAmount().setTotal(Long.valueOf(total));
        request.getAmount().setCurrency("CNY");
        return  request;
    }


    private PrepayRequest createPrepayRequest(Integer total,String openid,String outTradeNo) {
        PrepayRequest request = new PrepayRequest();
        request.setAppid(appid);
        request.setMchid(spMchid);
        request.setDescription("Qi台球馆-消费");
        request.setOutTradeNo(outTradeNo);
        request.setNotifyUrl("https://weixin.qq.com/tripartite/wx/pay/callback");
        Amount amount = new Amount();
        amount.setCurrency("CNY");
        amount.setTotal(total);
        Payer payer = new Payer();
        payer.setOpenid(openid);
        request.setPayer(payer);
        request.setAmount(amount);
        return request;
    }

    public Transaction getWxPay(String transactionId) {
        QueryOrderByIdRequest queryOrderByIdRequest = new QueryOrderByIdRequest();
        queryOrderByIdRequest.setMchid(spMchid);
        queryOrderByIdRequest.setTransactionId(transactionId);
        Transaction transaction = service.queryOrderById(queryOrderByIdRequest);
        log.info("微信支付查询结果: {}", transaction);
        return transaction;
    }

    public Refund getWxRefund(String refundId) {
        QueryByOutRefundNoRequest queryByOutRefundNoRequest = new QueryByOutRefundNoRequest();
        queryByOutRefundNoRequest.setOutRefundNo(refundId);
        queryByOutRefundNoRequest.setSubMchid(spMchid);
        Refund refund = refundService.queryByOutRefundNo(queryByOutRefundNoRequest);
        log.info("微信退款查询结果: {}", refund);
        return refund;
    }
}
