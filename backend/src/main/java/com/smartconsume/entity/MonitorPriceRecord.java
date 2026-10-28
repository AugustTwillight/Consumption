package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 历史价格流水实体类
 */
@Data
@TableName("monitor_price_record")
public class MonitorPriceRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 监控任务ID
     */
    private Long taskId;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 记录时间
     */
    private LocalDateTime recordTime;
}
