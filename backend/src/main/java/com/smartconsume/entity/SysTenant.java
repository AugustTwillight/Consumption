package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 租户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
public class SysTenant extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * License授权密钥
     */
    private String licenseKey;

    /**
     * 最大用户数量
     */
    private Integer maxUser;

    /**
     * 授权到期时间
     */
    private LocalDateTime expireTime;

    /**
     * 状态：0冻结，1正常
     */
    private Integer status;
}
