package com.zwq.selfservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zwq.selfservice.entity.DetailsTable;
import com.zwq.selfservice.entity.WechatTable;
import com.zwq.selfservice.service.ApiService;
import com.zwq.selfservice.service.DetailsTableService;
import com.zwq.selfservice.service.WechatTableService;
import com.zwq.selfservice.util.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zwq.selfservice.util.CommonConstant.OPEN_DEPOSIT_TYPE;
import static com.zwq.selfservice.util.CommonConstant.OPEN_VIP_TYPE;

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

    @Autowired
    private WechatTableService wechatTableService;

    @Autowired
    private ApiService apiService;

    @Value("${info.desc}")
    public String descInfo;

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

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(method = RequestMethod.GET, path = "/getInfo")
    public ResponseData getInfo(String token,String tableId,int type) {
        log.info("获取开台信息======tableId:{},type:{}",tableId,type);
        ResponseData responseData = new ResponseData();
        HashMap<String, Object> map = new HashMap<>();
        map.put("descInfo",descInfo);
        WechatTable wechatInfo = wechatTableService.getOne(new QueryWrapper<WechatTable>()
                .eq("TOKEN",token));
        List<DetailsTable> list = detailsTableService.list(new QueryWrapper<DetailsTable>()
                .eq("table_number", tableId)
                .eq("user_info", wechatInfo.getOpenId())
                .orderByDesc( "start_time"));
        if (list == null || list.isEmpty()){
            responseData.setCode(400);
            return responseData;
        }
        DetailsTable detailsTable = list.get(0);
        LocalDateTime startTime = detailsTable.getStartTime();
        LocalDateTime endTime = detailsTable.getEndTime();
        if (endTime != null && endTime.isBefore(LocalDateTime.now())) {
            responseData.setCode(400);
            return responseData;
        }
        if (type == 0){
            type = detailsTable.getOpenType();
        }
        long seconds;
        if (OPEN_VIP_TYPE == type || OPEN_DEPOSIT_TYPE == type) {
            long epochSecond = startTime.atZone(ZoneId.systemDefault()).toEpochSecond();
            seconds = Duration.between(startTime, LocalDateTime.now()).toSeconds();
            long maxSeconds = apiService.getOpenTime(Integer.parseInt(tableId) + 110) - epochSecond;
            map.put("maxSeconds",maxSeconds);
        } else {
            seconds = Duration.between(LocalDateTime.now(), endTime).toSeconds();
            map.put("maxSeconds",0);
        }
        map.put("remainingSeconds", String.valueOf(seconds));
        map.put("type", detailsTable.getOpenType());
        log.info("获取台球桌返回信息======{}",map);
        responseData.setCode(200);
        responseData.setData(map);
        return responseData;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/OpenTime")
    public Map<Integer,String> getOpenTime(){
        return apiService.getOpenTimeMap();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/OpenTime")
    public String putOpenTime(Integer key,String value){
        return apiService.putOpenTimeMap(key,value);
    }

}