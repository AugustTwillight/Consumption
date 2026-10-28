package com.smartconsume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartconsume.entity.ConsumeBill;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 消费账单Mapper接口
 */
@Mapper
public interface ConsumeBillMapper extends BaseMapper<ConsumeBill> {

    /**
     * 统计月度消费总额
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 消费总额
     */
    BigDecimal sumMonthlyAmount(Long tenantId, Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 统计月度账单数量
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 账单数量
     */
    int countMonthlyBills(Long tenantId, Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
