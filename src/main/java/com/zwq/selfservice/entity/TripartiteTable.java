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
@TableName("TRIPARTITE_TABLE")
public class TripartiteTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 类型:1抖音2美团3其它
     */
    @TableField("PLATFORM_TYPE")
    private Byte platformType;

    /**
     * 券码
     */
    @TableField("CODES")
    private String codes;

    /**
     * 加密券码
     */
    @TableField("ENCRYPTED_CODES")
    private String encryptedCodes;

    /**
     * 请求Id
     */
    @TableField("REQUEST_ID")
    private String requestId;

    /**
     * 实付金额
     */
    @TableField("ACTUAL_PAYMENT")
    private BigDecimal actualPayment;

    /**
     * 订单金额
     */
    @TableField("AMOUNT")
    private BigDecimal amount;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
