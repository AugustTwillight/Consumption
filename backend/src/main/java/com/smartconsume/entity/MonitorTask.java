package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 监控任务实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("monitor_task")
public class MonitorTask extends BaseEntity {

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
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品链接
     */
    private String goodsLink;

    /**
     * 平台：taobao/jd/pdd
     */
    private String platform;

    /**
     * 当前监控价格
     */
    private BigDecimal currentPrice;

    /**
     * 目标提醒价
     */
    private BigDecimal targetPrice;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;
}
