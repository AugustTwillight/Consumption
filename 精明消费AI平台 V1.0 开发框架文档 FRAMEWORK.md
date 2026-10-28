# 精明消费AI平台 V1.0 开发框架文档 FRAMEWORK
作用：给AI固定依赖版本、脚手架结构、配置模板、第三方组件清单，防止AI随意更换技术栈

## 1. 后端框架信息
基础框架：SpringBoot 3.2.x
JDK：21
构建工具：Maven
ORM：MyBatis-Plus 3.5.5
数据库：MySQL 8.0
缓存：Redis 7.2
安全：jjwt（JWT）、BCrypt密码加密
WebSocket：Spring WebSocket
定时任务：Spring自带Scheduled
工具包：hutool-all
依赖清单pom.xml核心依赖：
spring-boot-starter-web
spring-boot-starter-websocket
mybatis-plus-boot-starter
mysql-connector-j
spring-boot-starter-data-redis
jjwt-api, jjwt-impl, jjwt-jackson
hutool-all
lombok

### 后端application.yml模板
```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/smart_consume?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root
    password: Smart@2026
  redis:
    host: redis
    port: 6379
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  global-config:
    db-config:
      logic-delete-field: delFlag
      logic-delete-value: 1
      logic-not-delete-value: 0
jwt:
  secret: QWxhZGRpbjpvcGVuIHNlc2FtZQ==
  expire: 86400000
license:
  check-cron: 0 0 */12 * * ?
2. 前端框架信息
 
框架：Vue3 + Vite
UI库：Element Plus
状态管理：Pinia
网络请求：Axios
路由：Vue Router 4
构建目标：静态dist，部署到Nginx
目录规范：
src/
├── api/        所有接口请求封装
├── components/ 公共组件
├── views/      页面
├── store/      pinia
├── router/     路由+权限守卫
├── utils/      jwt、格式化、同步工具
├── assets/     静态资源
 
3. Chrome插件框架
 
Manifest版本：V3
原生JS，不引入Vue/React，轻量化，减少插件体积
文件固定清单：manifest.json, popup.html, popup.js, content.js, background.js
 
4. 部署框架
 
容器：Docker + Docker Compose
镜像版本锁定：mysql:8.0.36 redis:7.2-alpine nginx:alpine openjdk:21-jdk-slim
网络：bridge独立网络smart-net
反向代理：Nginx统一转发前端静态、/api后端、/ws websocket
 
5. 外部依赖说明
 
本项目不依赖外部LLM大模型接口，凑单是本地组合算法，私有化离线可用；
不搭建爬虫服务，商品数据来源用户浏览器插件DOM解析上报；
消息推送只使用WebSocket站内推送，可选扩展邮件/短信（本次版本不做）。
 
6. 扩展预留点（AI不要实现，预留）
 
1. 新增电商平台解析规则，只需要新增content.js分支
​
2. 新增优惠类型，计算器模块增加RuleHandler实现类
​
3. 后续可拆分为微服务，当前版本单体优先
​
4. 同步模块可新增同步表，只需要在白名单配置增加表名
 
7. 禁止修改的框架约束
 
❌ 禁止替换SpringBoot为其他后端框架
❌ 禁止把Manifest V3降级V2
❌ 禁止把同步推拉模型改成只拉或者只推
❌ 禁止去掉租户+License隔离模型
❌ 禁止后端主动发起HTTP爬虫访问电商站点