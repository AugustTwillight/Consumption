package com.smartconsume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartconsume.entity.SyncWaterMark;
import org.apache.ibatis.annotations.Mapper;

/**
 * 同步水位表Mapper接口
 */
@Mapper
public interface SyncWaterMarkMapper extends BaseMapper<SyncWaterMark> {
}
