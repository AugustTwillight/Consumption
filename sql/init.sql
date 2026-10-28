-- 精明消费AI平台 V1.0 数据库初始化脚本
-- 数据库: MySQL 8.0

CREATE DATABASE IF NOT EXISTS `smart_consume` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `smart_consume`;

-- 1. sys_user 用户表
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID，0为超管',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码BCrypt加密',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0禁用，1启用',
  `point_balance` int NOT NULL DEFAULT '0' COMMENT '积分余额',
  `level` int NOT NULL DEFAULT '1' COMMENT '用户等级',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username_tenant` (`username`,`tenant_id`,`del_flag`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. sys_tenant 租户表
CREATE TABLE `sys_tenant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_name` varchar(100) NOT NULL COMMENT '租户名称',
  `license_key` varchar(100) DEFAULT NULL COMMENT 'License授权密钥',
  `max_user` int NOT NULL DEFAULT '100' COMMENT '最大用户数量',
  `expire_time` datetime DEFAULT NULL COMMENT '授权到期时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0冻结，1正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_license_key` (`license_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- 3. sys_license_log license校验日志
CREATE TABLE `sys_license_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `check_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '校验时间',
  `result` tinyint NOT NULL COMMENT '校验结果：0失败，1成功',
  `message` varchar(500) DEFAULT NULL COMMENT '校验消息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_check_time` (`check_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='License校验日志表';

-- 4. calc_plan 凑单方案主表
CREATE TABLE `calc_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `plan_name` varchar(100) DEFAULT NULL COMMENT '方案名称',
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '商品总金额',
  `total_discount` decimal(10,2) DEFAULT '0.00' COMMENT '总优惠金额',
  `final_amount` decimal(10,2) DEFAULT '0.00' COMMENT '实付总金额',
  `risk_tags` varchar(500) DEFAULT NULL COMMENT '风险标签，逗号分隔',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1正常，2已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_user` (`tenant_id`,`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='凑单方案主表';

-- 5. calc_plan_goods 方案内商品子表
CREATE TABLE `calc_plan_goods` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` bigint NOT NULL COMMENT '方案ID',
  `goods_name` varchar(200) NOT NULL COMMENT '商品名称',
  `goods_link` varchar(500) DEFAULT NULL COMMENT '商品链接',
  `platform` varchar(50) DEFAULT NULL COMMENT '平台：taobao/jd/pdd',
  `unit_price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='方案内商品子表';

-- 6. calc_rule 优惠规则子表
CREATE TABLE `calc_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` bigint NOT NULL COMMENT '方案ID',
  `rule_type` varchar(50) NOT NULL COMMENT '规则类型：full_reduce/category_coupon/shop_coupon/cross_store_discount',
  `rule_condition` decimal(10,2) DEFAULT '0.00' COMMENT '优惠门槛金额',
  `rule_discount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠减免金额',
  `is_stackable` tinyint NOT NULL DEFAULT '1' COMMENT '是否可叠加：0否，1是',
  `category_limit` varchar(200) DEFAULT NULL COMMENT '品类限制，逗号分隔',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠规则子表';

-- 7. monitor_task 监控任务
CREATE TABLE `monitor_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `goods_name` varchar(200) NOT NULL COMMENT '商品名称',
  `goods_link` varchar(500) DEFAULT NULL COMMENT '商品链接',
  `platform` varchar(50) DEFAULT NULL COMMENT '平台：taobao/jd/pdd',
  `current_price` decimal(10,2) NOT NULL COMMENT '当前监控价格',
  `target_price` decimal(10,2) NOT NULL COMMENT '目标提醒价',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0禁用，1启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_user` (`tenant_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='监控任务表';

-- 8. monitor_price_record 历史价格流水
CREATE TABLE `monitor_price_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NOT NULL COMMENT '监控任务ID',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `record_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_record_time` (`record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='历史价格流水表';

-- 9. consume_bill 消费账单
CREATE TABLE `consume_bill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `plan_id` bigint DEFAULT NULL COMMENT '关联凑单方案ID',
  `bill_name` varchar(100) NOT NULL COMMENT '账单名称',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实际支付金额',
  `expected_cycle` int DEFAULT NULL COMMENT '预期使用周期（天）',
  `tags` varchar(200) DEFAULT NULL COMMENT '标签，逗号分隔',
  `remarks` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_user` (`tenant_id`,`user_id`),
  KEY `idx_plan_id` (`plan_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费账单表';

-- 10. community_post 帖子
CREATE TABLE `community_post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) NOT NULL COMMENT '帖子标题',
  `content` text COMMENT '帖子内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '图片链接，逗号分隔',
  `goods_link` varchar(500) DEFAULT NULL COMMENT '关联商品链接',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态：0审核中，1已通过，2已拒绝',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `collect_count` int NOT NULL DEFAULT '0' COMMENT '收藏数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_status` (`tenant_id`,`status`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子表';

-- 11. community_comment 评论
CREATE TABLE `community_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `parent_comment_id` bigint DEFAULT '0' COMMENT '父评论ID，0为顶级评论',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_tenant_user` (`tenant_id`,`user_id`),
  KEY `idx_parent_comment_id` (`parent_comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区评论表';

-- 12. user_point_log 积分流水
CREATE TABLE `user_point_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `point_change` int NOT NULL COMMENT '积分变动值（正为获得，负为消耗）',
  `point_type` varchar(50) NOT NULL COMMENT '类型：earn/consume',
  `source_type` varchar(50) DEFAULT NULL COMMENT '来源类型：calc/monitor/post/etc',
  `source_id` bigint DEFAULT NULL COMMENT '来源ID',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_user` (`tenant_id`,`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户积分流水表';

-- 13. sys_message 站内消息
CREATE TABLE `sys_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `message_type` varchar(50) NOT NULL COMMENT '消息类型：price_alert/sync_change/comment_notice/etc',
  `title` varchar(100) NOT NULL COMMENT '消息标题',
  `content` varchar(500) NOT NULL COMMENT '消息内容',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读：0未读，1已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_user` (`tenant_id`,`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内消息表';

-- 14. sync_water_mark 同步水位表
CREATE TABLE `sync_water_mark` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `table_name` varchar(100) NOT NULL COMMENT '表名',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID',
  `user_id` bigint DEFAULT '0' COMMENT '用户ID，0为全局',
  `water_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '水位时间戳',
  `data_version` bigint NOT NULL DEFAULT '1' COMMENT '数据版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_table_tenant_user` (`table_name`,`tenant_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='同步水位表';

-- 15. sync_operation_log 同步操作日志
CREATE TABLE `sync_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `sync_type` varchar(20) NOT NULL COMMENT '同步类型：pull/push',
  `table_name` varchar(100) NOT NULL COMMENT '表名',
  `conflict_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否冲突：0无冲突，1有冲突',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_user` (`tenant_id`,`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='同步操作日志表';

-- 初始化基础数据
-- 插入默认租户（超管租户 tenant_id=0）
INSERT INTO `sys_tenant` (`id`, `tenant_name`, `license_key`, `max_user`, `expire_time`, `status`, `del_flag`) 
VALUES (0, '超级管理员租户', 'SUPER_ADMIN_LICENSE_KEY', -1, NULL, 1, 0);

-- 插入默认超级管理员用户
INSERT INTO `sys_user` (`id`, `tenant_id`, `username`, `password`, `nickname`, `status`, `point_balance`, `level`, `del_flag`) 
VALUES (1, 0, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 1, 1000, 1, 0);

-- 插入默认同步水位表初始数据
INSERT INTO `sync_water_mark` (`table_name`, `tenant_id`, `user_id`, `water_time`, `data_version`) VALUES
('calc_plan', 0, 0, '2026-01-01 00:00:00', 1),
('calc_plan_goods', 0, 0, '2026-01-01 00:00:00', 1),
('calc_rule', 0, 0, '2026-01-01 00:00:00', 1),
('monitor_task', 0, 0, '2026-01-01 00:00:00', 1),
('monitor_price_record', 0, 0, '2026-01-01 00:00:00', 1),
('consume_bill', 0, 0, '2026-01-01 00:00:00', 1),
('community_post', 0, 0, '2026-01-01 00:00:00', 1),
('community_comment', 0, 0, '2026-01-01 00:00:00', 1),
('user_point_log', 0, 0, '2026-01-01 00:00:00', 1),
('sys_message', 0, 0, '2026-01-01 00:00:00', 1);

</content>