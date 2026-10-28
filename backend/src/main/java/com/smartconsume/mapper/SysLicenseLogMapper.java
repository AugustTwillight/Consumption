package com.smartconsume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartconsume.entity.SysLicenseLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * License校验日志Mapper接口
 */
@Mapper
public interface SysLicenseLogMapper extends BaseMapper<SysLicenseLog> {
}
