package com.zwq.selfservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zwq.selfservice.entity.ManagerInfoTable;
import com.zwq.selfservice.service.ManagerInfoTableService;
import com.zwq.selfservice.util.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/managerInfoTable")
@Slf4j
public class ManagerInfoTableController {

    private final ManagerInfoTableService managerInfoTableService;

    public ManagerInfoTableController(ManagerInfoTableService managerInfoTableService) {
        this.managerInfoTableService = managerInfoTableService;
    }

    // PC登录
    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping("/login")
    public boolean login(String name,String password) {
        ManagerInfoTable manager = managerInfoTableService.getOne(
                new QueryWrapper<ManagerInfoTable>()
                        .eq("manager_name", name).eq("password", password));
        return manager != null;
    }

    // 查询全部
    @GetMapping("/getManagerInfos")
    public List<ManagerInfoTable> getManagerInfos() {
        List<ManagerInfoTable> list = managerInfoTableService.list();
        log.info("获取管理员信息列表: {}", list);
        return list;
    }


    // 查询全部
    @GetMapping("/getManagerPhone")
    public ResponseData getManagerPhone() {
        List<ManagerInfoTable> list = managerInfoTableService.list();
        List<String> phone = list.stream().map(ManagerInfoTable::getPhone).toList();
        int randomIndex = ThreadLocalRandom.current().nextInt(phone.size());
        ResponseData responseData = new ResponseData();
        responseData.setData(phone.get(randomIndex));
        responseData.setCode(200);
        responseData.setMessage("获取管理员电话成功");
        return responseData;
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