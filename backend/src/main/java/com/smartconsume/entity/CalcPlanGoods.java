package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 方案内商品子表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("calc_plan_goods")
public class CalcPlanGoods extends BaseEntity {

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
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 数量
     */
    private Integer quantity;
}
