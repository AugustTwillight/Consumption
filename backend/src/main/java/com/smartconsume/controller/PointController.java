package com.smartconsume.controller;

import com.smartconsume.common.result.Result;
import com.smartconsume.dto.PointAddDTO;
import com.smartconsume.entity.UserPointLog;
import com.smartconsume.service.PointService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/point")
@Validated
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @PostMapping("/add")
    public Result<Void> addPoint(@Valid @RequestBody PointAddDTO dto, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        pointService.addPoint(tenantId, userId, dto.getPointChange(), dto.getPointType(), dto.getSourceType(), dto.getSourceId(), dto.getRemark());
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
