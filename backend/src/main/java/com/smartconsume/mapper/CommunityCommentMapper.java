package com.smartconsume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartconsume.entity.CommunityComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区评论Mapper接口
 */
@Mapper
public interface CommunityCommentMapper extends BaseMapper<CommunityComment> {
}
