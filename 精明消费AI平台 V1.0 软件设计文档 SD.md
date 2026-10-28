# 精明消费AI平台 V1.0 软件设计文档 SDD
版本：V1.0
目标：指导AI进行架构设计、模块划分、表设计、接口设计、算法设计、同步逻辑设计

## 1. 架构总览
整体采用前后端分离 + 插件端三端架构
三层架构：接入层 → 业务服务层 → 数据层
- 接入层：Vue前端页面 + Chrome Extension + Nginx反向代理 + WebSocket
- 业务层：SpringBoot微内核单体（私有化场景优先单体，降低部署复杂度），按包拆分模块
- 数据层：MySQL持久业务数据，Redis缓存token、限流、临时优惠计算缓存、分布式锁

分层原则：Controller → Service → Mapper，严格分层，禁止跨层直接调用。

## 2. 模块划分（包结构）
com.smartconsume
├── auth        认证、JWT、登录
├── tenant      租户、License管理
├── calculator  AI凑单计算核心算法
├── monitor     价格监控、价格历史、告警任务
├── bill        消费账单、复盘报表
├── community   帖子、评论、点赞收藏、审核
├── sync        跨端增量同步、冲突处理、水位管理
├── user        用户信息、积分、等级
├── msg         站内消息、消息推送
├── common      公共类、统一返回、异常、工具类
├── ws          WebSocket推送服务

## 3. 数据库设计概要（完整SQL单独文件）
核心表清单：
1. sys_user 用户表，tenant_id隔离
2. sys_tenant 租户表，license_key、max_user、expire_time
3. sys_license_log license校验日志
4. calc_plan 凑单方案主表
5. calc_plan_goods 方案内商品子表
6. calc_rule 优惠规则子表
7. monitor_task 监控任务
8. monitor_price_record 历史价格流水
9. consume_bill 消费账单
10. community_post 帖子
11. community_comment 评论
12. user_point_log 积分流水
13. sys_message 站内消息
14. sync_water_mark 同步水位表
15. sync_operation_log 同步操作日志

主键统一bigint自增；所有业务表带tenant_id、create_time、update_time、del_flag逻辑删除。

## 4. 核心模块详细设计
### 4.1 JWT认证设计
登录成功下发token，存入Redis黑名单用于登出失效；请求拦截器校验Authorization头；401无权限统一返回。

### 4.2 License & 租户隔离设计
所有查询强制拼接tenant_id；超管tenantId=0；定时任务每12h远程校验license，校验失败标记租户冻结，冻结后禁止新用户登录。

### 4.3 AI凑单计算器核心算法设计
输入：商品数组（单价、数量）、优惠规则数组（满减门槛、减免金额、是否可叠加、品类限制）
算法策略：
1. 过滤无效规则，按优先级排序（券>满减>津贴）
2. 回溯枚举可行组合（控制组合数量，避免爆炸，限制商品上限15件）
3. 计算每种组合实付、单品分摊价、优惠利用率
4. 打分排序：省钱金额优先，叠加风险标签
输出：top1最优方案 + 备选方案列表
> 注意：不接入大模型API，纯组合数学计算，降低私有化成本

### 4.4 价格监控&告警设计
插件上报价格变更 → 写入monitor_price_record；对比target_price；达到阈值，写入sys_message，通过WebSocket推送到在线客户端；Chrome通知由插件service worker接收推送。
轮询：后端不主动爬，价格全部来自前端插件上报。

### 4.5 增量同步模块（重点设计）
核心字段：table_name、water_time(水位时间戳)、data_version版本号
- Pull流程：客户端传入表名+lastWaterTime，服务端查询该表 > water_time、未删除数据，返回数据+新水位
- Push流程：客户端推送变更，携带localVersion；服务端查询云端当前版本；
    - 如果云端版本 > localVersion → 标记冲突 conflict=true，返回最新云端数据，由前端做合并弹窗
    - 无冲突：覆盖写入，版本+1
冲突策略：服务端只检测，**合并策略交给前端**（用户选择以本地为准 / 云端为准）
同步表白名单：账单、凑单方案、监控任务、收藏帖子；配置类、租户管理数据不开放同步。

### 4.6 WebSocket推送设计
连接时携带token鉴权；按用户id订阅通道；推送事件类型：price_alert、sync_change、comment_notice；断线自动重连由前端实现。

## 5. 前端架构设计
Vue3 + Vite + Element Plus + Pinia状态管理
页面路由划分：
/login 登录
/home 首页
/calc 凑单计算器
/monitor 价格监控列表
/bill 账单、月度复盘
/community 社区帖子
/user 用户中心、积分等级
/msg 消息中心
/tenant 租户后台（租户管理员）
/super-admin 超管后台
/personal-sync 同步管理页面
本地存储：localStorage存放token、本地缓存账单/方案，用于离线模式

## 6. Chrome插件设计
Manifest V3；content脚本注入电商页面，隔离环境，只做DOM解析提取商品；background service worker负责通知监听、推送接收；popup弹窗作为操作面板；存储token在chrome.storage.local。

## 7. 异常与错误码设计
200成功
401 token失效未登录
403 权限不足 / license冻结
404 资源不存在
409 同步数据冲突
500 服务内部异常

## 8. 安全设计
- 密码BCrypt哈希，不存明文
- 接口防重、简单限流（Redis）
- 租户行级数据隔离，禁止跨租户查询
- 插件host_permissions严格限定电商域名，不开放全网权限