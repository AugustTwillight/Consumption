package com.smartconsume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smartconsume.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 同步水位表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sync_water_mark")
public class SyncWaterMark extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 用户ID，0为全局
     */
    private Long userId;

    /**
     * 水位时间戳
     */
    private LocalDateTime waterTime;

    /**
     * 数据版本号
     */
    private Long dataVersion;
}
