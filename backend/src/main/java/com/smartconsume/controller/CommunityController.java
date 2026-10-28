package com.smartconsume.controller;

import com.smartconsume.common.result.Result;
import com.smartconsume.entity.CommunityComment;
import com.smartconsume.entity.CommunityPost;
import com.smartconsume.service.CommunityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 社区控制器
 */
@RestController
@RequestMapping("/api/v1/community")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    /**
     * 发布帖子
     *
     * @param request 请求参数 {title, content, images, goodsLink}
     * @param httpRequest HTTP请求
     * @return 帖子ID
     */
    @PostMapping("/post")
    public Result<Long> createPost(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        CommunityPost post = new CommunityPost();
        post.setTenantId(tenantId);
        post.setUserId(userId);
        post.setTitle((String) request.get("title"));
        post.setContent((String) request.get("content"));
        post.setImages((String) request.get("images"));
        post.setGoodsLink((String) request.get("goodsLink"));

        communityService.createPost(post);
        return Result.success(post.getId());
    }

    /**
     * 查询帖子列表
     *
     * @param httpRequest HTTP请求
     * @return 帖子列表
     */
    @GetMapping("/post-list")
    public Result<List<CommunityPost>> getPostList(HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");

        List<CommunityPost> posts = communityService.getPostList(tenantId);
        return Result.success(posts);
    }

    /**
     * 查询帖子详情
     *
     * @param postId 帖子ID
     * @param httpRequest HTTP请求
     * @return 帖子详情
     */
    @GetMapping("/post-detail/{postId}")
    public Result<CommunityPost> getPostDetail(@PathVariable Long postId, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");

        CommunityPost post = communityService.getPostDetail(postId, tenantId);
        if (post == null) {
            return Result.fail(404, "帖子不存在");
        }
        return Result.success(post);
    }

    /**
     * 点赞帖子
     *
     * @param postId 帖子ID
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PostMapping("/post-like/{postId}")
    public Result<Void> likePost(@PathVariable Long postId, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");

        communityService.likePost(postId, tenantId);
        return Result.success();
    }

    /**
     * 收藏帖子
     *
     * @param postId 帖子ID
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PostMapping("/post-collect/{postId}")
    public Result<Void> collectPost(@PathVariable Long postId, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");

        communityService.collectPost(postId, tenantId);
        return Result.success();
    }

    /**
     * 发布评论
     *
     * @param request 请求参数 {postId, parentCommentId, content}
     * @param httpRequest HTTP请求
     * @return 评论ID
     */
    @PostMapping("/comment")
    public Result<Long> createComment(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        CommunityComment comment = new CommunityComment();
        comment.setPostId(((Number) request.get("postId")).longValue());
        comment.setTenantId(tenantId);
        comment.setUserId(userId);
        comment.setParentCommentId(request.get("parentCommentId") != null ? ((Number) request.get("parentCommentId")).longValue() : 0L);
        comment.setContent((String) request.get("content"));

        communityService.createComment(comment);
        return Result.success(comment.getId());
    }

    /**
     * 查询帖子评论列表
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    @GetMapping("/comment-list/{postId}")
    public Result<List<CommunityComment>> getCommentList(@PathVariable Long postId) {
        List<CommunityComment> comments = communityService.getCommentList(postId);
        return Result.success(comments);
    }

    /**
     * 审核帖子
     *
     * @param postId 帖子ID
     * @param status 审核状态：1已通过，2已拒绝
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PutMapping("/post-audit/{postId}/{status}")
    public Result<Void> auditPost(@PathVariable Long postId, @PathVariable Integer status, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");

        communityService.auditPost(postId, status, tenantId);
        return Result.success();
    }

    /**
     * 删除帖子
     *
     * @param postId 帖子ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/post-delete/{postId}")
    public Result<Void> deletePost(@PathVariable Long postId, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        communityService.deletePost(postId, tenantId, userId);
        return Result.success();
    }
}
