package com.zwq.selfservice.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zwq.selfservice.service.impl.ApiServiceImpl2;
import com.zwq.selfservice.vo.SwitchResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/qi")
@Slf4j
public class SwitchController {
    @Autowired
    ApiServiceImpl2  apiServiceImpl2;


    public static HashMap<String,String> switchTypeMap = new HashMap<>();


    @RequestMapping(path = "/postdata",method = RequestMethod.POST)
    public SwitchResponseVO receiveStatus(Map<String,String> body){
        log.info("开关接收body为=======================:{}",body);
        String data = body.get("data");
        JSONObject jsonObject = JSON.parseObject(data);
        String serialNumber = jsonObject.get("serialNumber").toString();
        String type = jsonObject.get("type").toString();
        String event = jsonObject.get("event").toString();
        SwitchController.switchTypeMap.put(serialNumber,type);
        SwitchResponseVO switchResponseVO = new SwitchResponseVO();
        switchResponseVO.setCode(200);
        switchResponseVO.setMsg("接收请求成功");
        return switchResponseVO;
    }

    @RequestMapping(path = "/testOpen",method = RequestMethod.POST)
    public Boolean testOpen(@RequestBody String command){
        return apiServiceImpl2.openAndClose("861346077619133",command,false,1);
    }

}
