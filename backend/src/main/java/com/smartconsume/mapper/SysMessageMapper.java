package com.smartconsume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartconsume.entity.SysMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站内消息Mapper接口
 */
@Mapper
public interface SysMessageMapper extends BaseMapper<SysMessage> {
}
