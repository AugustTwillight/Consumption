# 精明消费AI平台 V1.0

## 项目简介
精明消费AI平台是一款面向个人用户及私有化B端租户的一体化消费省钱平台。核心功能包括：电商商品抓取（Chrome插件）、AI凑单优惠计算、价格监控与告警、消费记账复盘、社区分享捡漏、跨端数据增量同步、用户成长与积分体系。

## 核心功能
- **AI凑单计算器**：录入商品列表与优惠规则（满减、品类券、跨店津贴等），AI枚举可行组合，输出最优凑单方案、分摊单价、总节省金额及风险标签。
- **价格监控**：通过Chrome插件一键添加商品监控，记录历史价格并绘制价格曲线，价格低于目标价时触发站内消息与浏览器通知。
- **消费记账与复盘**：新增账单、关联凑单方案，录入预期使用周期与标签，支持月度消费复盘报表统计。
- **社区分享**：用户发布捡漏帖子，支持图文、关联商品，具备点赞、收藏、评论及租户管理员帖子审核功能。
- **跨端增量同步**：按表名+水位时间增量拉取云端变更数据，本地变更推送到云端并携带版本号，服务端检测冲突并交由前端弹窗合并。
- **用户成长与积分**：积分流水记录，等级权益展示（Lv.1/Lv.2/Lv.3），操作发放积分。
- **Chrome插件**：在淘宝/京东/拼多多页面一键抓取商品信息，一键加入监控、一键跳转平台凑单页面，接收降价浏览器通知。

## 技术栈
- **后端服务**: Java 21, SpringBoot 3.2.x, MyBatis-Plus 3.5.5, MySQL 8.0, Redis 7, WebSocket, JWT, Hutool
- **前端页面**: Vue3, Vite, Element Plus, Pinia, Vue Router 4, Axios, SockJS, @stomp/stompjs
- **Chrome插件**: Manifest V3, 原生JS (Content Script + Background Service Worker + Popup)
- **部署方式**: Docker Compose 一键部署 (MySQL + Redis + Backend + Frontend/Nginx)

## 项目目录结构
```
精明时代/
├── backend/                  # 后端SpringBoot项目
│   ├── src/main/java/com/smartconsume/
│   │   ├── controller/       # 控制器层
│   │   ├── service/          # 服务层
│   │   ├── mapper/           # 数据访问层
│   │   ├── entity/           # 实体类
│   │   ├── common/           # 公共组件（结果返回、异常处理、JWT、Redis、WebSocket等）
│   │   └── SmartConsumeApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml   # 配置文件
│   │   └── mapper/           # MyBatis XML映射文件
│   ├── Dockerfile            # 后端Docker镜像配置
│   └── pom.xml               # Maven依赖配置
├── frontend/                 # 前端Vue3项目
│   ├── src/
│   │   ├── api/              # API接口封装
│   │   ├── components/       # 公共组件
│   │   ├── router/           # 路由配置
│   │   ├── store/            # Pinia状态管理
│   │   ├── utils/            # 工具类（request、websocket等）
│   │   ├── views/            # 页面组件
│   │   ├── App.vue           # 根组件
│   │   └── main.js           # 入口文件
│   ├── package.json          # 前端依赖配置
│   ├── vite.config.js        # Vite配置
│   ├── Dockerfile            # 前端Docker镜像配置
│   └── nginx.conf            # Nginx反向代理配置
├── chrome-extension/         # Chrome浏览器插件源码
│   ├── manifest.json         # 插件清单文件 (Manifest V3)
│   ├── popup.html/js/css     # 插件弹窗页面
│   ├── content.js            # 页面抓取脚本
│   └── background.js         # 后台服务脚本
├── sql/
│   └── init.sql              # 数据库初始化脚本（15张核心表）
├── docker-compose.yml        # Docker Compose编排文件
├── 05-OpenAPI-接口文档.md    # 后端OpenAPI接口文档
├── 精明消费AI平台 V1.0 软件规格说明书 SRS.md
├── 精明消费AI平台 V1.0 软件设计文档 SD.md
├── 精明消费AI平台 V1.0 软件开发文档.md
└── 精明消费AI平台 V1.0 开发框架文档 FRAMEWORK.md
```

## 快速启动 (Docker方式)

确保已安装并启动 **Docker Desktop**，然后在项目根目录执行：

```bash
# 构建并启动所有服务
docker compose up -d --build

# 查看服务状态
docker compose ps

# 停止服务
docker compose down
```

服务启动后：
- **前端页面**：访问 `http://localhost`
- **后端API**：访问 `http://localhost:8080`
- **默认管理员账号**：用户名 `admin`，密码 `admin`

## 本地开发启动

### 1. 后端服务
确保本地已安装 MySQL 8.0 和 Redis 7，并执行 `sql/init.sql` 初始化数据库。
在 `backend/` 目录下使用 IDEA 或 Maven 启动 `SmartConsumeApplication.java`。

### 2. 前端服务
在 `frontend/` 目录下执行：
```bash
npm install
npm run dev
```
前端服务将运行在 `http://localhost:3000`。

### 3. Chrome插件
1. 打开 Chrome 浏览器，进入 `chrome://extensions/`
2. 开启右上角**“开发者模式”**
3. 点击**“加载已解压的扩展程序”**，选择 `chrome-extension` 目录

## 许可证
本项目为内部项目基准代码，仅供学习与参考使用。
