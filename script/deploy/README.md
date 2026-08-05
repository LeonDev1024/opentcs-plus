# OpenTCS Plus 部署

前后端 + MySQL + Redis + EMQX Docker 部署。

## 日常发布（推荐）

首次已在服务器装好 Docker 栈之后，日常只需上传 **jar + dist** 热更新，不重建镜像：

```bash
cd opentcs-plus/script/deploy

# 编译并发布到服务器
./deploy.sh quick ubuntu@你的服务器IP

# 已编译过，只上传更新
SKIP_BUILD=1 ./deploy.sh quick ubuntu@你的服务器IP

# 密码登录（可选）
SSHPASS='你的密码' ./deploy.sh quick ubuntu@你的服务器IP
```

流程：本地 `mvn` / `npm` 编译 → 上传 `app.jar` + `dist/` → 服务器 `docker cp` 进容器并重启/reload。

## 快速开始

### 开发机本地启动

```bash
cd opentcs-plus/script/deploy
chmod +x deploy.sh
./deploy.sh up
```

首次运行会自动编译前后端并构建 Docker 镜像，然后启动完整服务栈。

### 前置条件

- **JDK 21** + Maven（本地编译后端 JAR）
- Node.js + npm（编译前端）
- Docker（仅首次打运行镜像 / 导出离线包需要）

```bash
# macOS 切换 JDK 21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
java -version   # 应显示 21.x
```

### 生成离线部署包（仅首次/换机）

```bash
cd opentcs-plus/script/deploy

# 1. 网络较好时预拉 Docker 运行镜像（可选，不含 Maven）
./deploy.sh pull

# 2. 打包（本地编译 JAR + Docker 打镜像）
./deploy.sh build 2.0.1
```

### 网络不稳定时的建议

1. **关闭有问题的镜像加速器**：Docker Desktop → Settings → Docker Engine，删除 `registry-mirrors` 中的 `reg-mirror.qiniu.com`（频繁 EOF 时）。
2. **分步执行**：先 `./deploy.sh pull`，成功后再 `./deploy.sh build`。
3. **复用已有编译产物**（前端已 build 过）：
   ```bash
   SKIP_SOURCE_BUILD=1 ./deploy.sh build opentcs-plus-v2.0.1-release
   ```
4. **在 Linux x86 服务器上打包**（网络更稳定）：上传源码后同样执行 `pull` + `build`。

### 服务器一键安装

```bash
tar xzf opentcs-plus-2.0.1-linux-amd64.tar.gz
cd opentcs-plus-2.0.1-linux-amd64
./install.sh
```

## 目录结构

```
deploy/
├── deploy.sh              # 统一部署入口 (build/up/down/status)
├── docker-compose.yml     # 完整服务编排
├── .env.example           # 环境变量模板
├── application.yml        # 后端 Docker 配置参考
├── backend/Dockerfile     # 后端镜像
├── frontend/
│   ├── Dockerfile         # 前端镜像
│   └── nginx.conf         # Nginx 反向代理
├── config/mysql/          # MySQL 配置与初始化
└── scripts/
    ├── build-package.sh   # 离线打包
    ├── install.sh         # 服务器安装
    └── flyway-migrate.sh  # 数据库迁移
```

## 服务架构

```
Browser → frontend:80 (nginx)
            ├─ /          → SPA
            ├─ /prod-api/ → backend:8088
            └─ /ws/       → WebSocket

backend → mysql:3306, redis:6379, emqx:1883
```

| 服务 | 端口 | 说明 |
|------|------|------|
| frontend | 80 | Vue 前端 + Nginx 反向代理 |
| backend | 8088 | Spring Boot API |
| mysql | 3306 | 数据库 |
| redis | 6379 | 缓存 |
| emqx | 1883/8083/18083 | MQTT TCP / WebSocket / 管理控制台 |

## 常用命令

```bash
./deploy.sh status          # 查看状态
./deploy.sh logs backend    # 查看后端日志
./deploy.sh health          # 健康检查
./deploy.sh down            # 停止服务
./install.sh upgrade        # 离线包升级
./install.sh purge          # 完全卸载
```

## 系统要求

- Linux x86_64
- Docker 20.10+ 与 docker compose 插件
- 目标服务器无需互联网，但需提前安装并启动 Docker
- 建议 4GB+ 内存、20GB+ 磁盘

## EMQX 配置

- MQTT TCP：`tcp://<服务器IP>:1883`
- MQTT WebSocket：`ws://<服务器IP>:8083/mqtt`
- 管理控制台：`http://<服务器IP>:18083`
- 控制台账号及密码：见 `.env` 中 `EMQX_DASHBOARD_USER` / `EMQX_DASHBOARD_PASSWORD`
- 后端默认通过容器网络连接 `tcp://emqx:1883`
