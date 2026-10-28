package com.smartconsume.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartconsume.common.constant.Constants;
import com.smartconsume.entity.CommunityComment;
import com.smartconsume.entity.CommunityPost;
import com.smartconsume.entity.SysMessage;
import com.smartconsume.mapper.CommunityCommentMapper;
import com.smartconsume.mapper.CommunityPostMapper;
import com.smartconsume.mapper.SysMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 社区服务类
 */
@Slf4j
@Service
public class CommunityService {

    private final CommunityPostMapper communityPostMapper;
    private final CommunityCommentMapper communityCommentMapper;
    private final SysMessageMapper sysMessageMapper;

    public CommunityService(CommunityPostMapper communityPostMapper, CommunityCommentMapper communityCommentMapper, SysMessageMapper sysMessageMapper) {
        this.communityPostMapper = communityPostMapper;
        this.communityCommentMapper = communityCommentMapper;
        this.sysMessageMapper = sysMessageMapper;
    }

    /**
     * 发布帖子
     *
     * @param post 帖子信息
     * @return 帖子ID
     */
    public Long createPost(CommunityPost post) {
        post.setStatus(0); // 默认审核中
        post.setLikeCount(0);
        post.setCollectCount(0);
        communityPostMapper.insert(post);
        return post.getId();
    }

    /**
     * 查询帖子列表（已通过）
     *
     * @param tenantId 租户ID
     * @return 帖子列表
     */
    public List<CommunityPost> getPostList(Long tenantId) {
        LambdaQueryWrapper<CommunityPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityPost::getTenantId, tenantId)
                .eq(CommunityPost::getStatus, 1) // 仅查询已通过的帖子
                .eq(CommunityPost::getDelFlag, Constants.DEL_FLAG_NOT_DELETED)
                .orderByDesc(CommunityPost::getCreateTime);
        return communityPostMapper.selectList(queryWrapper);
    }

    /**
     * 查询帖子详情
     *
     * @param postId 帖子ID
     * @param tenantId 租户ID
     * @return 帖子详情
     */
    public CommunityPost getPostDetail(Long postId, Long tenantId) {
        LambdaQueryWrapper<CommunityPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityPost::getId, postId)
                .eq(CommunityPost::getTenantId, tenantId)
                .eq(CommunityPost::getDelFlag, Constants.DEL_FLAG_NOT_DELETED);
        return communityPostMapper.selectOne(queryWrapper);
    }

    /**
     * 点赞帖子
     *
     * @param postId 帖子ID
     * @param tenantId 租户ID
     */
    public void likePost(Long postId, Long tenantId) {
        CommunityPost post = communityPostMapper.selectById(postId);
        if (post != null && post.getTenantId().equals(tenantId) && post.getStatus() == 1) {
            post.setLikeCount(post.getLikeCount() + 1);
            communityPostMapper.updateById(post);
        }
    }

    /**
     * 收藏帖子
     *
     * @param postId 帖子ID
     * @param tenantId 租户ID
     */
    public void collectPost(Long postId, Long tenantId) {
        CommunityPost post = communityPostMapper.selectById(postId);
        if (post != null && post.getTenantId().equals(tenantId) && post.getStatus() == 1) {
            post.setCollectCount(post.getCollectCount() + 1);
            communityPostMapper.updateById(post);
        }
    }

    /**
     * 发布评论
     *
     * @param comment 评论信息
     * @return 评论ID
     */
    public Long createComment(CommunityComment comment) {
        communityCommentMapper.insert(comment);

        // 如果是回复评论，给原帖作者发消息
        if (comment.getParentCommentId() != null && comment.getParentCommentId() > 0) {
            // 这里简化处理，实际应该查询原评论的作者
        }

        return comment.getId();
    }

    /**
     * 查询帖子评论列表
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    public List<CommunityComment> getCommentList(Long postId) {
        LambdaQueryWrapper<CommunityComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityComment::getPostId, postId)
                .eq(CommunityComment::getParentCommentId, 0) // 仅查询顶级评论
                .eq(CommunityComment::getDelFlag, Constants.DEL_FLAG_NOT_DELETED)
                .orderByDesc(CommunityComment::getCreateTime);
        return communityCommentMapper.selectList(queryWrapper);
    }

    /**
     * 审核帖子
     *
     * @param postId 帖子ID
     * @param status 审核状态：1已通过，2已拒绝
     * @param tenantId 租户ID
     */
    public void auditPost(Long postId, Integer status, Long tenantId) {
        CommunityPost post = communityPostMapper.selectById(postId);
        if (post != null && post.getTenantId().equals(tenantId)) {
            post.setStatus(status);
            communityPostMapper.updateById(post);
        }
    }

    /**
     * 删除帖子
     *
     * @param postId 帖子ID
     * @param tenantId 租户ID
     * @param userId 用户ID
     */
    public void deletePost(Long postId, Long tenantId, Long userId) {
        CommunityPost post = communityPostMapper.selectById(postId);
        if (post != null && post.getTenantId().equals(tenantId) && post.getUserId().equals(userId)) {
            post.setDelFlag(1);
            communityPostMapper.updateById(post);
        }
    }

    /**
     * 创建社区评论通知消息
     *
     * @param postId 帖子ID
     * @param postTitle 帖子标题
     * @param commentUserId 评论者用户ID
     * @param postUserId 帖子作者用户ID
     */
    public void createCommentNotice(Long postId, String postTitle, Long commentUserId, Long postUserId) {
        if (postUserId.equals(commentUserId)) {
            return; // 自己评论自己不发消息
        }

        SysMessage message = new SysMessage();
        message.setTenantId(0L); // 租户ID需要从帖子中获取
        message.setUserId(postUserId);
        message.setMessageType("comment_notice");
        message.setTitle("评论通知");
        message.setContent(String.format("您的帖子【%s】收到了一条新评论", postTitle));
        message.setIsRead(0);
        sysMessageMapper.insert(message);
    }
}
