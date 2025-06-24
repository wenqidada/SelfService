package com.zwq.selfservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author zwq
 * @since 2025-02-16
 */
@Getter
@Setter
@TableName("DEPOSIT_TABLE")
public class DepositTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 存杆柜记录编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 存杆柜编号
     */
    @TableField("DEPOSIT_ID")
    private Integer depositId;

    /**
     * 用户信息
     */
    @TableField("DEPOSIT_USER")
    private String depositUser;

    /**
     * 存放开始时间
     */
    @TableField("START_TIME")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("END_TIME")
    private LocalDateTime endTime;
}
