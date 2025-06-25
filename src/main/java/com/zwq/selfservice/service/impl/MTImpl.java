package com.zwq.selfservice.service.impl;


import com.zwq.selfservice.service.TripartiteTableService;
import com.zwq.selfservice.util.SendHttpRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MTImpl {

    private final TripartiteTableService tripartiteTableService;
    private final SendHttpRequestUtil sendHttpRequestUtil;

    @Autowired
    public MTImpl(TripartiteTableService tripartiteTableService, SendHttpRequestUtil sendHttpRequestUtil) {
        this.tripartiteTableService = tripartiteTableService;
        this.sendHttpRequestUtil = sendHttpRequestUtil;
    }

    @Value("meituan.appAuthToken")
    private String appAuthToken;

    @Value("meituan.signKey")
    private String signKey;

    @Value("meituan.deviceId")
    private Long developerId;

    @Value("meituan.url.validation")
    private String validationUrl;

    @Value("meituan.url.verify")
    private String verifyUrl;

    public boolean mtWriteOff(String code){


        return true;
    }
    

}
