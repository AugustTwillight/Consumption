package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 优惠规则子表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("calc_rule")
public class CalcRule extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 方案ID
     */
    private Long planId;

    /**
     * 规则类型：full_reduce/category_coupon/shop_coupon/cross_store_discount
     */
    private String ruleType;

    /**
     * 优惠门槛金额
     */
    private BigDecimal ruleCondition;

    /**
     * 优惠减免金额
     */
    private BigDecimal ruleDiscount;

    /**
     * 是否可叠加：0否，1是
     */
    private Integer isStackable;

    /**
     * 品类限制，逗号分隔
     */
    private String categoryLimit;
}
