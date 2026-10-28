package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * License校验日志实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_license_log")
public class SysLicenseLog extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 校验时间
     */
    private LocalDateTime checkTime;

    /**
     * 校验结果：0失败，1成功
     */
    private Integer result;

    /**
     * 校验消息
     */
    private String message;
}
