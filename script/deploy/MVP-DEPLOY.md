# OpenTCS Plus MVP 部署指南

## 前提条件

- Docker Desktop 4.x+（含 Docker Compose v2）
- 可用内存：≥ 4GB
- 可用磁盘：≥ 10GB
- 开放端口：80、8088、3306、6379、9000、9001

---

## 一键启动（首次部署）

```bash
cd opentcs-plus/script/deploy

# 第一步：构建镜像（约 10-15 分钟）
./scripts/mvp-start.sh build

# 第二步：启动所有服务
./scripts/mvp-start.sh start
```

启动成功后访问：
- **前端**：http://localhost
- **后端 API**：http://localhost:8088
- **MinIO 控制台**：http://localhost:9001

默认账号：`admin` / `admin123`

---

## 日常操作

```bash
./scripts/mvp-start.sh status   # 查看状态
./scripts/mvp-start.sh logs     # 查看所有日志
./scripts/mvp-start.sh logs backend   # 只看后端日志
./scripts/mvp-start.sh stop     # 停止
./scripts/mvp-start.sh restart  # 重启
```

---

## MVP 验收流程（端到端冒烟测试）

按以下步骤验证核心链路：

### 1. 查看预置演示数据

登录后导航至：**部署配置 → 工厂配置 → 工厂列表**

应看到「演示仓库」工厂，包含：
- 一张导航地图「一楼导航图」
- 10 个点位（P001–P010）
- 22 条路径（双向 + 竖向连接）
- 5 个位置（4 个货架 + 1 个充电桩）

### 2. 地图编辑器验证

导航至：**部署配置 → 工厂配置 → 工厂列表 → 编辑地图**

- 应看到 2×5 网格点位渲染在画布上
- 修改任意点位属性，点击「保存」
- 刷新页面，确认修改持久化 ✅

### 3. 车辆配置验证

导航至：**部署配置 → 设备配置**

应看到：
- 品牌：演示品牌
- 车型：标准仿真车型
- 车辆：AGV-SIM-001、AGV-SIM-002（状态：IDLE）

### 4. 仿真执行验证

导航至：**运营管理 → 订单管理 → 创建订单**

- 起点：P001（入库点-1）
- 终点：P005（出库点-5）
- 分配车辆：AGV-SIM-001
- 提交订单

导航至：**运营管理 → 监控大屏**

- 应看到 AGV-SIM-001 开始移动
- 订单状态从 ACTIVE → ASSIGNED → FINISHED

### 5. 验收标准

| 验收项 | 预期结果 |
|--------|---------|
| 系统启动 | `docker compose ps` 所有服务 healthy |
| 地图保存 | 编辑器修改 → 保存 → 刷新后数据持久化 |
| 仿真执行 | 订单最终状态 FINISHED（不超时、不崩溃） |
| 监控大屏 | 车辆实时位置更新可见 |
| 重复10次 | 以上流程无崩溃 |

---

## 环境变量配置（.env）

首次启动自动创建，**生产环境必须修改密码**：

```bash
MYSQL_ROOT_PASSWORD=root123        # MySQL root 密码
MYSQL_DATABASE=opentcs             # 数据库名
MYSQL_USER=opentcs                 # 应用数据库用户
MYSQL_PASSWORD=opentcs123          # 应用数据库密码
REDIS_PASSWORD=redis123            # Redis 密码
MINIO_USER=minioadmin              # MinIO 用户
MINIO_PASSWORD=minioadmin123       # MinIO 密码
```

---

## 常见问题

### 端口冲突

修改 `docker-compose.yml` 中的端口映射：
```yaml
ports:
  - "18088:8088"   # 改为 18088 对外暴露
```

### MySQL 初始化失败

```bash
./scripts/mvp-start.sh clean   # 清理数据卷
./scripts/mvp-start.sh start   # 重新启动
```

### 后端启动慢

首次启动 JVM 预热约 60–90 秒，属正常现象。

### 查看后端启动日志

```bash
docker logs -f opentcs-admin
```

---

## 目录结构

```
script/deploy/
├── config/
│   ├── mysql/
│   │   ├── my.cnf                    # MySQL 配置
│   │   └── init.d/
│   │       ├── 01_init_database.sql  # 创建数据库
│   │       ├── 02_schema.sql         # 全量表结构
│   │       └── 03_demo_data.sql      # 演示数据
│   └── redis/
│       └── redis.conf                # Redis 配置
├── backend/
│   └── Dockerfile
├── frontend/
│   ├── Dockerfile
│   └── nginx.conf
├── scripts/
│   ├── mvp-start.sh                  # 一键启动脚本
│   ├── deploy.sh                     # CI/CD 部署脚本
│   └── healthcheck.sh
├── docker-compose.yml                # 开发/演示环境
├── docker-compose-prod.yml           # 生产环境（高可用）
├── .env.example                      # 环境变量模板
└── MVP-DEPLOY.md                     # 本文档
```
