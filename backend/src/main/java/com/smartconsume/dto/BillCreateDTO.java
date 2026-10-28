package com.smartconsume.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 账单创建DTO
 */
@Data
public class BillCreateDTO {
    @NotBlank(message = "账单名称不能为空")
    private String billName;

    private Long planId;

    @NotNull(message = "实际支付金额不能为空")
    @DecimalMin(value = "0.00", message = "实际支付金额不能小于0")
    private BigDecimal actualAmount;

    private Integer expectedCycle;

    private String tags;

    private String remarks;
}
