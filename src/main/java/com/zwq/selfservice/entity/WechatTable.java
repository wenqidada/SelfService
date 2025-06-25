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
@TableName("WECHAT_TABLE")
public class WechatTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户唯一标识(小程序独立)
     */
    @TableField("OPEN_ID")
    private String openId;

    /**
     * session_key
     */
    @TableField("SESSION_KEY")
    private String sessionKey;

    /**
     * 会话token
     */
    @TableField("TOKEN")
    private String token;

    /**
     * 登陆时间
     */
    @TableField("LOGIN_TIME")
    private LocalDateTime loginTime;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
