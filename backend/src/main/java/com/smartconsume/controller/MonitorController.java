package com.smartconsume.controller;

import com.smartconsume.common.result.Result;
import com.smartconsume.entity.MonitorPriceRecord;
import com.smartconsume.entity.MonitorTask;
import com.smartconsume.service.MonitorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 价格监控控制器
 */
@RestController
@RequestMapping("/api/v1/monitor")
public class MonitorController {

    private final MonitorService monitorService;

    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    /**
     * 添加或更新监控任务
     *
     * @param request 请求参数 {goodsName, goodsLink, platform, currentPrice, targetPrice}
     * @param httpRequest HTTP请求
     * @return 任务ID
     */
    @PostMapping("/task")
    public Result<Long> saveTask(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        MonitorTask task = new MonitorTask();
        task.setTenantId(tenantId);
        task.setUserId(userId);
        task.setGoodsName((String) request.get("goodsName"));
        task.setGoodsLink((String) request.get("goodsLink"));
        task.setPlatform((String) request.get("platform"));
        task.setCurrentPrice(new BigDecimal(request.get("currentPrice").toString()));
        task.setTargetPrice(new BigDecimal(request.get("targetPrice").toString()));
        task.setStatus(1);

        monitorService.saveOrUpdateTask(task);
        return Result.success(task.getId());
    }

    /**
     * 查询监控任务列表
     *
     * @param httpRequest HTTP请求
     * @return 任务列表
     */
    @GetMapping("/task-list")
    public Result<List<MonitorTask>> getTaskList(HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        List<MonitorTask> tasks = monitorService.getTaskList(tenantId, userId);
        return Result.success(tasks);
    }

    /**
     * 更新商品价格并记录
     *
     * @param request 请求参数 {taskId, price}
     * @return 是否触发告警
     */
    @PostMapping("/update-price")
    public Result<Boolean> updatePrice(@RequestBody Map<String, Object> request) {
        Long taskId = ((Number) request.get("taskId")).longValue();
        BigDecimal price = new BigDecimal(request.get("price").toString());

        boolean alertTriggered = monitorService.updatePriceAndCheckAlert(taskId, price);
        return Result.success(alertTriggered);
    }

    /**
     * 删除监控任务
     *
     * @param taskId 任务ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/task-delete/{taskId}")
    public Result<Void> deleteTask(@PathVariable Long taskId, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        monitorService.deleteTask(taskId, tenantId, userId);
        return Result.success();
    }

    /**
     * 启用/禁用监控任务
     *
     * @param taskId 任务ID
     * @param status 状态：0禁用，1启用
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PutMapping("/task-toggle/{taskId}/{status}")
    public Result<Void> toggleTaskStatus(@PathVariable Long taskId, @PathVariable Integer status, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        monitorService.toggleTaskStatus(taskId, status, tenantId, userId);
        return Result.success();
    }

    /**
     * 获取价格历史记录
     *
     * @param taskId 任务ID
     * @return 价格记录列表
     */
    @GetMapping("/price-records/{taskId}")
    public Result<List<MonitorPriceRecord>> getPriceRecords(@PathVariable Long taskId) {
        List<MonitorPriceRecord> records = monitorService.getPriceRecords(taskId);
        return Result.success(records);
    }
}
