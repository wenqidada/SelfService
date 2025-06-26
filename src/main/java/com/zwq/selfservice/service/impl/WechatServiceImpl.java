package com.zwq.selfservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zwq.selfservice.entity.WechatTable;
import com.zwq.selfservice.service.WechatTableService;
import com.zwq.selfservice.util.SendHttpRequestUtil;
import com.zwq.selfservice.vo.WeChatLoginRequest;
import com.zwq.selfservice.vo.WeChatLoginResponse;
import com.zwq.selfservice.vo.WeChatResponseVO;
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

    public boolean wxPay(String code) {
        return true;
    }

    public boolean wxRefund(String code) {

        return  true;
    }
}
