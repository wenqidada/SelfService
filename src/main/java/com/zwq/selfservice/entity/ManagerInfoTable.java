package com.zwq.selfservice.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
 * @since 2025-06-25
 */
@Getter
@Setter
@TableName("MANAGER_INFO_TABLE")
public class ManagerInfoTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员编号
     */
    @TableId(value = "MANAGER_ID", type = IdType.AUTO)
    private Integer managerId;

    /**
     * 管理员姓名
     */
    @TableField("MANAGER_NAME")
    private String managerName;

    /**
     * 密码
     */
    @TableField("PASSWORD")
    private String password;

    /**
     * 手机号
     */
    @TableField("PHONE")
    private String phone;

    /**
     * 微信号
     */
    @TableField("WECHAT")
    private String wechat;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
