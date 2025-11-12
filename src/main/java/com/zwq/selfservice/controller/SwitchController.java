package com.zwq.selfservice.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meituan.sdk.internal.exceptions.MtSdkException;
import com.zwq.selfservice.service.impl.ApiServiceImpl2;
import com.zwq.selfservice.util.QRCodeUtil;
import com.zwq.selfservice.util.ResponseData;
import com.zwq.selfservice.vo.OpenTableRequest;
import com.zwq.selfservice.vo.SwitchResponseVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public SwitchResponseVO receiveStatus(@RequestBody String rawBody){
        log.info("开关接收body为=======================:{}",rawBody);
        JSONObject body = JSON.parseObject(rawBody);
        JSONObject data = body.getJSONObject("data");
        String serialNumber = data.get("serialNumber").toString();
        String type = data.get("type").toString();
        String event = data.get("event").toString();
        SwitchController.switchTypeMap.put(serialNumber,type);
        SwitchResponseVO switchResponseVO = new SwitchResponseVO();
        switchResponseVO.setCode(200);
        switchResponseVO.setMessage("接收请求成功");
        return switchResponseVO;
    }

    @RequestMapping(path = "/open",method = RequestMethod.POST)
    public ResponseData open(@RequestBody OpenTableRequest request, @RequestHeader(name = "token") String token) throws MtSdkException {
        request.setToken(token);
        return apiServiceImpl2.open(request);
    }

    @RequestMapping(path = "/close",method = RequestMethod.POST)
    public ResponseData close(@RequestBody OpenTableRequest request) {
        return apiServiceImpl2.close(request);
    }

    @RequestMapping(path = "/price",method = RequestMethod.GET)
    public ResponseData getPrice(@RequestParam String tableId,String type,int time) {
        log.info("获取价格请求,tableId: {},type: {},time: {}", tableId,type,time);
        BigDecimal price = apiServiceImpl2.getPrice(tableId, type, time);
        ResponseData responseData = new ResponseData();
        responseData.setData(price);
        responseData.setCode(200);
        responseData.setMessage("获取价格成功");
        return responseData;
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(path = "/tem_open",method = RequestMethod.POST)
    public boolean temOpen(@RequestBody OpenTableRequest request) {
        return apiServiceImpl2.temOpen(request);
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(path = "/tem_close",method = RequestMethod.POST)
    public boolean temClose(@RequestBody OpenTableRequest request) {
        return apiServiceImpl2.temClose(request);
    }



    @GetMapping("/qrcode")
    public void getQRCode(@RequestParam String url, HttpServletResponse response) throws Exception {
        byte[] qrImage = QRCodeUtil.generateQRCodeImage(url, 300, 300);
        response.setContentType("image/png");
        response.getOutputStream().write(qrImage);
    }
}
