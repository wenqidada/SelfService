package com.zwq.selfservice.service.impl;

import com.zwq.selfservice.entity.BilliardTable;
import com.zwq.selfservice.dao.BilliardTableDao;
import com.zwq.selfservice.service.BilliardTableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwq.selfservice.vo.GetTableResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zwq
 * @since 2025-06-25
 */
@Service
public class BilliardTableServiceImp extends ServiceImpl<BilliardTableDao, BilliardTable> implements BilliardTableService {

    @Override
    public List<GetTableResponse> getTables() {
        List<BilliardTable> list = this.list();
        return list.stream().map(billiardTable -> GetTableResponse.builder()
                .tableNumber(billiardTable.getTableNumber())
                .useType(billiardTable.getUseType())
                .tableType(billiardTable.getTableType())
                .build()).toList();
    }

}
