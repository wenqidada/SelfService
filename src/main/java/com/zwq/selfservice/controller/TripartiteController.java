package com.zwq.selfservice.controller;

import com.zwq.selfservice.service.impl.DYImpl;
import com.zwq.selfservice.service.impl.MTImpl;
import com.zwq.selfservice.service.impl.WechatServiceImpl;
import com.zwq.selfservice.vo.WeChatResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @RequestMapping(method= RequestMethod.GET,path = "/login")
    public WeChatResponseVO weChatLongin(String code, String id){
        log.info("微信登录接口被调用,code: {},token: {}", code,id);
        return wechatServiceImpl.weChatLongin(code,id);
    }


    @RequestMapping(method= RequestMethod.GET,path = "/mt")
    public boolean mtWriteOff(String code){
        log.info("美团核销券码,code: {}", code);
        return mTImpl.mtWriteOff(code);
    }


    @RequestMapping(method= RequestMethod.GET,path = "/dy")
    public boolean dyWriteOff(String code){
        log.info("美团核销券码,code: {}", code);
        return dYImpl.dYWriteOff(code);
    }



}
