package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 社区评论实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("community_comment")
public class CommunityComment extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 父评论ID，0为顶级评论
     */
    private Long parentCommentId;

    /**
     * 评论内容
     */
    private String content;
}
