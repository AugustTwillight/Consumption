package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 同步操作日志实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sync_operation_log")
public class SyncOperationLog extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 同步类型：pull/push
     */
    private String syncType;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 是否冲突：0无冲突，1有冲突
     */
    private Integer conflictFlag;
}
