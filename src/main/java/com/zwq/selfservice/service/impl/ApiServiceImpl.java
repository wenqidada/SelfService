package com.zwq.selfservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zwq.selfservice.entity.BilliardTable;
import com.zwq.selfservice.entity.DetailsTable;
import com.zwq.selfservice.service.ApiService;
import com.zwq.selfservice.service.BilliardTableService;
import com.zwq.selfservice.service.DepositTableService;
import com.zwq.selfservice.service.DetailsTableService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.zwq.selfservice.util.CommonConstant.*;


@Service
public class ApiServiceImpl implements ApiService {

    private DetailsTableService detailsTableService;

    private DepositTableService depositTableService;

    private ScheduledExecutorService scheduledExecutorService;

    private BilliardTableService billiardTableService;

    @Autowired
    private DetailsTableService setDetailsTableService(DetailsTableService detailsTableService) {
        return this.detailsTableService = detailsTableService;
    }

    @Autowired
    private DepositTableService setDepositTableService(DepositTableService depositTableService) {
        return this.depositTableService = depositTableService;
    }

    @Autowired
    private ScheduledExecutorService setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        return this.scheduledExecutorService = scheduledExecutorService;
    }

    @Autowired
    private BilliardTableService setBilliardTableService(BilliardTableService billiardTableService) {
        return this.billiardTableService = billiardTableService;
    }

    //开灯(定时关灯或直接开灯)
    public Boolean open(DetailsTable detailsTable, int h) {

        int time = 0;
        if (Objects.isNull(detailsTable)){
            return false;
        }

        if (OPEN_YIN_TYPE.equals(detailsTable.getOpenType())){
            //进行抖音核销券码,获取券码时长
            time = 1;
            detailsTable.setCouponCode("码");
            detailsTable.setMoney(new BigDecimal(100));
            timeSubmit(detailsTable,time);
        }

        if (OPEN_TUAN_TYPE.equals(detailsTable.getOpenType())){
            //美团核销券码,获取券码时长
            time = 2;
            detailsTable.setCouponCode("码");
            detailsTable.setMoney(new BigDecimal(100));
            timeSubmit(detailsTable,time);
        }

        if (OPEN_VIP_TYPE.equals(detailsTable.getOpenType()) || OPEN_DEPOSIT_TYPE.equals(detailsTable.getOpenType())){
            //
        }

        if (OPEN_TIME_TYPE.equals(detailsTable.getOpenType())){
           timeSubmit(detailsTable,h);
        }
        detailsTableService.save(detailsTable);
        return false;
    }

    //定时开台提交任务
    private void timeSubmit(DetailsTable detailsTable,int h) {
        //获取定时开台时长
        int i = h * 60 * 60;
        detailsTable.setStartTime(LocalDateTime.now());
        detailsTable.setEndTime(LocalDateTime.now().plusMinutes(i));
        //提交定时开台任务
        scheduledExecutorService.schedule(() -> close(detailsTable), i, TimeUnit.MINUTES );
    }

    //关灯
    private boolean close(DetailsTable detailsTable) {
        return false;
    }


    //计算消费金额
    private BigDecimal TotalCost(DetailsTable detailsTable, BigDecimal h) {
        QueryWrapper<BilliardTable> wrapper = new QueryWrapper<>();
        wrapper.ge("table_number",detailsTable.getTableNumber());
        BilliardTable billiardTable = billiardTableService.getOne(wrapper);
        if (Objects.isNull(h)){
            return new BigDecimal(0);
        }
        return billiardTable.getCost().multiply(h);
    }

    // 服务启动时恢复未关闭的订单任务
    @PostConstruct
    public void initPendingOrders() {

        // 获取昨天0点的时间
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();

        // 格式化时间为字符串（可选，根据数据库字段类型进行调整）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startOfYesterdayStr = startOfYesterday.format(formatter);
        String currentTimeStr = currentTime.format(formatter);

        // 创建 QueryWrapper
        QueryWrapper<DetailsTable> wrapper = new QueryWrapper<>();

        // 设置查询条件：时间在昨天0点到当前时间之间，且状态为1
        wrapper.between("start_time", startOfYesterdayStr, currentTimeStr)
                .eq("done", 0);
        List<DetailsTable> detailsTables = detailsTableService.listObjs(wrapper);

        //循环未结束订单,从新提交任务
        detailsTables.forEach(e -> {
            if (!Objects.isNull(e.getEndTime())){
                Duration duration = Duration.between(LocalDateTime.now(), e.getEndTime());
                if (duration.isNegative()){
                    //开台时间已过,直接关灯
                    close(e);
                }else {
                    //重新提交倒计时关闭任务
                    scheduledExecutorService.schedule(() -> close(e), duration.toMillis(), TimeUnit.MILLISECONDS);
                }
            }
        });

    }


    //美团核销

    //抖音核销

    //小程序微信支付


}
