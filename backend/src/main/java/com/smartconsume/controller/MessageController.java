package com.smartconsume.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartconsume.common.constant.Constants;
import com.smartconsume.common.result.Result;
import com.smartconsume.entity.SysMessage;
import com.smartconsume.mapper.SysMessageMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站内消息控制器
 */
@RestController
@RequestMapping("/api/v1/message")
public class MessageController {

    private final SysMessageMapper sysMessageMapper;

    public MessageController(SysMessageMapper sysMessageMapper) {
        this.sysMessageMapper = sysMessageMapper;
    }

    /**
     * 查询消息列表
     *
     * @param isRead 是否已读筛选（可选）
     * @param httpRequest HTTP请求
     * @return 消息列表
     */
    @GetMapping("/list")
    public Result<List<SysMessage>> getMessageList(@RequestParam(required = false) Integer isRead, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        LambdaQueryWrapper<SysMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMessage::getTenantId, tenantId)
                .eq(SysMessage::getUserId, userId);

        if (isRead != null) {
            queryWrapper.eq(SysMessage::getIsRead, isRead);
        }

        queryWrapper.orderByDesc(SysMessage::getCreateTime);
        List<SysMessage> messages = sysMessageMapper.selectList(queryWrapper);
        return Result.success(messages);
    }

    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PutMapping("/read/{messageId}")
    public Result<Void> markAsRead(@PathVariable Long messageId, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        SysMessage message = sysMessageMapper.selectById(messageId);
        if (message != null && message.getTenantId().equals(tenantId) && message.getUserId().equals(userId)) {
            message.setIsRead(1);
            sysMessageMapper.updateById(message);
        }
        return Result.success();
    }

    /**
     * 一键全部已读
     *
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        LambdaQueryWrapper<SysMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMessage::getTenantId, tenantId)
                .eq(SysMessage::getUserId, userId)
                .eq(SysMessage::getIsRead, 0);

        List<SysMessage> messages = sysMessageMapper.selectList(queryWrapper);
        for (SysMessage message : messages) {
            message.setIsRead(1);
            sysMessageMapper.updateById(message);
        }
        return Result.success();
    }
}
