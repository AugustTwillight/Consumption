package com.smartconsume.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 帖子创建DTO
 */
@Data
public class PostCreateDTO {
    @NotBlank(message = "帖子标题不能为空")
    private String title;

    private String content;

    private String images;

    private String goodsLink;
}
