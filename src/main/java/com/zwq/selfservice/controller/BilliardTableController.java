package com.zwq.selfservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zwq.selfservice.entity.BilliardTable;
import com.zwq.selfservice.entity.DetailsTable;
import com.zwq.selfservice.entity.ManagerInfoTable;
import com.zwq.selfservice.entity.WechatTable;
import com.zwq.selfservice.service.BilliardTableService;
import com.zwq.selfservice.service.DetailsTableService;
import com.zwq.selfservice.service.ManagerInfoTableService;
import com.zwq.selfservice.service.WechatTableService;
import com.zwq.selfservice.util.ResponseData;
import com.zwq.selfservice.vo.GetTableResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    private final WechatTableService wechatTableService;

    private final DetailsTableService detailsTableService;

    private final ManagerInfoTableService managerInfoTableService;

    public BilliardTableController(BilliardTableService billiardTableService, WechatTableService wechatTableService, DetailsTableService detailsTableService, ManagerInfoTableService managerInfoTableService) {
        this.billiardTableService = billiardTableService;
        this.wechatTableService = wechatTableService;
        this.detailsTableService = detailsTableService;
        this.managerInfoTableService = managerInfoTableService;
    }


    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(method= RequestMethod.GET,path = "/getTables")
    public ResponseData getTables(String token){
        log.info("获取球桌信息接口被调用");
        ResponseData responseData = new ResponseData();
        try {
            if (token != null && !token.isEmpty()) {
                WechatTable wechatInfo = wechatTableService.getOne(new QueryWrapper<WechatTable>().eq("token",token));
                List<DetailsTable> list = detailsTableService.list(new QueryWrapper<DetailsTable>()
                        .eq("user_info", wechatInfo.getOpenId())
                        .orderByDesc( "start_time"));
                ManagerInfoTable one = managerInfoTableService.getOne(new QueryWrapper<ManagerInfoTable>().eq("wechat",wechatInfo.getOpenId()));
                List<GetTableResponse> tables = billiardTableService.getTables(one == null || one.getManagerName() == null);
                if (list != null && !list.isEmpty()) {
                    DetailsTable detailsTable = list.get(0);
                    LocalDateTime endTime = detailsTable.getEndTime();
                    if (endTime == null || endTime.isAfter(LocalDateTime.now())) {
                        for (GetTableResponse table : tables) {
                            if (table.getTableNumber() == (int)detailsTable.getTableNumber()){
                                table.setUseType((byte)0);
                            }
                        }
                    }
                }
                responseData.setData(tables);
                responseData.setCode(200);
                responseData.setMessage("获取台球桌信息成功");
            }else {
                responseData.setCode(400);
                responseData.setMessage("获取台球桌信息失败");
            }
        }catch (Exception e) {
            log.error("获取台球桌信息失败: {}", e.getMessage());
            responseData.setCode(500);
            responseData.setMessage("获取台球桌信息失败: " + e.getMessage());
            return responseData;
        }
        return responseData;
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
