package com.smartconsume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartconsume.entity.MonitorTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 监控任务Mapper接口
 */
@Mapper
public interface MonitorTaskMapper extends BaseMapper<MonitorTask> {
}
