package com.zwq.selfservice.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DYImpl {

    @Value("dy.url.validation")
    String validationUrl;

    @Value("dy.url.verify")
    String verifyUrl;

    @Value("dy.url.token")
    String tokenUrl;


    /**
     * 获取client token
     */
    public void getToken(){

    }




    /**
     * 验券准备
     */
    public void validation(){



    }

    /**
     * 调用抖音验证券码
     */
    public boolean verify(){

        return true;

    }

    /**
     * 撤销券码
     */
    public void cancel(){

    }

    public boolean dYWriteOff(String code) {
        return true;
    }
}
