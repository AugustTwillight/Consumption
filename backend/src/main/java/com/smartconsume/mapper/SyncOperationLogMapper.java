package com.smartconsume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartconsume.entity.SyncOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 同步操作日志Mapper接口
 */
@Mapper
public interface SyncOperationLogMapper extends BaseMapper<SyncOperationLog> {
}
