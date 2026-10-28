package com.smartconsume.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 积分添加DTO
 */
@Data
public class PointAddDTO {
    @NotNull(message = "积分变动值不能为空")
    private Integer pointChange;

    @NotBlank(message = "积分类型不能为空")
    private String pointType;

    @NotBlank(message = "来源类型不能为空")
    private String sourceType;

    private Long sourceId;

    private String remark;
}
