# OpenTCS Plus 部署配置

## 目录结构

```
deploy/
├── docker-compose.yml          # 整体编排（推荐）
├── backend/
│   ├── Dockerfile             # 后端 Dockerfile
│   └── start.sh               # 后端启动脚本
├── frontend/
│   ├── nginx.conf             # Nginx 配置
│   └── start.sh               # 前端启动脚本
└── scripts/
    ├── deploy.sh              # 一键部署脚本
    └── healthcheck.sh         # 健康检查脚本
```

## 快速开始

### 1. 一键部署（推荐）

```bash
# 克隆项目后执行
cd opentcs-plus/script/deploy
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

### 2. 手动部署

```bash
# 1. 启动基础设施（MySQL/Redis/MinIO）
docker-compose up -d mysql redis minio

# 2. 构建并启动后端
cd backend
docker build -t opentcs-admin:latest .
docker run -d --name opentcs-admin -p 8088:8088 --link mysql --link redis --link minio opentcs-admin:latest

# 3. 启动前端
cd frontend
docker run -d --name opentcs-web -p 80:80 -p 443:443 --link opentcs-admin opentcs-nginx:latest
```

### 3. Docker Compose 部署（最简）

```bash
# 修改 docker-compose.yml 中的配置后
docker-compose up -d
```
