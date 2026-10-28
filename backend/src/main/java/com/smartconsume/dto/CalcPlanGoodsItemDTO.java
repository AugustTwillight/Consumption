package com.smartconsume.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 凑单商品项DTO
 */
@Data
public class CalcPlanGoodsItemDTO {
    @NotBlank(message = "商品名称不能为空")
    private String goodsName;

    private String goodsLink;

    private String platform;

    @NotNull(message = "商品单价不能为空")
    @DecimalMin(value = "0.00", message = "商品单价不能小于0")
    private BigDecimal unitPrice;

    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    private Integer quantity;
}
