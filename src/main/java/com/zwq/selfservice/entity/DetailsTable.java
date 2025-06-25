package com.zwq.selfservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author zwq
 * @since 2025-06-25
 */
@Getter
@Setter
@TableName("DETAILS_TABLE")
public class DetailsTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户信息
     */
    @TableField("USER_INFO")
    private String userInfo;

    /**
     * 开台时间
     */
    @TableField("START_TIME")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("END_TIME")
    private LocalDateTime endTime;

    /**
     * 开台类型,1会员2押金3定时4抖音5美团
     */
    @TableField("OPEN_TYPE")
    private Byte openType;

    /**
     * 台号
     */
    @TableField("TABLE_NUMBER")
    private Byte tableNumber;

    /**
     * 劵码,无劵码为0
     */
    @TableField("COUPON_CODE")
    private String couponCode;

    /**
     * 消费金额
     */
    @TableField("MONEY")
    private BigDecimal money;
}
