package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户积分流水实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_point_log")
public class UserPointLog extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 积分变动值（正为获得，负为消耗）
     */
    private Integer pointChange;

    /**
     * 类型：earn/consume
     */
    private String pointType;

    /**
     * 来源类型：calc/monitor/post/etc
     */
    private String sourceType;

    /**
     * 来源ID
     */
    private Long sourceId;

    /**
     * 备注
     */
    private String remark;
}
