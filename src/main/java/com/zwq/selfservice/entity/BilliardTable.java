package com.zwq.selfservice.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
 * @since 2025-02-16
 */
@Getter
@Setter
@TableName("BILLIARD_TABLE")
public class BilliardTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 台号
     */
    @TableId(value = "TABLE_NUMBER", type = IdType.AUTO)
    private Integer tableNumber;

    /**
     * 锁编号
     */
    @TableField("LOCK_NO")
    private String lockNo;

    /**
     * 二维码链接
     */
    @TableField("QR_CODE")
    private String qrCode;

    /**
     * 球桌信息
     */
    @TableField("TABLE_INFO")
    private String tableInfo;

    /**
     * 台子类型,1台球桌2棋牌室3存杆柜
     */
    @TableField("TABLE_TYPE")
    private Byte tableType;

    /**
     * 费用/小时
     */
    @TableField("COST")
    private BigDecimal cost;

    /**
     * 修改人编号
     */
    @TableField("UPDATE_USER_ID")
    private Byte updateUserId;

    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
