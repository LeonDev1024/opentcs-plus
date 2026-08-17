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

# Docker 一键部署 (前后端 + MySQL + Redis + MinIO)
cd script/deploy
./deploy.sh up                              # 本地启动完整栈
./deploy.sh build 2.0.1                     # 生成离线部署包
./deploy.sh down|status|logs|health          # 运维命令
```

** Profiles**：使用 `-Pdev` 或 `-Pprod` 切换环境（默认：dev）。测试按 `@Tag("dev")` 或 `@Tag("prod")` 注解执行。

## 前端项目

前端为独立项目，位于 `../opentcs-plus-web/`：
- 技术栈：Vue 3 + TypeScript + Element Plus + Konva.js
- 开发服务器：`npm run dev`（默认端口 80）
- 构建：`npm run build:prod`
- 详见该目录下的 CLAUDE.md

## 架构设计

> 主叙事：**RuoYi 经典多模块 + RCS 扩展**（详见 `../doc/架构决策-多模块与RCS扩展.md`）。  
> 团队口令：业务改 `modules/<域>`，调度改 `kernel`，车协议改 `driver/adapter-*`，怎么跑起 `admin`。

```
opentcs-plus/
├── opentcs-admin/                          # L0 启动组装（唯一可运行）
│   └── web/config/DriverAdaptersImportConfiguration  # 显式装配 driver-adapter
├── opentcs-modules/                        # L1 业务 modules（对齐 RuoYi）
│   ├── opentcs-system/                     # 系统管理 + 认证策略
│   ├── opentcs-job/                        # 定时任务
│   ├── opentcs-vehicle/                    # 车辆管理 + 派单到车监听
│   ├── opentcs-order/                      # 运输订单
│   ├── opentcs-map-editor/                 # 地图编辑器（含 persistence）
│   └── opentcs-monitor/                    # 运维监控 snapshot + WebSocket
├── opentcs-kernel/                         # L2 RCS 扩展：调度内核
│   ├── opentcs-kernel-api/                 # 端口接口、DTO
│   ├── opentcs-kernel-domain/              # 纯领域模型 + domain.port
│   └── opentcs-kernel-core/                # DispatcherService / RoutePlannerImpl 等
├── opentcs-driver/                         # L2/L3 RCS 扩展：车载
│   ├── opentcs-driver-api/                 # DriverAdapter / VehicleGateway 契约
│   ├── opentcs-driver-runtime/             # DriverRegistry / Gateway / LOOPBACK
│   └── opentcs-driver-adapter-vda5050/     # VDA5050（仅 admin 引入）
├── opentcs-algorithm/                      # L3 RCS 扩展：算法插件 SPI
└── opentcs-common/                         # L4 RuoYi 风格通用能力（含 satoken AuthApi）
```

### 依赖硬规则

1. 业务 modules **禁止**依赖 `driver-adapter-*`（只依赖 `driver-api` + `driver-runtime`）
2. 具体协议 / 算法 jar **只由 admin 引入**
3. `kernel` 只依赖 `common` + 自身
4. 派单到车：`kernel 派单 → 事件 → vehicle 监听 → driver-runtime → adapter-*`
5. 业务 modules 只依赖 `kernel-api` / `kernel-domain.port`，禁止引用 `kernel.application` 实现类

### 架构约束测试（ArchUnit）

| 测试文件 | 覆盖范围 |
|----------|---------|
| `opentcs-kernel-core/.../KernelLayerArchitectureTest` | kernel 不引用 MyBatis/算法实现 |
| `opentcs-modules/opentcs-vehicle/.../VehicleLayerArchitectureTest` | 不依赖 adapter / `kernel.application`；controller 不直连 persistence |
| `opentcs-modules/opentcs-order/.../OrderLayerArchitectureTest` | controller 不碰 Entity；不依赖 `kernel.application` |
| `opentcs-modules/opentcs-map-editor/.../ApplicationPersistenceBoundaryArchitectureTest` | controller 不碰 Entity；不依赖 `kernel.application` |
| `opentcs-driver-runtime/.../DriverLayerArchitectureTest` | runtime 与 api 不依赖 adapter 实现 |
| `opentcs-modules/opentcs-monitor/.../MonitorLayerArchitectureTest` | 不依赖 kernel-core / adapter / 其他域 persistence |

运行：`mvn test -DskipTests=false -Dtest='*ArchitectureTest' -Pdev -pl opentcs-modules/opentcs-vehicle,opentcs-modules/opentcs-order,opentcs-modules/opentcs-map-editor,opentcs-modules/opentcs-monitor,opentcs-driver/opentcs-driver-runtime,opentcs-admin,opentcs-kernel/opentcs-kernel-core,opentcs-algorithm`

### 可观测性

- Actuator 端点：`GET /actuator/health`
- 调度专属分组：`GET /actuator/health/dispatch`（含内核、算法插件、db、redis 状态）
- Prometheus 指标：`GET /actuator/prometheus`
- Spring Boot Admin：在 `application-dev.yml` 中配置 `spring.boot.admin.client.enabled=true`

### 核心技术栈

- **框架**：Spring Boot 3.5.7, JDK 21
- **数据库**：MyBatis Plus 3.5.14 + MySQL 8.0
- **缓存**：Redisson 3.51.0 (Redis 7.0)
- **认证**：Sa-Token 1.44.0 (JWT)
- **消息**：MQTT, SSE, WebSocket
- **算法插件**：SPI + Spring AutoConfiguration + gRPC Bridge（支持 C++/Python/Go）
- **AI 集成**：Spring AI 1.0.0

### 核心领域模型 (opentcs-kernel)

Kernel 模块是调度核心实现（自洽领域模型，不依赖外部 OpenTCS 工程）：
- **kernel-api**：端口接口与 DTO（VehicleTypeApi, VehicleBrandApi, Router, Scheduler 等）
- **kernel-domain**：纯领域模型（Point, Path, Vehicle, VehicleBrand, VehicleType, TransportOrder, Domain Events，无 Spring/MyBatis 依赖）
- **kernel-core**：应用服务（DispatcherService, VehicleRegistry, RoutePlannerImpl）
- **modules/*/persistence**：MyBatis 持久化（归属业务 module，实现 kernel-api 端口）

### API 入口

- REST API：`http://localhost:8088`（默认）
- WebSocket：`/resource/ws/monitor`（监控大屏，独立于通知通道）
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
