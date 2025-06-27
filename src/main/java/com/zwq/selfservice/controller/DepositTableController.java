package com.zwq.selfservice.controller;

import com.zwq.selfservice.entity.DepositTable;
import com.zwq.selfservice.service.DepositTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/depositTable")
@Slf4j
public class DepositTableController {

    @Autowired
    private DepositTableService depositTableService;

    // 新增
    @PostMapping
    public boolean create(@RequestBody DepositTable depositTable) {
        log.info("调用create方法，参数: {}", depositTable);
        boolean result = depositTableService.save(depositTable);
        log.info("新增押金记录，参数: {}，结果: {}", depositTable, result);
        return result;
    }

    // 查询所有
    @GetMapping
    public List<DepositTable> list() {
        log.info("调用list方法，获取所有押金记录");
        List<DepositTable> result = depositTableService.list();
        log.info("获取押金记录列表成功，结果: {}", result);
        return result;
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public DepositTable getById(@PathVariable Long id) {
        log.info("调用getById方法，参数id: {}", id);
        DepositTable result = depositTableService.getById(id);
        log.info("获取押金记录详情成功，结果: {}", result);
        return result;
    }

    // 修改
    @PutMapping("/{id}")
    public boolean update(@PathVariable Integer id, @RequestBody DepositTable depositTable) {
        log.info("调用update方法，参数id: {}，参数: {}", id, depositTable);
        depositTable.setId(id);
        boolean result = depositTableService.updateById(depositTable);
        log.info("修改押金记录，参数: {}，结果: {}", depositTable, result);
        return result;
    }

    // 删除
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("调用delete方法，参数id: {}", id);
        depositTableService.removeById(id);
        log.info("删除押金记录，id: {}，结果: 成功", id);
    }
}