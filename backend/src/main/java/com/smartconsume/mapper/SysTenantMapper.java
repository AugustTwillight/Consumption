package com.smartconsume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartconsume.entity.SysTenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户Mapper接口
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenant> {
}
