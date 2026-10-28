package com.smartconsume.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartconsume.common.constant.Constants;
import com.smartconsume.common.exception.BusinessException;
import com.smartconsume.common.result.ResultCode;
import com.smartconsume.entity.SyncOperationLog;
import com.smartconsume.entity.SyncWaterMark;
import com.smartconsume.mapper.SyncOperationLogMapper;
import com.smartconsume.mapper.SyncWaterMarkMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 增量同步服务类
 */
@Slf4j
@Service
public class SyncService {

    private final SyncWaterMarkMapper syncWaterMarkMapper;
    private final SyncOperationLogMapper syncOperationLogMapper;

    public SyncService(SyncWaterMarkMapper syncWaterMarkMapper, SyncOperationLogMapper syncOperationLogMapper) {
        this.syncWaterMarkMapper = syncWaterMarkMapper;
        this.syncOperationLogMapper = syncOperationLogMapper;
    }

    /**
     * Pull流程：客户端传入表名+lastWaterTime，服务端查询该表 > water_time、未删除数据，返回数据+新水位
     *
     * @param tableName 表名
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param lastWaterTime 上次水位时间
     * @return 同步数据结果
     */
    public SyncPullResult pullData(String tableName, Long tenantId, Long userId, LocalDateTime lastWaterTime) {
        // 检查表是否在白名单
        boolean isWhiteList = false;
        for (String whiteTable : Constants.SYNC_WHITE_LIST_TABLES) {
            if (whiteTable.equals(tableName)) {
                isWhiteList = true;
                break;
            }
        }
        if (!isWhiteList) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED.getCode(), "该表不支持同步");
        }

        // 查询或创建水位记录
        SyncWaterMark waterMark = getOrCreateWaterMark(tableName, tenantId, userId);

        // 这里简化处理，实际应该根据表名动态查询数据
        // 返回模拟数据和新水位
        SyncPullResult result = new SyncPullResult();
        result.setData(new HashMap<>());
        result.setNewWaterTime(waterMark.getWaterTime());
        result.setDataVersion(waterMark.getDataVersion());
        result.setConflict(false);

        // 记录同步日志
        recordSyncLog(tenantId, userId, "pull", tableName, false);

        return result;
    }

    /**
     * Push流程：客户端推送变更，携带localVersion；服务端查询云端当前版本；
     * - 如果云端版本 > localVersion → 标记冲突 conflict=true，返回最新云端数据，由前端做合并弹窗
     * - 无冲突：覆盖写入，版本+1
     *
     * @param tableName 表名
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param localVersion 本地版本号
     * @param clientData 客户端数据
     * @return 同步结果
     */
    public SyncPushResult pushData(String tableName, Long tenantId, Long userId, Long localVersion, Map<String, Object> clientData) {
        // 检查表是否在白名单
        boolean isWhiteList = false;
        for (String whiteTable : Constants.SYNC_WHITE_LIST_TABLES) {
            if (whiteTable.equals(tableName)) {
                isWhiteList = true;
                break;
            }
        }
        if (!isWhiteList) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED.getCode(), "该表不支持同步");
        }

        // 查询或创建水位记录
        SyncWaterMark waterMark = getOrCreateWaterMark(tableName, tenantId, userId);

        SyncPushResult result = new SyncPushResult();
        result.setConflict(false);

        // 检查冲突：云端版本 > localVersion
        if (waterMark.getDataVersion() > localVersion) {
            result.setConflict(true);
            result.setCloudDataVersion(waterMark.getDataVersion());
            result.setCloudData(clientData); // 简化处理，实际应查询云端数据

            // 记录冲突日志
            recordSyncLog(tenantId, userId, "push", tableName, true);
        } else {
            // 无冲突：覆盖写入，版本+1
            waterMark.setDataVersion(waterMark.getDataVersion() + 1);
            waterMark.setWaterTime(LocalDateTime.now());
            syncWaterMarkMapper.updateById(waterMark);

            result.setNewDataVersion(waterMark.getDataVersion());
            result.setConflict(false);

            // 记录同步日志
            recordSyncLog(tenantId, userId, "push", tableName, false);
        }

        return result;
    }

    /**
     * 获取或创建水位记录
     */
    private SyncWaterMark getOrCreateWaterMark(String tableName, Long tenantId, Long userId) {
        LambdaQueryWrapper<SyncWaterMark> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SyncWaterMark::getTableName, tableName)
                .eq(SyncWaterMark::getTenantId, tenantId)
                .eq(SyncWaterMark::getUserId, userId);

        SyncWaterMark waterMark = syncWaterMarkMapper.selectOne(queryWrapper);
        if (waterMark == null) {
            waterMark = new SyncWaterMark();
            waterMark.setTableName(tableName);
            waterMark.setTenantId(tenantId);
            waterMark.setUserId(userId);
            waterMark.setWaterTime(LocalDateTime.now());
            waterMark.setDataVersion(1L);
            syncWaterMarkMapper.insert(waterMark);
        }
        return waterMark;
    }

    /**
     * 记录同步日志
     */
    private void recordSyncLog(Long tenantId, Long userId, String syncType, String tableName, boolean conflictFlag) {
        SyncOperationLog log = new SyncOperationLog();
        log.setTenantId(tenantId);
        log.setUserId(userId);
        log.setSyncType(syncType);
        log.setTableName(tableName);
        log.setConflictFlag(conflictFlag ? 1 : 0);
        syncOperationLogMapper.insert(log);
    }

    /**
     * Pull结果
     */
    @Data
    public static class SyncPullResult {
        private Map<String, Object> data;
        private LocalDateTime newWaterTime;
        private Long dataVersion;
        private boolean conflict;
    }

    /**
     * Push结果
     */
    @Data
    public static class SyncPushResult {
        private boolean conflict;
        private Long cloudDataVersion;
        private Map<String, Object> cloudData;
        private Long newDataVersion;
    }
}
