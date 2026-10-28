package com.smartconsume.controller;

import com.smartconsume.common.result.Result;
import com.smartconsume.dto.BillCreateDTO;
import com.smartconsume.entity.ConsumeBill;
import com.smartconsume.service.BillService;
import com.smartconsume.service.BillService.MonthlyReport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消费账单控制器
 */
@RestController
@RequestMapping("/api/v1/bill")
@Validated
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    /**
     * 新增账单
     *
     * @param dto 请求参数
     * @param httpRequest HTTP请求
     * @return 账单ID
     */
    @PostMapping("/create")
    public Result<Long> createBill(@Valid @RequestBody BillCreateDTO dto, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        ConsumeBill bill = new ConsumeBill();
        bill.setTenantId(tenantId);
        bill.setUserId(userId);
        bill.setBillName(dto.getBillName());
        bill.setPlanId(dto.getPlanId());
        bill.setActualAmount(dto.getActualAmount());
        bill.setExpectedCycle(dto.getExpectedCycle());
        bill.setTags(dto.getTags());
        bill.setRemarks(dto.getRemarks());

        billService.createBill(bill);
        return Result.success(bill.getId());
    }

    /**
     * 查询账单列表
     *
     * @param tag 标签筛选（可选）
     * @param httpRequest HTTP请求
     * @return 账单列表
     */
    @GetMapping("/list")
    public Result<List<ConsumeBill>> getBillList(@RequestParam(required = false) String tag, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        List<ConsumeBill> bills = billService.getBillList(tenantId, userId, tag);
        return Result.success(bills);
    }

    /**
     * 删除账单
     *
     * @param billId 账单ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/delete/{billId}")
    public Result<Void> deleteBill(@PathVariable Long billId, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        billService.deleteBill(billId, tenantId, userId);
        return Result.success();
    }

    /**
     * 获取月度复盘报表
     *
     * @param year 年份
     * @param month 月份
     * @param httpRequest HTTP请求
     * @return 月度报表
     */
    @GetMapping("/monthly-report")
    public Result<MonthlyReport> getMonthlyReport(@RequestParam int year, @RequestParam int month, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        MonthlyReport report = billService.getMonthlyReport(tenantId, userId, year, month);
        return Result.success(report);
    }
}
