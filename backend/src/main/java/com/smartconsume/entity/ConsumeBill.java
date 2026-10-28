package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 消费账单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("consume_bill")
public class ConsumeBill extends BaseEntity {

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
     * 关联凑单方案ID
     */
    private Long planId;

    /**
     * 账单名称
     */
    private String billName;

    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;

    /**
     * 预期使用周期（天）
     */
    private Integer expectedCycle;

    /**
     * 标签，逗号分隔
     */
    private String tags;

    /**
     * 备注
     */
    private String remarks;
}
