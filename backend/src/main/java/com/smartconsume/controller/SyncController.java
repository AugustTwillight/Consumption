package com.smartconsume.controller;

import com.smartconsume.common.result.Result;
import com.smartconsume.service.SyncService;
import com.smartconsume.service.SyncService.SyncPullResult;
import com.smartconsume.service.SyncService.SyncPushResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 增量同步控制器
 */
@RestController
@RequestMapping("/api/v1/sync")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    /**
     * Pull数据
     *
     * @param tableName 表名
     * @param lastWaterTime 上次水位时间
     * @param httpRequest HTTP请求
     * @return 同步数据结果
     */
    @GetMapping("/pull")
    public Result<SyncPullResult> pullData(
            @RequestParam String tableName,
            @RequestParam LocalDateTime lastWaterTime,
            HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        SyncPullResult result = syncService.pullData(tableName, tenantId, userId, lastWaterTime);
        return Result.success(result);
    }

    /**
     * Push数据
     *
     * @param request 请求参数 {tableName, localVersion, clientData}
     * @param httpRequest HTTP请求
     * @return 同步结果
     */
    @PostMapping("/push")
    public Result<SyncPushResult> pushData(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        String tableName = (String) request.get("tableName");
        Long localVersion = ((Number) request.get("localVersion")).longValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> clientData = (Map<String, Object>) request.get("clientData");

        SyncPushResult result = syncService.pushData(tableName, tenantId, userId, localVersion, clientData);
        return Result.success(result);
    }
}
