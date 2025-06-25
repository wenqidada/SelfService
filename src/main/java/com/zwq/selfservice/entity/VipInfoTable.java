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
 * @since 2025-06-25
 */
@Getter
@Setter
@TableName("VIP_INFO_TABLE")
public class VipInfoTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会员编号
     */
    @TableId(value = "VIP_ID", type = IdType.AUTO)
    private Integer vipId;

    /**
     * 用户信息
     */
    @TableField("VIP_USER")
    private String vipUser;

    /**
     * 余额
     */
    @TableField("BALANCE")
    private BigDecimal balance;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
