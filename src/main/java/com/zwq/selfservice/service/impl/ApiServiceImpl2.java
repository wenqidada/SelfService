package com.zwq.selfservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zwq.selfservice.controller.SwitchController;
import com.zwq.selfservice.entity.BilliardTable;
import com.zwq.selfservice.entity.DetailsTable;
import com.zwq.selfservice.service.ApiService;
import com.zwq.selfservice.service.BilliardTableService;
import com.zwq.selfservice.service.DepositTableService;
import com.zwq.selfservice.service.DetailsTableService;
import com.zwq.selfservice.util.SendHttpRequestUtil;
import com.zwq.selfservice.vo.SwitchRequestVO;
import com.zwq.selfservice.vo.SwitchResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import static com.zwq.selfservice.util.CommonConstant.*;


@Service
@Slf4j
public class ApiServiceImpl2 implements ApiService {

    private DetailsTableService detailsTableService;

    private DepositTableService depositTableService;

    private ScheduledExecutorService scheduledExecutorService;

    private BilliardTableService billiardTableService;

    private SendHttpRequestUtil sendHttpRequestUtil;

    @Value("${api.time_url}")
    String timeUrl;

    @Value("${api.open_url}")
    String openUrl;

    @Autowired
    private void setDetailsTableService(DetailsTableService detailsTableService) {
        this.detailsTableService = detailsTableService;
    }

    @Autowired
    private void setDepositTableService(DepositTableService depositTableService) {
        this.depositTableService = depositTableService;
    }

    @Autowired
    private void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Autowired
    private void setBilliardTableService(BilliardTableService billiardTableService) {
        this.billiardTableService = billiardTableService;
    }

    @Autowired
    private void setSendHttpRequestUtil(SendHttpRequestUtil sendHttpRequestUtil) {
        this.sendHttpRequestUtil = sendHttpRequestUtil;
    }



    //开灯(定时关灯或直接开灯)
    public Boolean open(DetailsTable detailsTable,int h) {

        int time = 0;
        if (Objects.isNull(detailsTable)){
            return false;
        }

        if (OPEN_YIN_TYPE.equals(detailsTable.getOpenType())){
            //进行抖音核销券码,获取券码时长
            time = 1;
            detailsTable.setCouponCode("码");
            detailsTable.setMoney(new BigDecimal(100));
            return startPlay(detailsTable, time);
        }

        if (OPEN_TUAN_TYPE.equals(detailsTable.getOpenType())){
            //美团核销券码,获取券码时长
            time = 2;
            detailsTable.setCouponCode("码");
            detailsTable.setMoney(new BigDecimal(100));
            return startPlay(detailsTable, time);
        }

        if (OPEN_VIP_TYPE.equals(detailsTable.getOpenType()) || OPEN_DEPOSIT_TYPE.equals(detailsTable.getOpenType())){
            return openAndClose(detailsTable.getCouponCode(),"1_1",false,0);
        }

        if (OPEN_TIME_TYPE.equals(detailsTable.getOpenType())){
            totalCost(detailsTable,h);
           return startPlay(detailsTable,h);
        }
        detailsTable.setStartTime(LocalDateTime.now());
        detailsTable.setTableNumber(detailsTable.getTableNumber());
        detailsTable.setDone((byte)0);
        detailsTableService.save(detailsTable);
        return false;
    }


    public boolean close(DetailsTable detailsTable,String command) {
        //计算关灯时间
        if (detailsTable.getOpenType() == null){
            return false;
        }
        DetailsTable one = detailsTableService.getOne(new QueryWrapper<DetailsTable>()
                .eq("table_number", detailsTable.getTableNumber())
                .eq("done", 0));
        if( one == null){
            return false;
        }

        LocalDateTime startTime = one.getStartTime();
        LocalDateTime endTime = LocalDateTime.now();
        long minutes = Duration.between(startTime, endTime).toMillis();
        double hours = minutes / 60.0;
        BigDecimal bigDecimal = totalCost(detailsTable, hours);
        one.setEndTime(endTime);
        one.setDone((byte)1);
        one.setMoney(bigDecimal);
        detailsTableService.updateById(one);
        return openAndClose(detailsTable.getCouponCode(),command,false,0);
    }



    private Boolean startPlay(DetailsTable detailsTable, int h) {
        long startTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        long endTime = LocalDateTime.now().plusHours(h).atZone(ZoneId.systemDefault()).toEpochSecond();
        String command = "1_1_"+startTime+"_"+endTime;
        return openAndClose(billiardTableService
                        .getById(detailsTable.getTableNumber()).getLockNo(), command, true,0);
    }


    //关灯
    public boolean openAndClose(String number,String command,boolean scheduled,int count){
        SwitchRequestVO switchRequestVO = new SwitchRequestVO();
        ResponseEntity<SwitchResponseVO> post;
        switchRequestVO.setSerialNumber(number);
        if (SwitchController.switchTypeMap.get(number) == null){
            switchRequestVO.setType("YD34");
        }else {
            switchRequestVO.setType(SwitchController.switchTypeMap.get(number));
        }
        switchRequestVO.setCommand(command);
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("token","3EFAD0DAAAE6856819E3C4B24E544408");
        headerMap.put("Content-Type", "application/json; charset=UTF-8");
        if (scheduled){
            post = sendHttpRequestUtil.post(timeUrl, switchRequestVO, SwitchResponseVO.class, headerMap);
        }else {
            post = sendHttpRequestUtil.post(openUrl, switchRequestVO, SwitchResponseVO.class, headerMap);
        }
        SwitchResponseVO body = post.getBody();
        //请求失败，进行重试
        if (body != null && body.getCode() != 200) {
            if (count <= 3){
                log.info("开台失败,进行重试,重试次数:{},异常调用body为==============={}",count,body);
                this.openAndClose(number, command, scheduled,count+1);
            }
            return false;
        }
        return true;
    }


    //计算消费金额
    private BigDecimal totalCost(DetailsTable detailsTable, double h) {
        QueryWrapper<BilliardTable> wrapper = new QueryWrapper<>();
        wrapper.ge("table_number",detailsTable.getTableNumber());
        BilliardTable billiardTable = billiardTableService.getOne(wrapper);
        if (h == 0 ){
            return new BigDecimal(0);
        }
        return billiardTable.getCost().multiply(BigDecimal.valueOf(h));
    }

    
    //小程序微信支付


}
