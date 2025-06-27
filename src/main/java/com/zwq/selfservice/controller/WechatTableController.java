package com.zwq.selfservice.controller;

import com.zwq.selfservice.entity.WechatTable;
import com.zwq.selfservice.service.WechatTableService;
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
@RequestMapping("/wechatTable")
@Slf4j
public class WechatTableController {

    @Autowired
    private WechatTableService wechatTableService;

    // 新增
    @PostMapping
    public boolean create(@RequestBody WechatTable wechatTable) {
        log.info("调用create方法，参数: {}", wechatTable);
        boolean result = wechatTableService.save(wechatTable);
        log.info("新增微信记录，参数: {}，结果: {}", wechatTable, result);
        return result;
    }

    // 查询所有
    @GetMapping
    public List<WechatTable> list() {
        log.info("调用list方法，获取所有微信记录");
        List<WechatTable> result = wechatTableService.list();
        log.info("获取微信记录列表成功，结果: {}", result);
        return result;
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public WechatTable getById(@PathVariable Long id) {
        log.info("调用getById方法，参数id: {}", id);
        WechatTable result = wechatTableService.getById(id);
        log.info("获取微信记录详情成功，结果: {}", result);
        return result;
    }

    // 修改
    @PutMapping("/{id}")
    public boolean update(@PathVariable Integer id, @RequestBody WechatTable wechatTable) {
        log.info("调用update方法，参数id: {}，参数: {}", id, wechatTable);
        wechatTable.setId(id);
        boolean result = wechatTableService.updateById(wechatTable);
        log.info("修改微信记录，参数: {}，结果: {}", wechatTable, result);
        return result;
    }

    // 删除
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("调用delete方法，参数id: {}", id);
        wechatTableService.removeById(id);
        log.info("删除微信记录，id: {}，结果: 成功", id);
    }
}