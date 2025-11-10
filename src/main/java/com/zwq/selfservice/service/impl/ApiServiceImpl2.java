package com.zwq.selfservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zwq.selfservice.controller.SwitchController;
import com.zwq.selfservice.entity.*;
import com.zwq.selfservice.service.*;
import com.zwq.selfservice.util.ResponseData;
import com.zwq.selfservice.util.SendHttpRequestUtil;
import com.zwq.selfservice.util.TimeUtil;
import com.zwq.selfservice.vo.OpenTableRequest;
import com.zwq.selfservice.vo.SwitchRequestVO;
import com.zwq.selfservice.vo.SwitchResponseVO;
import com.zwq.selfservice.vo.yunfei.ConsumeRequest;
import com.zwq.selfservice.vo.yunfei.ConsumeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.zwq.selfservice.util.CommonConstant.*;


@Service
@Slf4j
public class ApiServiceImpl2 implements ApiService {

    private DetailsTableService detailsTableService;

    private DepositTableService depositTableService;

    private ScheduledExecutorService scheduledExecutorService;

    private VipInfoTableService vipInfoTableService;

    private BilliardTableService billiardTableService;

    private SendHttpRequestUtil sendHttpRequestUtil;

    private WechatTableService wechatTableService;

    private TripartiteTableService tripartiteTableService;

    private WechatService wechatService;

    private YunFeiService yunFeiService;

    @Value("${info.deposit}")
    int deposit;

    @Value("${api.time_url}")
    String timeUrl;

    @Value("${api.open_url}")
    String openUrl;

    @Value("${api.token}")
    String token;

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

    @Autowired
    private void setVipInfoTableService(VipInfoTableService vipInfoTableService) {
        this.vipInfoTableService = vipInfoTableService;
    }

    @Autowired
    private void setWechatTableService(WechatTableService wechatTableService) {
        this.wechatTableService = wechatTableService;
    }

    @Autowired
    private void setTripartiteTableService(TripartiteTableService tripartiteTableService) {
        this.tripartiteTableService = tripartiteTableService;
    }

    @Autowired
    private void setWechatService(WechatService wechatService) {
        this.wechatService = wechatService;
    }

    @Autowired
    private void setYunFeiService(YunFeiService yunFeiService) {
        this.yunFeiService = yunFeiService;
    }

    private final Map<Integer,String> openTime = new HashMap<>();



    //开灯(定时关灯或直接开灯)
    public ResponseData open(OpenTableRequest openTableRequest){
        double amont = 0L;
        boolean result = false;
        ResponseData responseData = new ResponseData();
        DetailsTable detailsTable = new DetailsTable();
        TripartiteTable tripartiteTable = new TripartiteTable();
        BilliardTable billiardTable = billiardTableService.getById(openTableRequest.getTableId());
        String tableInfo = billiardTable.getTableInfo();
        String lockNo = billiardTable.getLockNo();
        WechatTable wechatInfo = wechatTableService.getOne(
                new QueryWrapper<WechatTable>().eq("token", openTableRequest.getToken()));
        String code = StringUtils.deleteWhitespace(openTableRequest.getCode());
        if (OPEN_YIN_TYPE == openTableRequest.getType() || OPEN_TUAN_TYPE == openTableRequest.getType()){
            int time = 0;
            ConsumeRequest consumeRequest = new ConsumeRequest();
            consumeRequest.setType("1");
            consumeRequest.setReceiptCode(code);
            consumeRequest.setVerifyCount(1);
            if (OPEN_YIN_TYPE == openTableRequest.getType()){
                log.info("抖音开台==============={}", billiardTable.getTableNumber());
                //进行抖音核销券码,获取券码时长
                consumeRequest.setPlatform("douyin");
                tripartiteTable.setPlatformType((byte)1);
                detailsTable.setOpenType((byte)4);
            }else {
                log.info("美团开台==============={}", billiardTable.getTableNumber());
                //美团核销券码,获取券码时长
                consumeRequest.setPlatform("meituan");
                tripartiteTable.setPlatformType((byte)2);
                detailsTable.setOpenType((byte)5);
            }
            ConsumeResponse consume = yunFeiService.consume(consumeRequest);
            if (consume.getCode() != 200){
                responseData.setCode(400);
                responseData.setMessage("验券失败,请核对券码信息");
                return responseData;
            }
            String dealTitle = consume.getData().getDealTitle();
            ConsumeResponse.DataBean.ResultBean resultBean = consume.getData().getResult().get(0);
            if (!dealTitle.contains("小时")){
                Optional<ConsumeResponse.DataBean.ResultBean> consumeResult = consume.getData().getResult().stream()
                        .filter(s -> s.getDealTitle().contains("小时")).findFirst();
                dealTitle = consumeResult.map(ConsumeResponse.DataBean.ResultBean::getDealTitle).orElse("");
            }
            int index = dealTitle.indexOf("小时");
            if (index != -1) {
                int numStart = index - 1;
                while (numStart >= 0 && Character.isDigit(dealTitle.charAt(numStart))) {
                    numStart--;
                }
                String num = dealTitle.substring(numStart + 1, index);
                time = Integer.parseInt(num);
            }
            amont = consume.getData().getPayAmount() / 100.0;
            LocalDateTime endTime = LocalDateTime.now().plusHours(time);
            detailsTable.setEndTime(endTime);
            if (amont > 0.0) {
                result =  scheduledPlay(billiardTable, time);
                if (result){
                    tripartiteTable.setAmount(BigDecimal.valueOf(amont));
                    tripartiteTable.setCodes(code);
                    tripartiteTable.setActualPayment(BigDecimal.valueOf(resultBean.getDealMarketPrice()));
                    tripartiteTable.setCreateTime(LocalDateTime.now());
                    tripartiteTable.setRequestId(resultBean.getOrderId());
                }
            }
        }
        if (OPEN_VIP_TYPE == openTableRequest.getType() || OPEN_DEPOSIT_TYPE == openTableRequest.getType()) {
            if (OPEN_VIP_TYPE == openTableRequest.getType()){
                log.info("会员开台==============={}", billiardTable.getTableNumber());
                VipInfoTable vip = vipInfoTableService.getOne(new QueryWrapper<VipInfoTable>()
                        .eq("vip_user", wechatInfo.getOpenId()));
                if (vip == null){
                    responseData.setCode(200);
                    responseData.setMessage("非会员用户,请充值或购买会员");
                    return responseData;
                }
                if (vip.getBalance().compareTo(BigDecimal.valueOf(10)) > 0){
                    long time = vip.getBalance().divide(billiardTable.getCost(),1,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(60 * 60)).longValue();
                    long endTime = LocalDateTime.now().plusSeconds(time).atZone(ZoneId.systemDefault()).toEpochSecond();
                    openTime.put(110 + billiardTable.getTableNumber(),"vip_" + endTime);
                    result = openAndClose(tableInfo,lockNo + "_1",false,0);
                    if (result){
                        detailsTable.setOpenType((byte)1);
                    }
                }else {
                    responseData.setCode(400);
                    responseData.setMessage("会员余额不足,请充值");
                    return responseData;
                }
            }else {
                log.info("押金开台==============={}", billiardTable.getTableNumber());
                result = openAndClose(tableInfo,lockNo + "_1",false,0);
                detailsTable.setOpenType((byte)2);
                long epochSecond = LocalDateTime.now().plusHours(6).atZone(ZoneId.systemDefault()).toEpochSecond();
                openTime.put(110 + billiardTable.getTableNumber(),"deposit_" + epochSecond);
            }
            tripartiteTable = null;
        }

        if (OPEN_TIME_TYPE == openTableRequest.getType()) {
            log.info("定时开台==============={}", billiardTable.getTableNumber());
            amont = getPrice(String.valueOf(billiardTable.getTableNumber()),"3",
                    openTableRequest.getTime()).doubleValue();
            result = scheduledPlay(billiardTable,openTableRequest.getTime());
            LocalDateTime endTime = LocalDateTime.now().plusHours(openTableRequest.getTime());
            detailsTable.setEndTime(endTime);
            detailsTable.setOpenType((byte)3);
            if (tripartiteTable != null) {
                tripartiteTable.setAmount(BigDecimal.valueOf(amont));tripartiteTable.setActualPayment(BigDecimal.valueOf(amont));
                tripartiteTable.setCreateTime(LocalDateTime.now());
                tripartiteTable.setRequestId(openTableRequest.getToken()+LocalDateTime.now());
                tripartiteTable.setPlatformType((byte)3);
            }
        }
        if (result){
            detailsTable.setTableNumber((byte)openTableRequest.getTableId());
            detailsTable.setCouponCode(code);
            detailsTable.setStartTime(LocalDateTime.now());
            detailsTable.setMoney(BigDecimal.valueOf(amont));
            detailsTable.setStartTime(LocalDateTime.now());
            detailsTable.setUserInfo(wechatInfo.getOpenId());
            detailsTableService.save(detailsTable);
            BilliardTable table = billiardTableService.getById(openTableRequest.getTableId());
            table.setUseType((byte)1);
            billiardTableService.updateById(table);
            if (tripartiteTable != null){
                tripartiteTableService.save(tripartiteTable);
            }
            responseData.setCode(200);
            responseData.setMessage("开台成功,请尽情享受台球时光");
            return responseData;
        }
        responseData.setCode(400);
        responseData.setMessage("开台失败,请稍后再试");
        return responseData;
    }


    public ResponseData close(OpenTableRequest request) {
        ResponseData responseData = new ResponseData();
        if (openTime.get(request.getTableId()) != null) {
            responseData.setCode(400);
            responseData.setMessage("球桌定时开台中,不能关闭台球桌");
            return responseData;
        }
        boolean flag;
        //计算关灯时间
        if (request.getType() != OPEN_VIP_TYPE && request.getType() != OPEN_DEPOSIT_TYPE) {
            responseData.setMessage("不支持的请求类型");
            responseData.setCode(400);
            return responseData;
        }
        DetailsTable one = detailsTableService.getOne(new QueryWrapper<DetailsTable>()
                .eq("table_number", request.getTableId())
                .isNull("end_time")
        );
        if (one == null) {
            responseData.setMessage("无开台记录");
            responseData.setCode(400);
            return responseData;
        }
        BilliardTable table = billiardTableService.getById(one.getTableNumber());
        String lockNo = table.getLockNo();
        String tableInfo = table.getTableInfo();
        //押金开台需返还押金
        if (request.getType() == OPEN_DEPOSIT_TYPE) {
            int retry = 0;
            String outTradeNo = wechatService.getOrderCache(request.getToken() + "deposit");
            if (outTradeNo == null || outTradeNo.isEmpty()) {
                List<TripartiteTable> tripartiteTables = tripartiteTableService.list(
                        new QueryWrapper<TripartiteTable>()
                                .eq("request_id", request.getToken())
                                .eq("encrypted_codes","deposit")
                                .orderByDesc("create_time"));
                TripartiteTable tripartiteTable = tripartiteTables.get(0);
                outTradeNo = tripartiteTable.getCodes();
            }
            while (retry < 3) {
                boolean b = wechatService.wxRefund(null, outTradeNo, deposit);
                if (!b){
                    retry++;
                    log.error("押金退款失败,重试次数: {}", retry);
                } else {
                    log.info("押金退款成功");
                    break;
                }
            }
            LocalDateTime endTime = LocalDateTime.now();
            one.setEndTime(endTime);
            one.setMoney(request.getPrice());
            detailsTableService.updateById(one);
            String command = lockNo + "_0";
            flag = openAndClose(tableInfo, command, false, 0);
            if (flag){
                table.setUseType((byte)0);
                billiardTableService.updateById(table);
                responseData.setCode(200);
                responseData.setMessage("押金开台关台成功");
                return responseData;
            }
        }else {
            BigDecimal price = getPrice(String.valueOf(request.getTableId()), String.valueOf(request.getType()), 0);
            WechatTable wechatInfo = wechatTableService.getOne(new QueryWrapper<WechatTable>().eq("token",request.getToken()));
            VipInfoTable vipInfo = vipInfoTableService.getOne(new QueryWrapper<VipInfoTable>().eq("vip_user",wechatInfo.getOpenId()));
            if (vipInfo.getBalance().compareTo(price) > 0){
                BigDecimal usePrice = vipInfo.getBalance().subtract(price);
                vipInfo.setBalance(usePrice);
            }else {
                vipInfo.setBalance(new BigDecimal(0));
            }
            vipInfoTableService.updateById(vipInfo);
            LocalDateTime endTime = LocalDateTime.now();
            one.setEndTime(endTime);
            one.setMoney(request.getPrice());
            detailsTableService.updateById(one);
            flag = openAndClose(tableInfo, lockNo + "_0", false, 0);
            if (flag){
                TripartiteTable tripartiteTable = new TripartiteTable();
                tripartiteTable.setAmount(request.getPrice());
                tripartiteTable.setRequestId(request.getToken()+LocalDateTime.now());
                tripartiteTable.setCreateTime(LocalDateTime.now());
                tripartiteTable.setPlatformType((byte)3);
                tripartiteTableService.save(tripartiteTable);
                table.setUseType((byte)0);
                billiardTableService.updateById(table);
                responseData.setCode(200);
                responseData.setMessage("会员关台成功");
                return responseData;
            }
        }
        responseData.setCode(400);
        responseData.setMessage("关台失败,请稍后再试");
        return responseData;
    }

    private Boolean scheduledPlay(BilliardTable billiardTable, int h) {
        String lockNo = billiardTable.getLockNo();
        String command;
        //不支持非定时开台
        if (h <= 0 ){
            //只进行开灯
            return false;
        }else {
            long startTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
            long endTime = LocalDateTime.now().plusHours(h).atZone(ZoneId.systemDefault()).toEpochSecond();
            command = lockNo + "_8_" + startTime + "_" + endTime;
            StringBuilder scheduledCommand = new StringBuilder();
            if (!openTime.isEmpty()) {
                for (Map.Entry<Integer, String> entry : openTime.entrySet()) {
                    String[] s = entry.getValue().split("_");
                    long openEndTime = Long.parseLong(s[s.length - 1]);
                    if (startTime < openEndTime){
                        scheduledCommand.append(entry.getValue()).append(";");
                    }
                }
            }
            openTime.put(billiardTable.getTableNumber(),command);
            command = scheduledCommand + command;
        }
        return openAndClose(billiardTable.getTableInfo(), command, true,0);
    }


    public boolean temOpen(OpenTableRequest request){
        BilliardTable table = billiardTableService.getById(request.getTableId());
        String lockNo = table.getLockNo();
        String command;
        boolean scheduled = false;
        StringBuilder scheduledCommand = new StringBuilder();
        if (request.getTime() != 0){
            long startTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
            if (!openTime.isEmpty()) {
                for (Map.Entry<Integer, String> entry : openTime.entrySet()) {
                    String[] s = entry.getValue().split("_");
                    long endTime = Long.parseLong(s[s.length - 1]);
                    if (startTime < endTime){
                        scheduledCommand.append(entry.getValue()).append(";");
                    }
                }
            }
            long endTime = LocalDateTime.now().plusHours(request.getTime()).atZone(ZoneId.systemDefault()).toEpochSecond();
            command = lockNo + "_8_"+startTime+"_"+endTime;
            scheduled = true;
            openTime.put(table.getTableNumber(),command);
            command = scheduledCommand + command;
        }else {
            command = lockNo + "_1";
        }
        boolean b = openAndClose(table.getTableInfo(), command, scheduled, 0);
        if (b){
            table.setUseType((byte)1);
            billiardTableService.updateById(table);
        }
        return b;
    }

    public boolean temClose(OpenTableRequest request){
        if (openTime.get(request.getTableId()) != null) {
            log.info("球桌定时开台中,不能关闭台球桌");
            return false;
        }
        BilliardTable table = billiardTableService.getById(request.getTableId());
        String lockNo = table.getLockNo();
        String command = lockNo + "_0";
        boolean b = openAndClose(table.getTableInfo(), command, false, 0);
        if (b){
            table.setUseType((byte)0);
            billiardTableService.updateById(table);
        }
        return b;
    }



    //调用开关发送指令
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
        headerMap.put("token",token);
        headerMap.put("Content-Type", "application/json; charset=UTF-8");
        if (scheduled){
            post = sendHttpRequestUtil.post(timeUrl, switchRequestVO, SwitchResponseVO.class, headerMap);
        }else {
            Map<String, String> params = new HashMap<>();
            params.put("save","1");
            post = sendHttpRequestUtil.post(openUrl, switchRequestVO, SwitchResponseVO.class, headerMap,params);
        }
        SwitchResponseVO body = post.getBody();
        //请求失败，进行重试
        if (body != null && body.getCode() != 200) {
            if (count < 3){
                log.info("开台失败,进行重试,重试次数:{},异常调用body为==============={}",count,body);
                this.openAndClose(number, command, scheduled,count+1);
            }
            return false;
        }
        //TODO 增加查询开关执行命令方法
        log.info("开关调用成功,返回body为==============={}",body);
        return true;
    }


    //计算消费金额
    private BigDecimal totalCost(BilliardTable billiardTable, double h) {
        if (h == 0 ){
            return new BigDecimal(0);
        }
        return billiardTable.getCost().multiply(BigDecimal.valueOf(h));
    }


    public BigDecimal getPrice(String tableId,String type,int time) {
        log.info("获取价格请求,tableId: {}", tableId);
        DetailsTable detailsTable = null;
        BigDecimal price = new BigDecimal(0);
        QueryWrapper<BilliardTable> wrapper = new QueryWrapper<>();
        wrapper.eq("table_number", tableId);
        BilliardTable billiardTable = billiardTableService.getOne(wrapper);
        List<DetailsTable> detailsTables = detailsTableService.list(new QueryWrapper<DetailsTable>()
                .eq("table_number", tableId)
                .isNull("end_time")
                .orderBy(true,false,"start_time")
        );
        if (!detailsTables.isEmpty()) {
            detailsTable = detailsTables.get(0);
        }
        if (OPEN_DEPOSIT_TYPE == Integer.parseInt(type) && billiardTable.getUseType() == 0) {
            //如果是押金开台,并且无记录情况,为第一次开台,支付押金100元
            price = new BigDecimal(deposit);
        }
        if (OPEN_TIME_TYPE == Integer.parseInt(type) && billiardTable.getUseType() == 0) {
            if (TimeUtil.isInNightTimeRangeStrict()){
                price = billiardTable.getIdleCost().multiply(BigDecimal.valueOf(time));
            }else {
                price = billiardTable.getCost().multiply(BigDecimal.valueOf(time));
            }
        }
        if (billiardTable.getUseType() == 1 && detailsTable != null) {
            if (OPEN_DEPOSIT_TYPE == Integer.parseInt(type) && TimeUtil.isInNightTimeRangeStrict()){
                LocalDateTime startTime = detailsTable.getStartTime();
                LocalDateTime now = LocalDateTime.now();
                long minutes = Duration.between(startTime, now).toMinutes();
                price = billiardTable.getIdleCost()
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(minutes));
            }else {
                LocalDateTime startTime = detailsTable.getStartTime();
                LocalDateTime now = LocalDateTime.now();
                long minutes = Duration.between(startTime, now).toMinutes();
                price = billiardTable.getCost()
                        .divide(BigDecimal.valueOf(60), 3, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(minutes)).setScale(2,RoundingMode.HALF_UP);
            }
        }
        return price;
    }


    @Override
    public String createQR(String command) {
        return "";
    }


//    @Scheduled(fixedRate = 5000)
//    @Transactional
    public void checkAndUpdateOpenType(){
        StringBuilder scheduledCommand = new StringBuilder();
        AtomicBoolean flag = new AtomicBoolean(false);
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        List<BilliardTable> tableList = billiardTableService.list(new QueryWrapper<BilliardTable>().eq("use_type", 1));
        if (tableList == null || tableList.isEmpty()){
            openTime.clear();
            return;
        }
        tableList.forEach(table -> {
            if (openTime.get(table.getTableNumber()) != null) {
                String[] s = openTime.get(table.getTableNumber()).split("_");
                long endTime = Long.parseLong(s[s.length - 1]);
                if (now > endTime){
                    table.setUseType((byte)0);
                    billiardTableService.updateById(table);
                    //求桌更新后,删除定时球桌信息
                    openTime.remove(table.getTableNumber());
                    flag.set(true);
                }
            }
        });
        if (flag.get()) {
            for (Map.Entry<Integer, String> entry : openTime.entrySet()) {
                String[] s = entry.getValue().split("_");
                long endTime = Long.parseLong(s[s.length - 1]);
                if (now < endTime){
                    scheduledCommand.append(entry.getValue()).append(";");
                }
                // 会员开台,余额不足时关闭台球桌
                if (entry.getKey() > 110) {
                    BilliardTable one = billiardTableService.getOne(new QueryWrapper<BilliardTable>().eq("table_number",entry.getKey()-110));
                    boolean result = openAndClose(one.getTableInfo(),one.getLockNo()+"_0",false,0);
                    if (!result){
                        log.info("会员费用不足,关台失败,球桌信息为===={}",one);
                    }
                }
            }
            if (!scheduledCommand.isEmpty()) {
                scheduledCommand.setLength(scheduledCommand.length() - 1);
                log.info("调用定时开关指令为====={}", scheduledCommand);
                boolean b = openAndClose("863505072020799", scheduledCommand.toString(), true, 0);
            }
        }
    }


    @Scheduled(fixedRate = 5000)
    @Transactional
    public void checkAndUpdateUseType(){
        //1.查询正在使用台球桌
        StringBuilder scheduledCommand = new StringBuilder();
        AtomicBoolean flag = new AtomicBoolean(false);
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        List<BilliardTable> tableList = billiardTableService.list(new QueryWrapper<BilliardTable>().eq("use_type", 1));
        if (tableList == null || tableList.isEmpty()){
            openTime.clear();
            return;
        }
        tableList.forEach(table -> {
            if (openTime.get(table.getTableNumber()) != null) {
                String[] s = openTime.get(table.getTableNumber()).split("_");
                long endTime = Long.parseLong(s[s.length - 1]);
                if (now > endTime){
                    table.setUseType((byte)0);
                    billiardTableService.updateById(table);
                    //求桌更新后,删除定时球桌信息
                    openTime.remove(table.getTableNumber());
                    flag.set(true);
                }
            }
        });
        if (flag.get()) {
            for (Map.Entry<Integer, String> entry : openTime.entrySet()) {
                String[] s = entry.getValue().split("_");
                long endTime = Long.parseLong(s[s.length - 1]);
                if (now < endTime){
                    // 会员开台,余额不足时关闭台球桌
                    if (entry.getKey() > 110) {
                        BilliardTable one = billiardTableService.getOne(new QueryWrapper<BilliardTable>().eq("table_number",entry.getKey()-110));
                        boolean result = openAndClose(one.getTableInfo(),one.getLockNo()+"_0",false,0);
                        if (!result){
                            log.info("会员费用不足,自动关台失败,球桌信息为===={}",one);
                        }else {
                            DetailsTable detailsTable = detailsTableService.getOne(new QueryWrapper<DetailsTable>()
                                    .eq("table_number",one.getTableNumber())
                                    .isNull("end_time"));
                            LocalDateTime time = LocalDateTime.now();
                            BigDecimal balance;
                            if (entry.getValue().startsWith("vip")){
                                //更新vip余额
                                VipInfoTable vipInfoTable = vipInfoTableService.getOne(new QueryWrapper<VipInfoTable>()
                                        .eq("vip_user",detailsTable.getUserInfo()));
                                balance = vipInfoTable.getBalance();
                                vipInfoTable.setUpdateTime(time);
                                vipInfoTable.setBalance(BigDecimal.valueOf(0));
                                vipInfoTableService.updateById(vipInfoTable);
                            }else {
                                balance = BigDecimal.valueOf(deposit);
                            }
                            //更新明细表结束时间
                            detailsTable.setEndTime(time);
                            detailsTable.setMoney(balance);
                            detailsTableService.updateById(detailsTable);
                        }
                    }else {
                        scheduledCommand.append(entry.getValue()).append(";");
                    }
                }
            }
            if (!scheduledCommand.isEmpty()) {
                scheduledCommand.setLength(scheduledCommand.length() - 1);
                log.info("调用定时开关指令为====={}", scheduledCommand);
                boolean b = openAndClose(tableList.get(0).getTableInfo(), scheduledCommand.toString(), true, 0);
            }
        }
    }

    public long getOpenTime(Integer tableId) {
        log.info("获取时长++++++++++++{}",tableId);
        String time = openTime.get(tableId);
        if (time != null){
            String[] s = time.split("_");
            return Long.parseLong(s[s.length - 1]);
        }else {
            for (Map.Entry<Integer, String> entry : openTime.entrySet()) {
                log.warn("获取时长失败,当前缓存信息key ======={} , value ======={}",entry.getKey(),entry.getValue());
            }
            return 6 * 60 * 60;
        }
    }

    private String modifyCommand(String command) {
        if (command == null || command.isEmpty()) {
            return command;
        }

        // 分割字符串获取各个命令
        String[] commands = command.split(";");

        // 处理最后一个命令
        String lastCommand = commands[commands.length - 1];
        String[] parts = lastCommand.split("_");

        // 检查命令格式：必须有至少4个部分（如：1_2_开始时间戳_结束时间戳）
        if (parts.length < 4) {
            // 如果命令格式不完整（只有一个下划线），直接返回原命令
            return command;
        }

        // 计算5分钟后的时间戳
        long fiveMinutesLater = LocalDateTime.now()
                .plusMinutes(5)
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();

        // 替换最后一个部分（结束时间戳）为新的时间戳
        parts[parts.length - 1] = String.valueOf(fiveMinutesLater);

        // 重新构建最后一个命令
        commands[commands.length - 1] = String.join("_", parts);

        // 重新组合所有命令
        return String.join(";", commands);
    }


}
