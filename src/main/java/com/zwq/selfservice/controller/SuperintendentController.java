package com.zwq.selfservice.controller;

import com.zwq.selfservice.entity.BilliardTable;
import com.zwq.selfservice.entity.ManagerInfoTable;
import com.zwq.selfservice.service.BilliardTableService;
import com.zwq.selfservice.service.ManagerInfoTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SuperintendentController {

    private ManagerInfoTableService managerInfoTableService;

    private BilliardTableService billiardTableService;

    @Autowired
    private ManagerInfoTableService setManagerInfoTableService(ManagerInfoTableService managerInfoTableService){
        return this.managerInfoTableService = managerInfoTableService;
    }

    @Autowired
    private BilliardTableService setBilliardTableService(BilliardTableService billiardTableService){
        return this.billiardTableService = billiardTableService;
    }

    /**
     * 获取管理员列表
     * @return
     */
    @RequestMapping(method= RequestMethod.GET,path = "/manager/list")
    public List<ManagerInfoTable> getManagerInfoList(){
        return managerInfoTableService.list();
    }

    /**
     * 根据ID获取管理员信息
     * @param id
     * @return
     */
    @RequestMapping(method= RequestMethod.GET,path = "/manager/{id}")
    public ManagerInfoTable getManagerInfo(@PathVariable Integer id){
        return managerInfoTableService.getById(id);
    }

    /**
     * 新增管理员
     * @param managerInfoTable
     * @return
     */
    @RequestMapping(method= RequestMethod.POST,path = "/manager/save")
    public Boolean saveManagerInfo(ManagerInfoTable managerInfoTable){
        return managerInfoTableService.save(managerInfoTable);
    }

    /**
     * 修改管理员信息
     * @param managerInfoTable
     * @return
     */
    @RequestMapping(method= RequestMethod.PUT,path = "/manager/update")
    public Boolean updateManagerInfo(ManagerInfoTable managerInfoTable){
        return managerInfoTableService.updateById(managerInfoTable);
    }

    /**
     * 删除管理员
     * @param id
     * @return
     */
    @RequestMapping(method= RequestMethod.DELETE,path = "/manager/{id}")
    public Boolean deleteManagerInfo(@PathVariable Integer id){
        return managerInfoTableService.removeById(id);
    }


    /**
     * 获取球台列表
     * @return
     */
    @RequestMapping(method= RequestMethod.GET,path = "/billiard/list")
    public List<BilliardTable> getBilliardList(){
        return billiardTableService.list();
    }

    /**
     * 根据ID获取球台信息
     * @param id
     * @return
     */
    @RequestMapping(method= RequestMethod.GET,path = "/billiard/{id}")
    public BilliardTable getBilliard(@PathVariable Integer id){
        return billiardTableService.getById(id);
    }

    /**
     * 新增球台
     * @param billiardTable
     * @return
     */
    @RequestMapping(method= RequestMethod.POST,path = "/billiard/save")
    public Boolean saveBilliard(BilliardTable billiardTable){
        return billiardTableService.save(billiardTable);
    }

    /**
     * 修改球台信息
     * @param billiardTable
     * @return
     */
    @RequestMapping(method= RequestMethod.PUT,path = "/billiard/update")
    public Boolean updateBilliard(BilliardTable billiardTable){
        return billiardTableService.updateById(billiardTable);
    }

    /**
     * 删除球台
     * @param id
     * @return
     */
    @RequestMapping(method= RequestMethod.DELETE,path = "/billiard/{id}")
    public Boolean deleteBilliard(@PathVariable Integer id){
        return billiardTableService.removeById(id);
    }

}
