package com.smartconsume.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 监控任务保存DTO
 */
@Data
public class MonitorTaskSaveDTO {
    @NotBlank(message = "商品名称不能为空")
    private String goodsName;

    private String goodsLink;

    private String platform;

    @NotNull(message = "当前价格不能为空")
    @DecimalMin(value = "0.00", message = "当前价格不能小于0")
    private BigDecimal currentPrice;

    @NotNull(message = "目标价格不能为空")
    @DecimalMin(value = "0.00", message = "目标价格不能小于0")
    private BigDecimal targetPrice;
}
