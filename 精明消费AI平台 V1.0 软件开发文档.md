# 精明消费AI平台 V1.0 软件开发文档（开发指引）
面向AI开发执行使用，规定编码规范、开发顺序、任务拆分、自测清单、交付验收标准

## 1. 开发顺序（AI必须按顺序做，避免依赖混乱）
阶段1：数据库初始化脚本 init.sql 编写，建表、索引、基础数据
阶段2：后端基础脚手架搭建：统一返回、全局异常、JWT工具、Redis封装、MyBatis-Plus
阶段3：认证模块开发（登录、登出、获取用户信息、拦截器）
阶段4：租户+License模块、权限隔离逻辑
阶段5：核心凑单计算器算法 + 相关CRUD接口
阶段6：价格监控模块、价格记录、告警消息逻辑
阶段7：消费账单、月度复盘报表接口
阶段8：社区帖子评论点赞收藏、审核接口
阶段9：增量同步模块（pull/push、冲突检测、水位表）
阶段10：站内消息、WebSocket实时推送服务
阶段11：后端全部接口自测，对齐OpenAPI文档
阶段12：Vue3前端页面开发，路由、权限、页面组件
阶段13：前端同步逻辑、离线缓存、冲突弹窗处理
阶段14：Chrome扩展插件开发、页面抓取逻辑、通知逻辑
阶段15：Docker打包、docker-compose.yml、Nginx配置、部署测试
阶段16：全流程联调、回归测试、输出全部交付文件

## 2. 编码规范
### 后端 Java
1. 使用SpringBoot 3.x + JDK21 + MyBatis-Plus
2. 所有数据库实体继承基础BaseEntity：id、tenant_id、create_time、update_time、del_flag
3. 禁止硬编码常量，统一放在Constants类
4. 业务校验在Service层抛出自定义异常，全局异常处理器捕获包装为统一返回
5. 定时任务使用Spring @Scheduled，License校验任务
6. 不使用复杂分布式事务，本项目单体，业务最终一致性即可
### 前端 Vue3
1. <script setup>语法；Pinia管理全局用户、token状态
2. 接口统一封装axios请求，拦截401自动跳转登录
3. 分页组件统一封装；表格支持筛选
4. 同步冲突弹窗：两个选项【以本地覆盖云端 / 以云端覆盖本地】
### 插件
1. 严格区分content.js（页面读取）和background.js（后台任务）
2. 禁止在content脚本写业务提交逻辑，数据通过popup转发后端

## 3. 接口开发约束
严格按照【05-OpenAPI-接口文档.md】实现，入参、出参字段名大小写、结构不能随意修改。

## 4. 自测清单（每模块完成必须自测）
✅ 登录、token过期、退出登录
✅ 租户隔离：A租户用户看不到B租户任何数据
✅ License过期后租户用户无法登录
✅ 凑单：多商品、多券叠加，计算金额正确，风险标签输出
✅ 监控上报价格、降价触发消息、websocket推送
✅ 账单新增、月度报表统计正确
✅ 发帖、评论、审核、点赞收藏
✅ 多设备同步：新增、修改、删除；冲突场景能识别并弹窗
✅ 消息已读、全部已读
✅ Chrome插件淘宝/京东/pdd抓取商品、添加监控
✅ docker compose up 一键启动整套服务，无报错
✅ 前端路由权限：普通用户看不到租户/超管页面

## 5. 交付包目录结构（最终交付）
smart-consume-package/
├── backend/
│   ├── Dockerfile
│   └── target/smart-consume.jar
├── frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── dist/
├── chrome-extension/
│   ├── manifest.json
│   ├── popup.html popup.js content.js background.js icon
├── sql/init.sql
├── docker-compose.yml
├── 文档文件夹/
│   ├── SRS规格.md
│   ├── SDD设计.md
│   ├── DEV开发指南.md
│   ├── FRAMEWORK框架.md
│   ├── OpenAPI接口.md

## 6. 验收标准
1. 全部接口和OpenAPI文档一致，请求返回结构匹配
2. 租户数据完全隔离
3. 凑单数学计算结果准确，无计算溢出
4. 同步推拉、冲突检测功能稳定
5. 插件可正常抓取三大平台商品
6. Docker一键部署成功，访问Web页面正常，WebSocket推送正常
7. 所有文档和代码配套，注释清晰