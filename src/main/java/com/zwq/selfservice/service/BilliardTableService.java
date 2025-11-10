package com.zwq.selfservice.service;

import com.zwq.selfservice.entity.BilliardTable;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zwq.selfservice.vo.GetTableResponse;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zwq
 * @since 2025-06-25
 */
public interface BilliardTableService extends IService<BilliardTable> {

    List<GetTableResponse> getTables(boolean flag);

}
