package com.smartconsume.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论创建DTO
 */
@Data
public class CommentCreateDTO {
    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    private Long parentCommentId;

    @NotBlank(message = "评论内容不能为空")
    private String content;
}
