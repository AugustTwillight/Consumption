package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 凑单方案主表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("calc_plan")
public class CalcPlan extends BaseEntity {

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
     * 方案名称
     */
    private String planName;

    /**
     * 商品总金额
     */
    private BigDecimal totalAmount;

    /**
     * 总优惠金额
     */
    private BigDecimal totalDiscount;

    /**
     * 实付总金额
     */
    private BigDecimal finalAmount;

    /**
     * 风险标签，逗号分隔
     */
    private String riskTags;

    /**
     * 状态：1正常，2已删除
     */
    private Integer status;
}
