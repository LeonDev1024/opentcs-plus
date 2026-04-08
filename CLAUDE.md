# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

本文档为 Claude Code (claude.ai/code) 在本项目中工作时提供指导。
## 项目简介

OpenTCS Plus 是基于 OpenTCS 内核构建的企业级 AGV（自动导引车）调度系统。采用 Spring Boot 3.5 + JDK 21 开发，模块化架构设计，适用于仓储物流场景的私有化部署。

## 项目结构

```
opentcsplus/                              # 项目根目录
├── opentcs-plus/                        # 后端系统 (当前目录)
├── opentcs-plus-web/                    # 前端 Vue 3 项目 (独立 CLAUDE.md)
├── opentcs-plus-docs/                   # VitePress 文档
└── doc/                                 # 项目文档资源
```

## 构建命令

```bash
# 进入后端目录
cd opentcs-plus

# 构建整个项目（默认跳过测试）
mvn clean package -DskipTests

# 构建并运行测试（按 profile 标签执行）
mvn clean package -Pdev                  # 执行 dev 标签的测试
mvn clean package -Pprod                 # 执行 prod 标签的测试

# 运行单个测试类
mvn test -Dtest=ClassName -Pdev

# 运行单个测试方法
mvn test -Dtest=ClassName#methodName -Pdev

# 仅编译不打包
mvn compile

# 构建 Docker 镜像
cd opentcs-admin && docker build -t opentcs-admin:latest .

# 部署脚本（构建、启动、停止、重启）
./script/deploy.sh build|start|stop|restart
```

** Profiles**：使用 `-Pdev` 或 `-Pprod` 切换环境（默认：dev）。测试按 `@Tag("dev")` 或 `@Tag("prod")` 注解执行。

## 前端项目

前端为独立项目，位于 `../opentcs-plus-web/`：
- 技术栈：Vue 3 + TypeScript + Element Plus + Konva.js
- 开发服务器：`npm run dev`（默认端口 80）
- 构建：`npm run build:prod`
- 详见该目录下的 CLAUDE.md

## 架构设计

### 基于 OpenTCS Kernel 的重构架构

```
opentcs-plus/
├── opentcs-admin/                          # 接口层 - Spring Boot 启动入口、全局配置
│   └── controller/                         # 仅含认证/入口 Controller（AuthController 等）
├── opentcs-applications/                   # 应用层 - 业务用例/服务编排
│   ├── opentcs-map-editor/                 # 地图编辑器
│   ├── opentcs-order/                      # 订单任务
│   ├── opentcs-vehicle/                    # 车辆管理（含 Brand/VehicleType/Vehicle）
│   │   ├── application/                    # 应用服务（BrandApplicationService 等）
│   │   └── controller/                     # Controller 只调用应用服务，不直接访问 persistence
│   ├── opentcs-system/                     # 系统管理 + 认证策略
│   │   └── auth/                           # SysLoginService / SysRegisterService / IAuthStrategy 及实现
│   └── opentcs-simulation/                 # 仿真模拟
├── opentcs-kernel/                         # 领域层 - Kernel 契约与领域模型
│   ├── opentcs-kernel-api/                 # 端口接口、算法契约、DTO（Router/Scheduler）
│   ├── opentcs-kernel-domain/              # 纯领域模型（Point/Path/Vehicle/TransportOrder/Domain Event）
│   │                                       # 含 RoutingAlgorithm 接口（算法注入点）
│   └── opentcs-kernel-core/                # 应用服务（DispatcherService/VehicleRegistry/RoutePlannerImpl）
├── opentcs-infrastructure/                 # 基础设施层 - 持久化实现（MyBatis）
│   ├── opentcs-map-persistence/            # 地图持久化
│   ├── opentcs-order-persistence/          # 订单持久化
│   └── opentcs-vehicle-persistence/        # 车辆持久化（BrandDomainService 等接口实现）
├── opentcs-strategies-default/             # 策略层 - 可替换的默认算法实现
│   └── AStarRoutingAlgorithm               # 实现 RoutingAlgorithm 接口，依赖 kernel-domain
├── opentcs-driver/                         # 基础设施层 - AGV 驱动适配
│   ├── opentcs-driver-api/                 # 驱动接口契约
│   └── opentcs-driver-adapter-vda5050/     # VDA5050 协议适配器
├── opentcs-security/                       # 安全领域模块
│   ├── opentcs-security-api/               # AuthApi / PermissionApi
│   └── opentcs-security-core/              # 安全实现（待与 common-security/satoken 整合）
├── opentcs-common/                         # 通用基础模块（共 23 个子模块）
│   ├── opentcs-common-core/                # 核心：DTO、枚举、异常、R
│   ├── opentcs-common-mybatis/             # MyBatis Plus 封装
│   ├── opentcs-common-redis/               # Redisson 缓存
│   ├── opentcs-common-security/            # 安全配置
│   ├── opentcs-common-satoken/             # Sa-Token JWT
│   ├── opentcs-common-websocket/           # WebSocket
│   ├── opentcs-common-mqtt/                # MQTT 集成
│   ├── opentcs-common-oss/                 # 文件存储
│   ├── opentcs-common-sms/                 # 短信
│   └── ... (其余扩展模块)
└── pom.xml
```

### 分层依赖规则（单向依赖）

```
接口层(admin) → 应用层 → 领域层(kernel-api/domain) → 无外部依赖
                        ↓
             基础设施层（实现 kernel-api 端口）
             策略层（实现 RoutingAlgorithm 等算法接口）

禁止方向：
❌ 领域层 → 基础设施层
❌ 接口层/应用层 → persistence 实体/Mapper（必须通过应用服务）
❌ 领域层 → Spring / common-infra
```

### 核心技术栈

- **框架**：Spring Boot 3.5.7, JDK 21
- **数据库**：MyBatis Plus 3.5.14 + MySQL 8.0
- **缓存**：Redisson 3.51.0 (Redis 7.0)
- **认证**：Sa-Token 1.44.0 (JWT)
- **消息**：MQTT, SSE
- **AI 集成**：Spring AI 1.0.0-M4

### 核心领域模型 (opentcs-kernel)

Kernel 模块是调度核心实现（不依赖外部 OpenTCS 工程，自洽领域模型）：
- **kernel-api**：端口与 DTO、算法接口（`Router`/`Scheduler` 等）
- **kernel-domain**：领域模型（Point, Path, Vehicle, TransportOrder 等，无基础设施依赖）
- **kernel-core**：应用服务（注册表、调度、路由规划装配）
- **opentcs-infrastructure-kernel-persistence**：MyBatis 持久化实现（基础设施层）

### API 入口

- REST API：`http://localhost:8088`（默认）
- WebSocket：`/ws/**`
- MQTT：可配置的消息代理集成

### 配置文件

配置文件位于 `opentcs-admin/src/main/resources/`：
- `application.yml` - 主配置
- `application-dev.yml` - 开发环境
- `application-prod.yml` - 生产环境

## 开发规范
## Git 工作规范

> 项目统一的 Git 提交和 PR 规范，详见根目录 `CLAUDE.md`

### Commit 提交原则

#### 1. 原子性原则
- **每次提交只做一件事**：一个提交应该能够独立编译、运行和测试
- 避免"一锅炖"式的提交（如同时修改业务逻辑、修复 bug、重构代码）

#### 2. 及时提交原则
- 完成一个独立的功能点后**立即提交**，不要等到代码写了很多以后才提交
- **每天至少提交一次**

#### 3. 提交信息规范

**标题格式**：`type(scope): description`

类型（type）说明：
| 类型 | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | bug 修复 |
| `docs` | 文档更新 |
| `style` | 代码格式（不影响功能） |
| `refactor` | 重构（既不是新功能也不是 bug 修复） |
| `perf` | 性能优化 |
| `test` | 测试相关 |
| `chore` | 构建/工具链变更 |

**标题示例**：
- `feat(order): 添加订单批量处理功能`
- `fix(vehicle): 修复车辆状态同步问题`
- `refactor(driver): 重构驱动适配器架构`

#### 4. 提交粒度建议
- 单个文件修改：可以直接提交
- 多个文件但同一功能：可以一起提交
- 多个不相关的改动：**分别提交**

---

### Pull Request 原则

#### 1. 小而专注原则
- PR 应该是针对一个独立的功能或 bug 修复
- 理想情况下，一个 PR 的代码量应该能在 **30 分钟内** 完成审查
- **单个 PR 的文件修改不超过 10 个**

#### 2. 可审查性原则
- PR 标题应清晰描述改动内容
- PR 描述应包含：
    - 改动目的（解决什么问题）
    - 改动内容概述
    - 测试情况说明

#### 3. 可测试性原则
- 确保代码能够在本地正常运行
- 如果有自动化测试，需要通过测试

#### 4. PR 流程
1. 从最新的 `main` 分支创建新分支
2. 在新分支上进行开发
3. 提交代码并推送
4. 创建 PR 并描述改动内容
5. 等待代码审查和 CI 检查
6. 根据反馈进行修改
7. 合并后删除分支

---
