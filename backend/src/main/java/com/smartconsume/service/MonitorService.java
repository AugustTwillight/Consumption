package com.smartconsume.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartconsume.common.constant.Constants;
import com.smartconsume.entity.MonitorPriceRecord;
import com.smartconsume.entity.MonitorTask;
import com.smartconsume.entity.SysMessage;
import com.smartconsume.mapper.MonitorPriceRecordMapper;
import com.smartconsume.mapper.MonitorTaskMapper;
import com.smartconsume.mapper.SysMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 价格监控服务类
 */
@Slf4j
@Service
public class MonitorService {

    private final MonitorTaskMapper monitorTaskMapper;
    private final MonitorPriceRecordMapper monitorPriceRecordMapper;
    private final SysMessageMapper sysMessageMapper;

    public MonitorService(MonitorTaskMapper monitorTaskMapper, MonitorPriceRecordMapper monitorPriceRecordMapper, SysMessageMapper sysMessageMapper) {
        this.monitorTaskMapper = monitorTaskMapper;
        this.monitorPriceRecordMapper = monitorPriceRecordMapper;
        this.sysMessageMapper = sysMessageMapper;
    }

    /**
     * 添加或更新监控任务
     *
     * @param task 监控任务
     * @return 任务ID
     */
    public Long saveOrUpdateTask(MonitorTask task) {
        if (task.getId() != null) {
            monitorTaskMapper.updateById(task);
        } else {
            monitorTaskMapper.insert(task);
        }
        return task.getId();
    }

    /**
     * 查询用户监控任务列表
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @return 任务列表
     */
    public List<MonitorTask> getTaskList(Long tenantId, Long userId) {
        LambdaQueryWrapper<MonitorTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MonitorTask::getTenantId, tenantId)
                .eq(MonitorTask::getUserId, userId)
                .eq(MonitorTask::getDelFlag, Constants.DEL_FLAG_NOT_DELETED)
                .orderByDesc(MonitorTask::getCreateTime);
        return monitorTaskMapper.selectList(queryWrapper);
    }

    /**
     * 更新商品价格并记录历史价格
     *
     * @param taskId 任务ID
     * @param price 新价格
     * @return 是否触发告警
     */
    public boolean updatePriceAndCheckAlert(Long taskId, BigDecimal price) {
        MonitorTask task = monitorTaskMapper.selectById(taskId);
        if (task == null || task.getDelFlag() != Constants.DEL_FLAG_NOT_DELETED) {
            return false;
        }

        // 记录历史价格
        MonitorPriceRecord record = new MonitorPriceRecord();
        record.setTaskId(taskId);
        record.setPrice(price);
        record.setRecordTime(LocalDateTime.now());
        monitorPriceRecordMapper.insert(record);

        // 更新当前价格
        task.setCurrentPrice(price);
        monitorTaskMapper.updateById(task);

        // 检查是否触发告警
        boolean alertTriggered = false;
        if (task.getStatus() != null && task.getStatus() == 1 && price.compareTo(task.getTargetPrice()) <= 0) {
            // 触发告警
            alertTriggered = true;
            createPriceAlertMessage(task, price);
        }

        return alertTriggered;
    }

    /**
     * 创建价格告警消息
     *
     * @param task 监控任务
     * @param currentPrice 当前价格
     */
    private void createPriceAlertMessage(MonitorTask task, BigDecimal currentPrice) {
        SysMessage message = new SysMessage();
        message.setTenantId(task.getTenantId());
        message.setUserId(task.getUserId());
        message.setMessageType("price_alert");
        message.setTitle("价格监控告警");
        message.setContent(String.format("商品【%s】价格已降至¥%s，低于您的目标价¥%s！",
                task.getGoodsName(), currentPrice, task.getTargetPrice()));
        message.setIsRead(0);
        sysMessageMapper.insert(message);

        log.info("触发价格告警消息: 任务ID={}, 商品={}, 当前价格={}, 目标价格={}",
                task.getId(), task.getGoodsName(), currentPrice, task.getTargetPrice());
    }

    /**
     * 删除监控任务
     *
     * @param taskId 任务ID
     * @param tenantId 租户ID
     * @param userId 用户ID
     */
    public void deleteTask(Long taskId, Long tenantId, Long userId) {
        MonitorTask task = monitorTaskMapper.selectById(taskId);
        if (task != null && task.getTenantId().equals(tenantId) && task.getUserId().equals(userId)) {
            task.setDelFlag(1);
            monitorTaskMapper.updateById(task);
        }
    }

    /**
     * 启用/禁用监控任务
     *
     * @param taskId 任务ID
     * @param status 状态：0禁用，1启用
     * @param tenantId 租户ID
     * @param userId 用户ID
     */
    public void toggleTaskStatus(Long taskId, Integer status, Long tenantId, Long userId) {
        MonitorTask task = monitorTaskMapper.selectById(taskId);
        if (task != null && task.getTenantId().equals(tenantId) && task.getUserId().equals(userId)) {
            task.setStatus(status);
            monitorTaskMapper.updateById(task);
        }
    }

    /**
     * 获取价格历史记录
     *
     * @param taskId 任务ID
     * @return 价格记录列表
     */
    public List<MonitorPriceRecord> getPriceRecords(Long taskId) {
        LambdaQueryWrapper<MonitorPriceRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MonitorPriceRecord::getTaskId, taskId)
                .orderByDesc(MonitorPriceRecord::getRecordTime);
        return monitorPriceRecordMapper.selectList(queryWrapper);
    }
}
