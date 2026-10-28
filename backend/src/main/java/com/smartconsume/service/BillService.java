package com.smartconsume.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartconsume.common.constant.Constants;
import com.smartconsume.entity.ConsumeBill;
import com.smartconsume.mapper.ConsumeBillMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 消费账单服务类
 */
@Slf4j
@Service
public class BillService {

    private final ConsumeBillMapper consumeBillMapper;

    public BillService(ConsumeBillMapper consumeBillMapper) {
        this.consumeBillMapper = consumeBillMapper;
    }

    /**
     * 新增账单
     *
     * @param bill 账单信息
     * @return 账单ID
     */
    public Long createBill(ConsumeBill bill) {
        consumeBillMapper.insert(bill);
        return bill.getId();
    }

    /**
     * 查询账单列表
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param tag 标签筛选（可选）
     * @return 账单列表
     */
    public List<ConsumeBill> getBillList(Long tenantId, Long userId, String tag) {
        LambdaQueryWrapper<ConsumeBill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsumeBill::getTenantId, tenantId)
                .eq(ConsumeBill::getUserId, userId)
                .eq(ConsumeBill::getDelFlag, Constants.DEL_FLAG_NOT_DELETED);

        if (tag != null && !tag.isEmpty()) {
            queryWrapper.like(ConsumeBill::getTags, tag);
        }

        queryWrapper.orderByDesc(ConsumeBill::getCreateTime);
        return consumeBillMapper.selectList(queryWrapper);
    }

    /**
     * 删除账单
     *
     * @param billId 账单ID
     * @param tenantId 租户ID
     * @param userId 用户ID
     */
    public void deleteBill(Long billId, Long tenantId, Long userId) {
        ConsumeBill bill = consumeBillMapper.selectById(billId);
        if (bill != null && bill.getTenantId().equals(tenantId) && bill.getUserId().equals(userId)) {
            bill.setDelFlag(1);
            consumeBillMapper.updateById(bill);
        }
    }

    /**
     * 获取月度复盘报表
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param year 年份
     * @param month 月份
     * @return 月度报表
     */
    public MonthlyReport getMonthlyReport(Long tenantId, Long userId, int year, int month) {
        // 计算该月的开始和结束时间
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDateTime startDate = firstDay.atStartOfDay();
        LocalDateTime endDate = lastDay.atTime(LocalTime.MAX);

        // 统计消费总额和账单数量
        BigDecimal totalAmount = consumeBillMapper.sumMonthlyAmount(tenantId, userId, startDate, endDate);
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        int billCount = consumeBillMapper.countMonthlyBills(tenantId, userId, startDate, endDate);

        // 计算平均每日摊薄成本（如果有预期使用周期）
        BigDecimal avgDailyCost = BigDecimal.ZERO;
        if (billCount > 0) {
            // 简单计算：总支付金额 / 总预期使用天数
            // 这里简化处理，实际应该从账单中计算
        }

        MonthlyReport report = new MonthlyReport();
        report.setYear(year);
        report.setMonth(month);
        report.setTotalAmount(totalAmount);
        report.setBillCount(billCount);
        report.setAvgDailyCost(avgDailyCost);

        return report;
    }

    /**
     * 月度报表数据
     */
    @Data
    public static class MonthlyReport {
        private int year;
        private int month;
        private BigDecimal totalAmount;
        private int billCount;
        private BigDecimal avgDailyCost;
    }
}
