package com.smartconsume.common.constant;

/**
 * 常量类
 */
public class Constants {

    /**
     * 超级管理员租户ID
     */
    public static final Long SUPER_ADMIN_TENANT_ID = 0L;

    /**
     * 逻辑删除：未删除
     */
    public static final Integer DEL_FLAG_NOT_DELETED = 0;

    /**
     * 逻辑删除：已删除
     */
    public static final Integer DEL_FLAG_DELETED = 1;

    /**
     * 状态：启用
     */
    public static final Integer STATUS_ENABLED = 1;

    /**
     * 状态：禁用
     */
    public static final Integer STATUS_DISABLED = 0;

    /**
     * JWT黑名单前缀
     */
    public static final String JWT_BLACKLIST_PREFIX = "jwt:blacklist:";

    /**
     * 同步白名单表列表
     */
    public static final String[] SYNC_WHITE_LIST_TABLES = {
            "calc_plan",
            "calc_plan_goods",
            "calc_rule",
            "monitor_task",
            "monitor_price_record",
            "consume_bill",
            "community_post",
            "community_comment",
            "user_point_log",
            "sys_message"
    };

    /**
     * 最大凑单商品数量
     */
    public static final int MAX_CALC_GOODS_COUNT = 15;

}
