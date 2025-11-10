package com.zwq.selfservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.util.NonceUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.*;
import com.zwq.selfservice.entity.TripartiteTable;
import com.zwq.selfservice.entity.WechatTable;
import com.zwq.selfservice.service.TripartiteTableService;
import com.zwq.selfservice.service.WechatService;
import com.zwq.selfservice.service.WechatTableService;
import com.zwq.selfservice.util.ResponseData;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class WechatServiceImpl implements WechatService {

    private final WechatTableService wechatService;
    private final SendHttpRequestUtil sendHttpRequestUtil;
    private final TripartiteTableService tripartiteTableService;
    private Config config;

    private final Cache<String, String> orderCache = Caffeine.newBuilder()
            .maximumSize(100)
            .build();

    @Autowired
    public WechatServiceImpl(WechatTableService wechatService, SendHttpRequestUtil sendHttpRequestUtil, TripartiteTableService tripartiteTableService) throws UnknownHostException {
        this.wechatService = wechatService;
        this.sendHttpRequestUtil = sendHttpRequestUtil;
        this.tripartiteTableService = tripartiteTableService;
    }

    @Value("${wx.merchantId}")
    public String merchantId;

    @Value("${wx.privateKeyPath}")
    public String privateKeyPath;

    @Value("${wx.merchantSerialNumber}")
    public String merchantSerialNumber;

    @Value("${wx.api_v3_key}")
    public String apiV3Key;

    @Value("${wx.sp_mchid}")
    public String spMchid;

    @Value("${server.port}")
    String port;

    @Value("${info.deposit}")
    int deposit;

    String ip = InetAddress.getLocalHost().getHostAddress();


    public static JsapiService service;

    public static RefundService refundService;

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.secret}")
    private String secret;

    @Value("${wx.login_url}")
    private String loginUrl;


    public WeChatResponseVO weChatLongin(String code, String token){
        WeChatLoginRequest build = WeChatLoginRequest.builder().appid(appid).secret(secret).js_Code(code).build();
        ResponseEntity<String> weChatLoginResponseVOResponseEntity = sendHttpRequestUtil.get(loginUrl, build, String.class, null);
        WeChatLoginResponse body = JSON.parseObject(weChatLoginResponseVOResponseEntity.getBody(),WeChatLoginResponse.class);
        if (weChatLoginResponseVOResponseEntity.getStatusCode().is2xxSuccessful()) {
            if (body != null && body.getOpenId() != null) {
                WechatTable wechatTable;
                wechatTable = wechatService.getOne(new QueryWrapper<WechatTable>().eq("open_id", body.getOpenId()));
                if (wechatTable != null && wechatTable.getOpenId() != null){
                    return WeChatResponseVO.builder().id(String.valueOf(wechatTable.getId())).token(wechatTable.getToken()).code(200).build();
                }else {
                    wechatTable = new WechatTable();
                    wechatTable.setOpenId(body.getOpenId());
                    wechatTable.setSessionKey(body.getSession_Key());
                    wechatTable.setToken(UUID.randomUUID() + "QI");
                    wechatTable.setLoginTime(LocalDateTime.now());
                    wechatTable.setCreateTime(LocalDateTime.now());
                    wechatService.save(wechatTable);
                }
                log.info("微信登录成功，openid: {}", body.getOpenId());
                QueryWrapper<WechatTable> wrapper = new QueryWrapper<>();
                wrapper.eq("open_id",wechatTable.getOpenId()).eq("token",wechatTable.getToken());
                WechatTable loginData = wechatService.getOne(wrapper);
                return WeChatResponseVO.builder().id(String.valueOf(loginData.getId())).token(loginData.getToken()).code(200).build();
            } else {
                log.error("微信登录失败，响应体为空或openid为空");
                return WeChatResponseVO.builder().code(400).build();
            }
        } else {
            if (body != null) {
                log.error("微信登录请求失败，状态码: {},错误信息为: {} ,网络状态码为 :{}",
                        body.getErrCode(), body.getErrMsg(), weChatLoginResponseVOResponseEntity.getStatusCode());
            }
            return WeChatResponseVO.builder().code(400).build();
        }
    }

    public ResponseData wxPay(BigDecimal price,String token) {
        ResponseData responseData = new ResponseData();
        responseData.setMessage("获取支付参数失败");
        responseData.setCode(400);
        //todo 押金100元
        int total = price == null ? deposit : price.multiply(BigDecimal.valueOf(100)).intValue();
        PrepayResponse prepay = null;
        PrepayWithRequestPaymentResponse response = new PrepayWithRequestPaymentResponse();
        String outTradeNo = "QI-PAY" + createId();
        try {
            PrepayRequest prepayRequest = createPrepayRequest(total, token,outTradeNo);
            log.info("微信支付请求体======={}",prepayRequest);
            prepay = service.prepay(prepayRequest);
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
            if (price == null){
                //支付押金为新开台,删除之前缓存订单
                orderCache.invalidate(token);
                orderCache.put(token + "deposit",outTradeNo);
            }else {
                orderCache.put(token,outTradeNo);
            }
            try {
                TripartiteTable tripartiteTable = new TripartiteTable();
                tripartiteTable.setActualPayment(BigDecimal.valueOf(total).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP));
                tripartiteTable.setCreateTime(LocalDateTime.now());
                tripartiteTable.setCodes(outTradeNo);
                tripartiteTable.setPlatformType((byte)3);
                tripartiteTable.setRequestId(token);
                if (price == null){
                    tripartiteTable.setEncryptedCodes("deposit");
                }
                tripartiteTableService.save(tripartiteTable);
            }catch (Exception ignored){
            }
            log.info("微信支付预支付成功，prepay:{},openid: {}", prepay, token);
            response.setAppId(appid);
            response.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));
            response.setNonceStr(NonceUtil.createNonce(32));
            response.setPackageVal("prepay_id=" + prepay.getPrepayId());
            response.setSignType("RSA");
            String message =
                    response.getAppId() + "\n" + response.getTimeStamp() + "\n" + response.getNonceStr() + "\n" + response.getPackageVal() + "\n";
            String signer = config.createSigner().sign(message).getSign();
            response.setPaySign(signer);
            log.info("微信支付预支付请求成功，返回参数: {}", response);
            responseData.setCode(200);
            responseData.setData(response);
            responseData.setMessage("获取支付参数成功");
        }else {
            CloseOrderRequest closeOrderRequest = new CloseOrderRequest();
            closeOrderRequest.setMchid(spMchid);
            closeOrderRequest.setOutTradeNo(outTradeNo);
            service.closeOrder(closeOrderRequest);
            log.error("微信支付预支付失败，关闭订单，商户订单号: {}", outTradeNo);
        }
        return responseData;
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
        config = new RSAPublicKeyConfig.Builder()
                .merchantId(merchantId)
                // 使用 com.wechat.pay.java.core.util 中的函数从本地文件中加载商户私钥，商户私钥会用来生成请求的签名
                .privateKey("""
                        -----BEGIN PRIVATE KEY-----
                           MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCjk3tVlTLMw540
                           jvwnZPdAhoU/JHcPpX0srsYH9HV0mS+YGQrf7pdOmQezH82qjzAZyUHeU82VdFyh
                           C+bRKHdB/xQLx00Dqyr5QOK5Q0Lvg4eaJCDMeCN/emWG3uLWJxQkvheCM065+246
                           7xIQH22v8uRCurw3SuMdVzAhUnw5iUwG6uaub2C2JBivZT8PkuK1GSRTurhBAwCo
                           pQitY2xMLLooNSJUbQ2peBU9F5S6z4BakbyzAVBZyCOCDCCTzOCYzET6Uvdw16zg
                           rrXzV1sa23IaD8koDkkZONbCrpAeJyQzsEulaqVtdclRAI95UO20tBV/Pb/7yCqS
                           2Ucb6/pJAgMBAAECggEAIE4NKo7HtJbhlMG0XNdc2Vp30EuUU/u75+owY/vMgpWK
                           R8CSXD1Tlos0cRTT2l5jAoSzxIPKczzC2m9uS1pxixAkVlsrA5F3cDh3Tl1RR9oz
                           GqcN7zO+1vtGN8enBuI64TLFk8osPyC/2VXDvHlogTo4CFG2wDjC99VzRPF0ZSw+
                           dCejOLpeWbB6W4M5l3z7ABi/V9S6KeucUcRVxfPcHlzZRJ6ALYTDKozWCF0Is70K
                           TTr14hdFMP2g3drWtbEEJ9aywBUg+iZkvr1x0S9MTueI6RZSzvPlPSMF8+Ec9wzs
                           qFD8QKxjXu8sDIROyxVkf+TyI6EUQ6SrVt5v9sa3vQKBgQDWXBaXiFWzmJlUlJD+
                           k9ftL96GePBQUVD+FvU4CCufRo6AcCqIzaBvkNcaznCIz8WLCendCCOBPRL8HEKL
                           bYIm2KnA4OnTzpang5SkNJUNTP3p1xnd2N09RhcLBrY62qZ9oqMY/ap9npeAgHUn
                           FgknQpbDY1THbIM8cbkLhHf3swKBgQDDWfiJcz8i29v6Ub50DYy+3pNqcACUtAbH
                           OXqod+5mQ/ZUHVqrU8pUVYI12DEumGMuQOgN7flBuflqBgsAH7X9hC/+YKg7rfnC
                           WYSgsg7/TBQWJN9BJRZ/uq4RJAvHZ71q6IWAoYb+RAdOdTKsQv9QJEZ14L8j8Mf8
                           hJRMqB0IEwKBgQDDDwEv2svBpbbBFH/saLQwDUM8sohbTeOZk2KJHkYzpYy+q1LJ
                           MfQwEWjr57bXh9mGb/rzOxiz3dOMvlp/baLlnnq6HPAp4O+Od1mnIoIPnGPhie7r
                           xQ9tVn5Zdj4UyCqAYKJxd0LRFKNgN4RjkNW7eAigu57F6aRyODnelShlqQKBgFwB
                           Oc7yml++kMI6Uaqy3wDeLyvICF4A09hst9o0cfF0PMWs9ZEPa5GAnEzjv3Yki0Gz
                           jBdTZzuPbWqD4hZy2PHHmhsCRvmknKbuJkrQ0cNnf2ZWg1dZMlmCAI1OHsYRk+KQ
                           FKBacwu52n8XL4B2JW0HSLAn4533vxitEHNyEgpFAoGATvwehhWDuTTrS592LuBa
                           8mreFLqik37VnMjrC69JlFfT0r/i7ONRrweRFMCH1Z+d99pzijEwJNHpo+d1/eow
                           ZN+5SNj0cPvEMe/pNIT9ReRm6U8Ohy9IasVYBz3Fb6EcuwBGC3FO8NBU9kZuVdIf
                           PoK0ReNUOGyn74aC8gQbKGY=
                           -----END PRIVATE KEY-----
                     """).publicKey("""
                        -----BEGIN PUBLIC KEY-----
                        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4QSzJazaq+2FfvlVXOyo
                        EZWWlUlZOCVZsZxW+wOVRGWLGvY88+EPQxXZkGy6YRC2kWQpWiFR/b6wMZYb74dJ
                        KhYw94v1dABX3U1EoA78jJdkA9vDxX1sPEjcIujdse6sOjZnOZPVYXJiKDPZ48q/
                        CkSA97eRXfeMaPVCXkshLgC6f3GKoPlmctWajJ097Tczh9iZkyGdEPuzuPt5Ba00
                        9Cd4YPsuPhIVGLVQ46hla+2E1bNP5hg96Ca/DZcXChbTpOSb2gKWQEty0Qw2hYOk
                        2peL8k4kjeJUTFC2mwio1X21aRMqtogXGVEfNjghFWnjQ1NQNFH701giZxtamqIx
                        3QIDAQAB
                        -----END PUBLIC KEY-----
                        """).publicKeyId("PUB_KEY_ID_0117249439012025081600382214000200")
                .merchantSerialNumber(merchantSerialNumber)
                .apiV3Key(apiV3Key)
                .build();
        service = new JsapiService.Builder().config(config).build();
        refundService = new RefundService.Builder().config(config).build();
    }


    private CreateRequest createRefundRequest(String transactionId,String outTradeNo,Integer total) throws UnknownHostException {
        CreateRequest request = new CreateRequest();
        //微信支付订单号
        request.setTransactionId(transactionId);
        //商户订单号
        request.setOutTradeNo(outTradeNo);
        request.setOutRefundNo("QI-REFUND" + createId());
        request.setReason("商品已售完");
        request.setNotifyUrl("http://" + ip + ":" + port +"/tripartite/wx/refund/callback");
        request.setFundsAccount(ReqFundsAccount.AVAILABLE);
        request.setAmount(new AmountReq());
        request.getAmount().setRefund(Long.valueOf(total));
        request.getAmount().setTotal(Long.valueOf(total));
        request.getAmount().setCurrency("CNY");
        return  request;
    }


    private PrepayRequest createPrepayRequest(Integer total,String token,String outTradeNo) {
        WechatTable userInfo = wechatService.getOne(new QueryWrapper<WechatTable>().eq("token", token));
        PrepayRequest request = new PrepayRequest();
        request.setAppid(appid);
        request.setMchid(spMchid);
        request.setDescription("Qi台球馆-消费");
        request.setOutTradeNo(outTradeNo);
        request.setNotifyUrl("http://" + ip + ":" + port + "/tripartite/wx/pay/callback");
        Amount amount = new Amount();
        amount.setCurrency("CNY");
        amount.setTotal(total);
        Payer payer = new Payer();
        payer.setOpenid(userInfo.getOpenId());
        request.setPayer(payer);
        request.setAmount(amount);
        return request;
    }

    public ResponseData getWxPay(String token) {
        ResponseData responseData = new ResponseData();
        String transactionId = getOrderCache(token);
        if (transactionId == null){
            List<TripartiteTable> list = tripartiteTableService.list(new QueryWrapper<TripartiteTable>()
                    .eq("request_id", token)
                    .orderBy(true,false,"create_time"));
            transactionId = list.get(0).getCodes();
        }
        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        request.setMchid(spMchid);
        request.setOutTradeNo(transactionId);
        Transaction transaction = service.queryOrderByOutTradeNo(request);
        log.info("微信支付查询结果: {}", transaction);
        if (Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
            responseData.setCode(200);
            responseData.setMessage("支付成功");
            responseData.setData(Boolean.TRUE);
        }else {
            responseData.setMessage("支付未成功");
            responseData.setCode(400);
            responseData.setData(Boolean.FALSE);
        }
        return responseData;
    }

    public Refund getWxRefund(String refundId) {
        QueryByOutRefundNoRequest queryByOutRefundNoRequest = new QueryByOutRefundNoRequest();
        queryByOutRefundNoRequest.setOutRefundNo(refundId);
        queryByOutRefundNoRequest.setSubMchid(spMchid);
        Refund refund = refundService.queryByOutRefundNo(queryByOutRefundNoRequest);
        log.info("微信退款查询结果: {}", refund);
        return refund;
    }


    public String getOrderCache(String token){
        String ifPresent = orderCache.getIfPresent(token);
        if (ifPresent == null ){
            ifPresent = orderCache.getIfPresent(token + "deposit");
        }
        log.info("获取退款订单信息====={},token ====={}",ifPresent,token);
        return ifPresent;
    }

    private String createId(){
        UUID uuid = UUID.randomUUID();
        byte[] bytes = new byte[16];
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) bytes[i] = (byte) (msb >>> (8 * (7 - i)));
        for (int i = 8; i < 16; i++) bytes[i] = (byte) (lsb >>> (8 * (15 - i)));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
