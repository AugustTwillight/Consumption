package com.smartconsume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartconsume.entity.MonitorPriceRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 历史价格流水Mapper接口
 */
@Mapper
public interface MonitorPriceRecordMapper extends BaseMapper<MonitorPriceRecord> {
}
