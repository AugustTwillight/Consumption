package com.smartconsume.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 凑单计算请求DTO
 */
@Data
public class CalcPlanCalculateDTO {
    private String planName;

    @Valid
    @NotEmpty(message = "商品列表不能为空")
    private List<CalcPlanGoodsItemDTO> goodsList;

    @Valid
    private List<CalcRuleItemDTO> ruleList;
}
