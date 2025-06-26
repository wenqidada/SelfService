package com.zwq.selfservice.controller;

import com.zwq.selfservice.entity.VipInfoTable;
import com.zwq.selfservice.service.VipInfoTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vipInfoTable")
@Slf4j
public class VipInfoTableController {

    private final VipInfoTableService vipInfoTableService;

    public VipInfoTableController(VipInfoTableService vipInfoTableService) {
        this.vipInfoTableService = vipInfoTableService;
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