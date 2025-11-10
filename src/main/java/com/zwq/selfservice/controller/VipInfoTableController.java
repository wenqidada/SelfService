package com.zwq.selfservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zwq.selfservice.config.DepositConfig;
import com.zwq.selfservice.entity.VipInfoTable;
import com.zwq.selfservice.entity.WechatTable;
import com.zwq.selfservice.service.VipInfoTableService;
import com.zwq.selfservice.service.WechatTableService;
import com.zwq.selfservice.util.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/vipInfoTable")
@Slf4j
public class VipInfoTableController {

    private final VipInfoTableService vipInfoTableService;
    private final WechatTableService wechatTableService;
    private final DepositConfig depositConfig;

    @Value("${info.desc}")
    public String vipDesc;

    public VipInfoTableController(VipInfoTableService vipInfoTableService, WechatTableService wechatTableService, DepositConfig depositConfig) {
        this.vipInfoTableService = vipInfoTableService;
        this.wechatTableService = wechatTableService;
        this.depositConfig = depositConfig;
    }

    // 查询全部
    @GetMapping("/getVipInfos")
    public List<VipInfoTable> getVipInfos() {
        List<VipInfoTable> list = vipInfoTableService.list();
        log.info("获取会员信息列表: {}", list);
        return list;
    }

    // 查询单个
    @GetMapping("/getVipInfo")
    public VipInfoTable getVipInfo(@RequestParam Long id) {
        VipInfoTable info = vipInfoTableService.getById(id);
        log.info("获取会员信息: {}", info);
        return info;
    }

    // 查询单个
    @GetMapping("/getVip")
    public ResponseData getVip(@RequestParam String token) {
        WechatTable wechatInfo = wechatTableService.getOne(new QueryWrapper<WechatTable>().eq("token",token));
        VipInfoTable info = vipInfoTableService.getOne(new QueryWrapper<VipInfoTable>().eq("vip_user",wechatInfo.getOpenId()));
        HashMap<String, Object> result = new HashMap<>();
        Set<String> configPrice = depositConfig.getPrice().keySet();
        List<Integer> price = configPrice.stream().map(Integer::parseInt).sorted().toList();
        if (info == null || info.getBalance() == null) {
            result.put("balance", BigDecimal.valueOf(0.00));
        }else {
            result.put("balance",info.getBalance());
        }
        result.put("vipDesc",vipDesc);
        result.put("price",price);
        log.info("微信获取会员信息: {}", info);
        return new ResponseData(200, "获取vip信息成功", result);
    }


    // 新增
    @PostMapping("/updateVipInfo")
    public ResponseData addVipInfo(@RequestParam String token,@RequestParam Integer total) {
        WechatTable wechatInfo = wechatTableService.getOne(new QueryWrapper<WechatTable>().eq("token",token));
        VipInfoTable info = vipInfoTableService.getOne(new QueryWrapper<VipInfoTable>().eq("vip_user",wechatInfo.getOpenId()));
        boolean result;
        if (total != null){
            for (Map.Entry<String, Integer> entry : depositConfig.getPrice().entrySet()) {
                Integer i = Integer.parseInt(entry.getKey());
                if (total.equals(i)) {
                    total = i + entry.getValue();
                }
            }
        }else {
            return new ResponseData(400,"会员充值失败,请联系店长", false);
        }
        if (info == null) {
            VipInfoTable vipInfoTable = new VipInfoTable();
            vipInfoTable.setBalance(BigDecimal.valueOf(total));
            vipInfoTable.setVipUser(wechatInfo.getOpenId());
            vipInfoTable.setCreateTime(LocalDateTime.now());
            vipInfoTable.setUpdateTime(LocalDateTime.now());
            result = vipInfoTableService.save(vipInfoTable);
        }else {
            info.setBalance(info.getBalance().add(BigDecimal.valueOf(total)));
            info.setUpdateTime(LocalDateTime.now());
            result = vipInfoTableService.updateById(info);
        }
        if (result){
            return new ResponseData(200,"会员充值成功", true);
        }else {
            return new ResponseData(400,"会员充值失败,请联系店长", false);
        }
    }

    // 新增
    @PostMapping("/addVipInfo")
    public boolean addVipInfo(@RequestBody VipInfoTable vipInfoTable) {
        boolean result = vipInfoTableService.save(vipInfoTable);
        log.info("新增会员信息: {}, 结果: {}", vipInfoTable, result);
        return result;
    }

    // 修改
    @PutMapping("/updateVipInfo")
    public boolean updateVipInfo(@RequestBody VipInfoTable vipInfoTable) {
        boolean result = vipInfoTableService.updateById(vipInfoTable);
        log.info("修改会员信息: {}, 结果: {}", vipInfoTable, result);
        return result;
    }

    // 删除
    @DeleteMapping("/deleteVipInfo")
    public boolean deleteVipInfo(@RequestParam Long id) {
        boolean result = vipInfoTableService.removeById(id);
        log.info("删除会员信息: {}, 结果: {}", id, result);
        return result;
    }
}