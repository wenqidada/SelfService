package com.zwq.selfservice.controller;

import com.zwq.selfservice.entity.ManagerInfoTable;
import com.zwq.selfservice.service.ManagerInfoTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/managerInfoTable")
@Slf4j
public class ManagerInfoTableController {

    private final ManagerInfoTableService managerInfoTableService;

    public ManagerInfoTableController(ManagerInfoTableService managerInfoTableService) {
        this.managerInfoTableService = managerInfoTableService;
    }

    // 查询全部
    @GetMapping("/getManagerInfos")
    public List<ManagerInfoTable> getManagerInfos() {
        List<ManagerInfoTable> list = managerInfoTableService.list();
        log.info("获取管理员信息列表: {}", list);
        return list;
    }

    // 查询单个
    @GetMapping("/getManagerInfo")
    public ManagerInfoTable getManagerInfo(@RequestParam Long id) {
        ManagerInfoTable info = managerInfoTableService.getById(id);
        log.info("获取管理员信息: {}", info);
        return info;
    }

    // 新增
    @PostMapping("/addManagerInfo")
    public boolean addManagerInfo(@RequestBody ManagerInfoTable managerInfoTable) {
        boolean result = managerInfoTableService.save(managerInfoTable);
        log.info("新增管理员信息: {}, 结果: {}", managerInfoTable, result);
        return result;
    }

    // 修改
    @PutMapping("/updateManagerInfo")
    public boolean updateManagerInfo(@RequestBody ManagerInfoTable managerInfoTable) {
        boolean result = managerInfoTableService.updateById(managerInfoTable);
        log.info("修改管理员信息: {}, 结果: {}", managerInfoTable, result);
        return result;
    }

    // 删除
    @DeleteMapping("/deleteManagerInfo")
    public boolean deleteManagerInfo(@RequestParam Long id) {
        boolean result = managerInfoTableService.removeById(id);
        log.info("删除管理员信息: {}, 结果: {}", id, result);
        return result;
    }
}