package com.zwq.selfservice.controller;

import com.zwq.selfservice.entity.BilliardTable;
import com.zwq.selfservice.service.BilliardTableService;
import com.zwq.selfservice.vo.GetTableResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/billiardTable")
@Slf4j
public class BilliardTableController {

    private final BilliardTableService billiardTableService;

    public BilliardTableController(BilliardTableService billiardTableService) {
        this.billiardTableService = billiardTableService;
    }


    @RequestMapping(method= RequestMethod.GET,path = "/getTables")
    public List<GetTableResponse> getTables(){
        List<GetTableResponse> tables = billiardTableService.getTables();
        log.info("获取台球桌信息: {}", tables);
        return tables;
    }

    // 查询全部
    @RequestMapping(method = RequestMethod.GET, path = "/getTableList")
    public List<BilliardTable> getTableList() {
        List<BilliardTable> tables = billiardTableService.list();
        log.info("获取台球桌信息: {}", tables);
        return tables;
    }

    // 查询单个
    @RequestMapping(method = RequestMethod.GET, path = "/getTable")
    public BilliardTable getTable(Long id) {
        BilliardTable table = billiardTableService.getById(id);
        log.info("获取台球桌详情: {}", table);
        return table;
    }

    // 新增
    @RequestMapping(method = RequestMethod.POST, path = "/addTable")
    public boolean addTable(@RequestBody BilliardTable table) {
        boolean result = billiardTableService.save(table);
        log.info("新增台球桌: {}, 结果: {}", table, result);
        return result;
    }

    // 修改
    @RequestMapping(method = RequestMethod.PUT, path = "/updateTable")
    public boolean updateTable(@RequestBody BilliardTable table) {
        boolean result = billiardTableService.updateById(table);
        log.info("修改台球桌: {}, 结果: {}", table, result);
        return result;
    }

    // 删除
    @RequestMapping(method = RequestMethod.DELETE, path = "/deleteTable")
    public boolean deleteTable(Long id) {
        boolean result = billiardTableService.removeById(id);
        log.info("删除台球桌: {}, 结果: {}", id, result);
        return result;
    }

}
