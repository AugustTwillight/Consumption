package com.smartconsume.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 凑单方案保存DTO
 */
@Data
public class CalcPlanSaveDTO {
    @NotBlank(message = "方案名称不能为空")
    private String planName;

    @Valid
    @NotEmpty(message = "商品列表不能为空")
    private List<CalcPlanGoodsItemDTO> goodsList;

    @Valid
    private List<CalcRuleItemDTO> ruleList;

    @NotNull(message = "商品总金额不能为空")
    private BigDecimal totalAmount;

    @NotNull(message = "总优惠金额不能为空")
    private BigDecimal totalDiscount;

    @NotNull(message = "实付总金额不能为空")
    private BigDecimal finalAmount;

    private String riskTags;
}
