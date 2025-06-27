package com.zwq.selfservice.controller;

import com.zwq.selfservice.entity.TripartiteTable;
import com.zwq.selfservice.service.TripartiteTableService;
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
@RequestMapping("/tripartiteTable")
@Slf4j
public class TripartiteTableController {

    @Autowired
    private TripartiteTableService tripartiteTableService;

    // 新增
    @PostMapping
    public boolean create(@RequestBody TripartiteTable tripartiteTable) {
        log.info("调用create方法，参数: {}", tripartiteTable);
        boolean result = tripartiteTableService.save(tripartiteTable);
        log.info("新增三方记录，参数: {}，结果: {}", tripartiteTable, result);
        return result;
    }

    // 查询所有
    @GetMapping
    public List<TripartiteTable> list() {
        log.info("调用list方法，获取所有三方记录");
        List<TripartiteTable> result = tripartiteTableService.list();
        log.info("获取三方记录列表成功，结果: {}", result);
        return result;
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public TripartiteTable getById(@PathVariable Long id) {
        log.info("调用getById方法，参数id: {}", id);
        TripartiteTable result = tripartiteTableService.getById(id);
        log.info("获取三方记录详情成功，结果: {}", result);
        return result;
    }

    // 修改
    @PutMapping("/{id}")
    public boolean update(@PathVariable Integer id, @RequestBody TripartiteTable tripartiteTable) {
        log.info("调用update方法，参数id: {}，参数: {}", id, tripartiteTable);
        tripartiteTable.setId(id);
        boolean result = tripartiteTableService.updateById(tripartiteTable);
        log.info("修改三方记录，参数: {}，结果: {}", tripartiteTable, result);
        return result;
    }

    // 删除
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("调用delete方法，参数id: {}", id);
        tripartiteTableService.removeById(id);
        log.info("删除三方记录，id: {}，结果: 成功", id);
    }
}