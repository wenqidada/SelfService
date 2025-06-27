package com.zwq.selfservice.controller;

import com.zwq.selfservice.entity.DetailsTable;
import com.zwq.selfservice.service.DetailsTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zwq
 * @since 2025-06-25
 */
@RestController
@RequestMapping("/detailsTable")
@Slf4j
public class DetailsTableController {

    @Autowired
    private DetailsTableService detailsTableService;

    // 新增
    @PostMapping
    public boolean create(@RequestBody DetailsTable detailsTable) {
        log.info("调用create方法，参数: {}", detailsTable);
        boolean result = detailsTableService.save(detailsTable);
        log.info("新增明细记录，参数: {}，结果: {}", detailsTable, result);
        return result;
    }

    // 查询所有
    @GetMapping
    public List<DetailsTable> list() {
        log.info("调用list方法，获取所有明细记录");
        List<DetailsTable> result = detailsTableService.list();
        log.info("获取明细记录列表成功，结果: {}", result);
        return result;
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public DetailsTable getById(@PathVariable Long id) {
        log.info("调用getById方法，参数id: {}", id);
        DetailsTable result = detailsTableService.getById(id);
        log.info("获取明细记录详情成功，结果: {}", result);
        return result;
    }

    // 修改
    @PutMapping("/{id}")
    public boolean update(@PathVariable Integer id, @RequestBody DetailsTable detailsTable) {
        log.info("调用update方法，参数id: {}，参数: {}", id, detailsTable);
        detailsTable.setId(id);
        boolean result = detailsTableService.updateById(detailsTable);
        log.info("修改明细记录，参数: {}，结果: {}", detailsTable, result);
        return result;
    }

    // 删除
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("调用delete方法，参数id: {}", id);
        detailsTableService.removeById(id);
        log.info("删除明细记录，id: {}，结果: 成功", id);
    }
}