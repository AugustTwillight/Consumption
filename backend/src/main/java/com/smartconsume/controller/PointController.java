package com.smartconsume.controller;

import com.smartconsume.common.result.Result;
import com.smartconsume.entity.UserPointLog;
import com.smartconsume.service.PointService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/point")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @PostMapping("/add")
    public Result<Void> addPoint(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        Integer pointChange = ((Number) request.get("pointChange")).intValue();
        String pointType = (String) request.get("pointType");
        String sourceType = (String) request.get("sourceType");
        Long sourceId = request.get("sourceId") != null ? ((Number) request.get("sourceId")).longValue() : null;
        String remark = (String) request.get("remark");

        pointService.addPoint(tenantId, userId, pointChange, pointType, sourceType, sourceId, remark);
        return Result.success();
    }

    @GetMapping("/log-list")
    public Result<List<UserPointLog>> getPointLogList(HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        List<UserPointLog> list = pointService.getPointLogList(tenantId, userId);
        return Result.success(list);
    }
}
