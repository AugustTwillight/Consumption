# 精明消费AI平台 V1.0 OpenAPI 接口文档

## 1. 说明
- 基础路径：`/api/v1`
- 统一返回格式：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {}
  }
  ```
- 鉴权方式：所有接口（除登录外）需在请求头携带 `Authorization: Bearer <token>`

---

## 2. 身份认证模块

### 2.1 账号密码登录
- **接口**：`POST /api/v1/auth/login`
- **请求参数**：
  ```json
  {
    "username": "admin",
    "password": "admin123"
  }
  ```
- **返回参数**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
  }
  ```

### 2.2 手机号验证码登录
- **接口**：`POST /api/v1/auth/phone-login`
- **请求参数**：
  ```json
  {
    "phone": "13800138000",
    "code": "123456"
  }
  ```
- **返回参数**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
  }
  ```

### 2.3 登出
- **接口**：`POST /api/v1/auth/logout`
- **请求头**：`Authorization: Bearer <token>`
- **返回参数**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": null
  }
  ```

### 2.4 获取用户信息
- **接口**：`GET /api/v1/auth/info`
- **请求头**：`Authorization: Bearer <token>`
- **返回参数**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "id": 1,
      "tenantId": 0,
      "username": "admin",
      "nickname": "超级管理员",
      "status": 1,
      "pointBalance": 1000,
      "level": 1
    }
  }
  ```

---

## 3. 凑单计算器模块

### 3.1 执行凑单计算
- **接口**：`POST /api/v1/calc-plan/calculate`
- **请求参数**：
  ```json
  {
    "planName": "双十一凑单方案",
    "goodsList": [
      {
        "goodsName": "商品A",
        "goodsLink": "https://...",
        "platform": "taobao",
        "unitPrice": 99.00,
        "quantity": 1
      }
    ],
    "ruleList": [
      {
        "ruleType": "full_reduce",
        "ruleCondition": 200.00,
        "ruleDiscount": 30.00,
        "isStackable": 1,
        "categoryLimit": ""
      }
    ]
  }
  ```
- **返回参数**：包含 `totalAmount`, `totalDiscount`, `finalAmount`, `riskTags`, `scenarios` 等

### 3.2 保存凑单方案
- **接口**：`POST /api/v1/calc-plan/save`
- **请求头**：`Authorization: Bearer <token>`
- **请求参数**：同计算结果结构，包含 `planName`, `goodsList`, `ruleList`, `totalAmount`, `totalDiscount`, `finalAmount`, `riskTags`
- **返回参数**：`{ "code": 200, "data": 123 }` （方案ID）

### 3.3 查询方案列表
- **接口**：`GET /api/v1/calc-plan/list`
- **请求头**：`Authorization: Bearer <token>`
- **返回参数**：方案列表数组

### 3.4 查询方案详情
- **接口**：`GET /api/v1/calc-plan/detail/{id}`
- **请求头**：`Authorization: Bearer <token>`
- **返回参数**：包含 `plan`, `goodsList`, `ruleList`

### 3.5 删除方案
- **接口**：`DELETE /api/v1/calc-plan/delete/{id}`
- **请求头**：`Authorization: Bearer <token>`
- **返回参数**：`{ "code": 200, "msg": "操作成功" }`

---

## 4. 价格监控模块

### 4.1 添加或更新监控任务
- **接口**：`POST /api/v1/monitor/task`
- **请求头**：`Authorization: Bearer <token>`
- **请求参数**：
  ```json
  {
    "goodsName": "商品B",
    "goodsLink": "https://...",
    "platform": "jd",
    "currentPrice": 150.00,
    "targetPrice": 120.00
  }
  ```
- **返回参数**：任务ID

### 4.2 查询监控任务列表
- **接口**：`GET /api/v1/monitor/task-list`
- **请求头**：`Authorization: Bearer <token>`
- **返回参数**：任务列表数组

### 4.3 更新商品价格并记录
- **接口**：`POST /api/v1/monitor/update-price`
- **请求参数**：
  ```json
  {
    "taskId": 1,
    "price": 119.00
  }
  ```
- **返回参数**：`{ "code": 200, "data": true }` （是否触发告警）

### 4.4 删除监控任务
- **接口**：`DELETE /api/v1/monitor/task-delete/{taskId}`
- **请求头**：`Authorization: Bearer <token>`

### 4.5 启用/禁用监控任务
- **接口**：`PUT /api/v1/monitor/task-toggle/{taskId}/{status}`
- **请求头**：`Authorization: Bearer <token>`

### 4.6 获取价格历史记录
- **接口**：`GET /api/v1/monitor/price-records/{taskId}`
- **返回参数**：价格记录列表数组

---

## 5. 消费账单模块

### 5.1 新增账单
- **接口**：`POST /api/v1/bill/create`
- **请求头**：`Authorization: Bearer <token>`
- **请求参数**：
  ```json
  {
    "billName": "双十一购物账单",
    "planId": 123,
    "actualAmount": 250.00,
    "expectedCycle": 30,
    "tags": "家电,促销",
    "remarks": "凑单满减"
  }
  ```
- **返回参数**：账单ID

### 5.2 查询账单列表
- **接口**：`GET /api/v1/bill/list?tag=家电`
- **请求头**：`Authorization: Bearer <token>`
- **返回参数**：账单列表数组

### 5.3 删除账单
- **接口**：`DELETE /api/v1/bill/delete/{billId}`
- **请求头**：`Authorization: Bearer <token>`

### 5.4 获取月度复盘报表
- **接口**：`GET /api/v1/bill/monthly-report?year=2026&month=1`
- **请求头**：`Authorization: Bearer <token>`
- **返回参数**：
  ```json
  {
    "year": 2026,
    "month": 1,
    "totalAmount": 2500.00,
    "billCount": 5,
    "avgDailyCost": 8.33
  }
  ```

---

## 6. 社区模块

### 6.1 发布帖子
- **接口**：`POST /api/v1/community/post`
- **请求头**：`Authorization: Bearer <token>`
- **请求参数**：
  ```json
  {
    "title": "双十一捡漏攻略",
    "content": "...",
    "images": "url1,url2",
    "goodsLink": "https://..."
  }
  ```
- **返回参数**：帖子ID

### 6.2 查询帖子列表
- **接口**：`GET /api/v1/community/post-list`
- **请求头**：`Authorization: Bearer <token>`
- **返回参数**：帖子列表数组（仅已通过）

### 6.3 查询帖子详情
- **接口**：`GET /api/v1/community/post-detail/{postId}`
- **请求头**：`Authorization: Bearer <token>`

### 6.4 点赞帖子
- **接口**：`POST /api/v1/community/post-like/{postId}`
- **请求头**：`Authorization: Bearer <token>`

### 6.5 收藏帖子
- **接口**：`POST /api/v1/community/post-collect/{postId}`
- **请求头**：`Authorization: Bearer <token>`

### 6.6 发布评论
- **接口**：`POST /api/v1/community/comment`
- **请求头**：`Authorization: Bearer <token>`
- **请求参数**：
  ```json
  {
    "postId": 1,
    "parentCommentId": 0,
    "content": "好攻略！"
  }
  ```

### 6.7 查询帖子评论列表
- **接口**：`GET /api/v1/community/comment-list/{postId}`

### 6.8 审核帖子
- **接口**：`PUT /api/v1/community/post-audit/{postId}/{status}`
- **请求头**：`Authorization: Bearer <token>`
- **参数**：`status`: 1已通过，2已拒绝

### 6.9 删除帖子
- **接口**：`DELETE /api/v1/community/post-delete/{postId}`
- **请求头**：`Authorization: Bearer <token>`

---

## 7. 增量同步模块

### 7.1 Pull数据
- **接口**：`GET /api/v1/sync/pull?tableName=calc_plan&lastWaterTime=2026-01-01T00:00:00`
- **请求头**：`Authorization: Bearer <token>`
- **返回参数**：
  ```json
  {
    "data": {},
    "newWaterTime": "2026-01-02T10:00:00",
    "dataVersion": 2,
    "conflict": false
  }
  ```

### 7.2 Push数据
- **接口**：`POST /api/v1/sync/push`
- **请求头**：`Authorization: Bearer <token>`
- **请求参数**：
  ```json
  {
    "tableName": "calc_plan",
    "localVersion": 1,
    "clientData": {}
  }
  ```
- **返回参数**：
  ```json
  {
    "conflict": false,
    "newDataVersion": 2
  }
  ```

---

## 8. 站内消息模块

### 8.1 查询消息列表
- **接口**：`GET /api/v1/message/list?isRead=0`
- **请求头**：`Authorization: Bearer <token>`

### 8.2 标记消息为已读
- **接口**：`PUT /api/v1/message/read/{messageId}`
- **请求头**：`Authorization: Bearer <token>`

### 8.3 一键全部已读
- **接口**：`PUT /api/v1/message/read-all`
- **请求头**：`Authorization: Bearer <token>`
