package com.smartconsume.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 凑单规则项DTO
 */
@Data
public class CalcRuleItemDTO {
    @NotBlank(message = "规则类型不能为空")
    private String ruleType;

    @NotNull(message = "优惠门槛不能为空")
    @DecimalMin(value = "0.00", message = "优惠门槛不能小于0")
    private BigDecimal ruleCondition;

    @NotNull(message = "优惠金额不能为空")
    @DecimalMin(value = "0.00", message = "优惠金额不能小于0")
    private BigDecimal ruleDiscount;

    private Integer isStackable;

    private String categoryLimit;
}
