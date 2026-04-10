# 余量宝后端服务

## 项目简介

余量宝是一个基于"拯救食物浪费"理念的盲盒/魔法袋交易平台后端服务，采用 Spring Boot 微服务架构。

**核心功能**：
- 🛒 盲盒商品购买与核销
- 👨‍🍳 商户入驻与门店管理
- 📱 微信小程序用户端
- 💰 余额支付与退款
- 📊 数据统计与分析

## 技术栈

| 类别 | 技术 |
|------|------|
| 核心框架 | Spring Boot 2.5.15 |
| 微服务 | Spring Cloud 2020.0.6 + Spring Cloud Alibaba 2021.1 |
| 数据库 | MySQL 8.0.33 |
| ORM框架 | MyBatis Plus 3.5.3.1 |
| 连接池 | Druid 1.2.20 |
| 缓存 | Redis + Redisson 3.15.1 |
| 消息队列 | RabbitMQ 3.8+ |
| API文档 | Knife4j 3.0.3 |
| 工具类 | Hutool 5.8.22 |
| JSON处理 | FastJSON 2.0.43 |
| JWT认证 | jjwt 0.11.5 |
| 对象映射 | MapStruct 1.5.5 |

## 系统架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              余量宝系统架构                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                 │
│  │   用户端    │     │   商户端    │     │   管理端    │                 │
│  │  微信小程序 │     │  微信小程序 │     │   Web后台   │                 │
│  └──────┬──────┘     └──────┬──────┘     └──────┬──────┘                 │
│         │                    │                    │                        │
│         └────────────────────┼────────────────────┘                        │
│                              ▼                                              │
│                    ┌───────────────────┐                                   │
│                    │    API Gateway    │  (ylb-gateway)                   │
│                    │   统一入口/鉴权   │                                   │
│                    └─────────┬─────────┘                                   │
│                              │                                              │
│         ┌────────────────────┼────────────────────┐                        │
│         ▼                    ▼                    ▼                        │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                │
│  │ 用户服务    │     │ 商户服务    │     │ 订单服务    │                │
│  │ ylb-user   │     │ ylb-merchant│     │ ylb-order   │                │
│  └──────┬──────┘     └──────┬──────┘     └──────┬──────┘                │
│         │                    │                    │                        │
│         └────────────────────┼────────────────────┘                        │
│                              ▼                                              │
│                    ┌───────────────────┐                                   │
│                    │   ylb-common      │  (公共模块)                       │
│                    │   实体/服务/工具   │                                   │
│                    └─────────┬─────────┘                                   │
│                              │                                              │
│         ┌────────────────────┼────────────────────┐                        │
│         ▼                    ▼                    ▼                        │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                │
│  │    MySQL    │     │    Redis    │     │  RabbitMQ   │                │
│  │   8.0.33    │     │    6.0+     │     │    3.8+     │                │
│  └─────────────┘     └─────────────┘     └─────────────┘                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 项目结构

```
余量宝后端/
├── ylb-common/                      # 公共模块（核心）
│   └── src/main/java/com/yuliangbao/common/
│       ├── config/                  # 配置类
│       │   ├── RedisConfig          # Redis配置
│       │   ├── RabbitMQConfig      # RabbitMQ配置
│       │   └── SecurityConfig      # 安全配置
│       ├── result/                  # 统一响应
│       ├── exception/               # 异常处理
│       ├── constant/                # 常量定义
│       ├── util/                    # 工具类
│       │   ├── JwtUtil              # JWT工具
│       │   └──WxPayUtil            # 微信支付工具
│       ├── pojo/
│       │   ├── entity/              # 实体类
│       │   ├── dto/                 # 数据传输对象
│       │   ├── vo/                  # 视图对象
│       │   └── enums/               # 枚举类
│       ├── mapper/                  # 数据访问层
│       ├── service/                 # 业务逻辑层
│       │   ├── user/                # 用户服务
│       │   ├── merchant/            # 商户服务
│       │   ├── order/               # 订单服务
│       │   ├── product/             # 商品服务
│       │   └── message/             # 消息服务
│       └── task/                    # 定时任务
│
├── ylb-gateway/                     # API网关服务
│   └── src/main/
│       ├── java/com/yuliangbao/gateway/
│       │   ├── controller/
│       │   │   ├── user/            # 用户接口
│       │   │   ├── merchant/        # 商户接口
│       │   │   ├── admin/           # 管理接口
│       │   │   └── common/          # 公共接口
│       │   ├── config/              # 网关配置
│       │   ├── interceptor/         # 拦截器
│       │   └── filter/              # 过滤器
│       └── resources/
│           └── application.yml       # 配置文件
│
├── sql/                             # 数据库脚本
│   └── init.sql                     # 初始化脚本
│
├── API接口文档.md                    # API接口文档
├── CONCURRENT_SAFETY_SOLUTION.md     # 并发安全解决方案
└── pom.xml                          # Maven父POM
```

## 核心业务模块

### 用户模块 (ylb-user)
- 微信登录/注册
- 用户信息管理
- 余额充值/消费
- 订单历史

### 商户模块 (ylb-merchant)
- 商户入驻/审核
- 门店管理
- 商品管理（上架/下架/库存）
- 经营数据统计

### 订单模块 (ylb-order)
- 订单创建/支付
- 订单核销
- 退款申请/处理
- 超时订单自动取消

### 商品模块 (ylb-product)
- 商品列表/详情
- 库存管理
- 价格策略

## 环境要求

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8+ | 推荐 JDK 8 或 JDK 11 |
| Maven | 3.6+ | 构建工具 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 6.0+ | 缓存/分布式锁 |
| RabbitMQ | 3.8+ | 消息队列 |

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd 余量宝后端
```

### 2. 数据库初始化

```bash
# 连接到MySQL
mysql -uroot -p

# 创建数据库
CREATE DATABASE yuliangbao CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行初始化脚本
source sql/init.sql
```

**初始化数据库字段变更（如有更新）**：
```sql
-- 添加用户余额乐观锁版本字段
ALTER TABLE user_info ADD COLUMN version INT NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）';
```

### 3. 配置文件修改

配置文件位于 `ylb-gateway/src/main/resources/application.yml`

```yaml
# 必需配置
spring:
  datasource:
    url: jdbc:mysql://your-mysql-host:3306/yuliangbao
    username: your-username
    password: your-password
  redis:
    host: your-redis-host
    password: your-password
  rabbitmq:
    host: your-rabbitmq-host
    username: your-username
    password: your-password

# 可选配置（可通过环境变量覆盖）
app:
  cors:
    allowed-origins: http://localhost:8081,http://your-domain.com
  jwt:
    secret: your-jwt-secret-key
```

### 4. 编译项目

```bash
# 编译整个项目
mvn clean install -DskipTests
```

### 5. 启动服务

```bash
# 方式一：使用Maven启动
cd ylb-gateway
mvn spring-boot:run

# 方式二：使用JAR包启动
java -jar ylb-gateway/target/ylb-gateway-1.0.0.jar
```

### 6. 访问服务

| 服务 | 地址 |
|------|------|
| API地址 | http://localhost:8080/api |
| API文档 | http://localhost:8080/api/doc.html |
| Druid监控 | http://localhost:8080/api/druid |

## 配置说明

### 核心配置项

```yaml
server:
  port: 8080                    # 服务端口

spring:
  application:
    name: ylb-gateway

  # 数据源（Druid连接池）
  datasource:
    url: jdbc:mysql://host:3306/yuliangbao
    username: you user name
    password: you pass word
    druid:
      initial-size: 5           # 初始连接数
      min-idle: 10              # 最小空闲连接
      max-active: 20            # 最大活跃连接

  # Redis缓存
  redis:
    host: localhost
    password: you password
    database: 0

  # RabbitMQ消息队列
  rabbitmq:
    host: localhost
    username: you user name
    password: you password

# 应用配置
app:
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:}  # CORS白名单
  jwt:
    secret: ${JWT_SECRET:}        # JWT密钥
    expiration: 604800000         # 过期时间(7天)
  upload:
    path: /data/upload            # 上传文件目录
```

### 环境变量

| 变量名 | 说明 | 默认值           |
|--------|------|---------------|
| `SPRING_PROFILES_ACTIVE` | 激活的配置环境 | dev           |
| `DB_HOST` | MySQL主机 | you host      |
| `DB_PORT` | MySQL端口 | 3306          |
| `DB_NAME` | 数据库名 | yuliangbao    |
| `DB_USERNAME` | 数据库用户名 | you username  |
| `DB_PASSWORD` | 数据库密码 | you password  |
| `REDIS_HOST` | Redis主机 | you host      |
| `REDIS_PORT` | Redis端口 | 6379          |
| `REDIS_PASSWORD` | Redis密码 | you pass word |
| `RABBITMQ_HOST` | RabbitMQ主机 | you host      |
| `JWT_SECRET` | JWT密钥 | (需配置)         |
| `CORS_ALLOWED_ORIGINS` | CORS允许的域名 | (需配置)         |

## 并发安全机制

本系统采用多种技术手段保证高并发场景下的数据安全：

| 并发场景 | 解决方案 | 实现方式 |
|----------|----------|----------|
| 库存超卖 | 原子扣减 | SQL WHERE条件 + 数据库行锁 |
| 余额扣减 | 乐观锁 | version字段版本控制 |
| 定时任务重复执行 | 分布式锁 | Redis SETNX |
| 核销分账阻塞 | 异步处理 | RabbitMQ消息队列 |
| 缓存雪崩 | TTL随机化 | 过期时间±10%随机波动 |


## API接口

### 接口概览

| 模块 | 接口数 | 主要功能 |
|------|--------|----------|
| 用户模块 | 8 | 登录、注册、信息、余额 |
| 商户模块 | 15 | 入驻、审核、门店、商品 |
| 订单模块 | 12 | 创建、支付、核销、退款 |
| 商品模块 | 6 | 列表、详情、库存 |
| 管理模块 | 20 | 用户/商户/订单/统计管理 |
| 公共模块 | 5 | 文件上传、微信登录 |

详细API文档见 [API接口文档.md](API接口文档.md)

## 开发指南

### 代码规范

- 遵循阿里巴巴Java开发规范
- 使用Lombok简化代码（`@Data`, `@Slf4j`等）
- 使用统一响应格式 `Result<T>`
- 使用全局异常处理器
- Controller层禁止业务逻辑

### 分层架构

```
Controller层 → Service层 → Mapper层 → Database
     │            │           │
     │            │           └── 数据访问
     │            └── 业务逻辑处理
     └── 接收请求/返回响应
```

### 新增功能步骤

1. **创建实体类** - `ylb-common/pojo/entity/`
2. **创建Mapper** - `ylb-common/mapper/`
3. **创建Service** - `ylb-common/service/`
4. **创建Controller** - `ylb-gateway/controller/`
5. **配置路由** - 如需要

### Git提交规范

```
feat:     新功能
fix:      修复bug
docs:     文档更新
style:    代码格式调整
refactor: 重构
test:     测试相关
chore:    构建/工具相关
```

```bash
# 示例
git commit -m "feat: 实现用户余额充值功能"
git commit -m "fix: 修复库存超卖问题"
git commit -m "docs: 更新API接口文档"
```

## 常见问题

### 1. 编译报错：Lombok未生效

**问题**：IDE中Lombok注解（如@Data, @Slf4j）报红

**解决方案**：
- 检查IDE是否安装Lombok插件
- 确保Maven配置了注解处理器
- 尝试 `mvn clean` 后重新编译

### 2. 数据库连接失败

**问题**：`Communications link failure`

**解决方案**：
- 检查MySQL服务是否启动
- 验证数据库连接配置
- 检查防火墙是否开放端口

### 3. Redis连接失败

**问题**：`Unable to connect to Redis`

**解决方案**：
- 检查Redis服务是否启动
- 验证Redis密码是否正确
- 检查Redis配置中的bind地址

### 4. RabbitMQ消息不消费

**问题**：消息发送成功但未消费

**解决方案**：
- 检查消费者是否启动
- 验证队列和交换机配置
- 检查消息序列化方式

### 5. CORS跨域问题

**问题**：浏览器提示跨域错误

**解决方案**：
- 配置 `app.cors.allowed-origins`
- 生产环境使用具体域名，不用通配符

## 部署指南

### 生产环境部署

```bash
# 1. 打包
mvn clean package -DskipTests

# 2. 复制JAR包
cp ylb-gateway/target/ylb-gateway-1.0.0.jar /data/app/

# 3. 创建启动脚本
cat > /data/app/start.sh << 'EOF'
#!/bin/bash
java -jar \
  -Xms512m -Xmx1024m \
  -Dspring.profiles.active=prod \
  -DB_HOST=prod-mysql-host \
  -DREDIS_HOST=prod-redis-host \
  /data/app/ylb-gateway-1.0.0.jar
EOF

# 4. 后台启动
nohup /data/app/start.sh > /data/app logs/ylb.log 2>&1 &
```

### Docker部署（可选）

```dockerfile
FROM openjdk:8-jdk-slim
WORKDIR /app
COPY target/ylb-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 监控与运维

### Druid监控

访问 `http://host:8080/api/druid` 查看：
- 数据源监控
- SQL执行监控
- URI访问监控

### 日志配置

日志配置文件：`ylb-gateway/src/main/resources/logback-spring.xml`

```xml
<logger name="com.yuliangbao" level="DEBUG"/>
<logger name="org.springframework" level="INFO"/>
<logger name="com.alibaba.druid" level="INFO"/>
```

## 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 平台管理 |
| 商户 | (申请入驻) | - | 需审核 |

## 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0.0 | 2026-02-28 | 初始版本发布 |
| 1.0.1 | 2026-03-09 | 并发安全优化：乐观锁、分布式锁、MQ异步处理 |

*项目最后更新时间：2026-03-09*
