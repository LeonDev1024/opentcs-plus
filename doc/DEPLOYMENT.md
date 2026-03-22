# OpenTCS Plus 生产环境部署指南

## 1. 部署架构

### 1.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                           用户访问                                    │
└─────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Nginx (宿主机端口 80)                                               │
│  ├── 静态文件服务 /var/www/opentcs                                   │
│  ├── API 代理 /prod-api/ → backend:8088                            │
│  └── WebSocket 代理 /ws/ → backend:8088                            │
└─────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Docker 网络 (opentcs-network)                                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐            │
│  │   Backend    │   │    MySQL     │   │    Redis    │            │
│  │ opentcs-admin│   │ opentcs-mysql│   │ opentcs-redis│            │
│  │   端口 8088  │   │   端口 3306  │   │   端口 6379  │            │
│  └──────────────┘   └──────────────┘   └──────────────┘            │
│                                                                     │
│  ┌──────────────┐                                                   │
│  │    MinIO     │                                                   │
│  │ opentcs-minio│                                                   │
│  │ 端口 9000/9001│                                                   │
│  └──────────────┘                                                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 服务清单

| 服务 | 容器名称 | 端口映射 | 数据卷 | 说明 |
|------|----------|----------|--------|------|
| Nginx | 宿主机 | 80:80 | /var/www/opentcs | 前端静态文件 + API 代理 |
| Backend | opentcs-admin | 8088:8088 | application.yml | Spring Boot 应用 |
| MySQL | opentcs-mysql | 3306:3306 | /data/mysql | 数据库 |
| Redis | opentcs-redis | 6379:6379 | /data/redis | 缓存 |
| MinIO | opentcs-minio | 9000:9000, 9001:9001 | /data/minio | 对象存储 |

## 2. 环境要求

### 2.1 服务器要求

- **操作系统**: Ubuntu 20.04+ / CentOS 8+
- **内存**: 至少 4GB（推荐 8GB）
- **磁盘**: 至少 40GB SSD
- **网络**: 可访问外网（用于拉取 Docker 镜像）

### 2.2 依赖服务

- Docker 20.10+
- Docker Compose 1.29+
- Nginx (宿主机安装)

## 3. 快速部署

### 3.1 初始化服务器

```bash
# 1. 安装 Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# 2. 配置 Docker 镜像加速 (腾讯云)
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<EOF
{
  "registry-mirrors": ["https://mirror.ccs.tencentyun.com"]
}
EOF
sudo systemctl restart docker

# 3. 安装 Nginx
sudo apt update
sudo apt install -y nginx
```

### 3.2 配置 Nginx

```bash
sudo tee /etc/nginx/sites-available/opentcs <<'EOF'
server {
    listen 80;
    server_name _;

    root /var/www/opentcs;
    index index.html;

    # 安全头
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # API 代理 (/prod-api -> 后端)
    location /prod-api/ {
        proxy_pass http://127.0.0.1:8088/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
        proxy_send_timeout 60s;
    }

    # WebSocket 代理
    location /ws/ {
        proxy_pass http://127.0.0.1:8088/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_read_timeout 86400;
    }

    # SPA History 模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 健康检查
    location /health {
        return 200 "OK";
        add_header Content-Type text/plain;
    }
}
EOF

# 启用配置
sudo ln -s /etc/nginx/sites-available/opentcs /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 3.3 部署所有服务

```bash
# 1. 创建数据目录
sudo mkdir -p /data/{mysql,redis,minio}
sudo mkdir -p /var/www/opentcs

# 2. 创建 docker-compose.yml
cat > /home/ubuntu/docker-compose.yml <<'EOF'
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: opentcs-mysql
    restart: unless-stopped
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: MySQL@2024!Root
      MYSQL_DATABASE: opentcs
      MYSQL_USER: opentcs
      MYSQL_PASSWORD: Opentcs@2024!DB
    volumes:
      - /data/mysql:/var/lib/mysql
    networks:
      - opentcs-network

  redis:
    image: redis:7-alpine
    container_name: opentcs-redis
    restart: unless-stopped
    ports:
      - "6379:6379"
    command: redis-server --requirepass Redis@2024!Cache
    volumes:
      - /data/redis:/data
    networks:
      - opentcs-network

  minio:
    image: minio/minio
    container_name: opentcs-minio
    restart: unless-stopped
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: MinIO@2024!Admin
    volumes:
      - /data/minio:/data
    command: server /data --console-address ":9001"
    networks:
      - opentcs-network

  backend:
    image: opentcs-admin:latest
    container_name: opentcs-admin
    restart: unless-stopped
    ports:
      - "8088:8088"
    volumes:
      - /home/ubuntu/application.yml:/app/application.yml
    depends_on:
      mysql:
        condition: service_started
      redis:
        condition: service_started
    networks:
      - opentcs-network

networks:
  opentcs-network:
    driver: bridge
EOF

# 3. 启动所有服务
cd /home/ubuntu
docker-compose up -d
```

## 4. 配置文件说明

### 4.1 后端配置 (application.yml)

```yaml
spring:
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://mysql:3306/opentcs?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&autoReconnect=true&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true
          username: opentcs
          password: Opentcs@2024!DB
  data:
    redis:
      host: redis
      port: 6379
      password: Redis@2024!Cache
server:
  port: 8088

xxl:
  job:
    enabled: false

mqtt:
  enabled: false
```

### 4.2 环境变量说明

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| MYSQL_ROOT_PASSWORD | MySQL root 密码 | MySQL@2024!Root |
| MYSQL_DATABASE | 数据库名称 | opentcs |
| MYSQL_USER | 数据库用户 | opentcs |
| MYSQL_PASSWORD | 数据库密码 | Opentcs@2024!DB |
| REDIS_PASSWORD | Redis 密码 | Redis@2024!Cache |
| MINIO_USER | MinIO 用户 | minioadmin |
| MINIO_PASSWORD | MinIO 密码 | MinIO@2024!Admin |

## 5. 日常运维

### 5.1 服务管理

```bash
# 查看所有容器状态
docker ps -a

# 查看容器日志
docker logs -f opentcs-admin     # 后端日志
docker logs -f opentcs-mysql     # MySQL 日志
docker logs -f opentcs-redis     # Redis 日志
docker logs -f opentcs-minio     # MinIO 日志

# 重启单个服务
docker restart opentcs-admin

# 停止所有服务
docker-compose down

# 启动所有服务
docker-compose up -d

# 重启所有服务
docker-compose restart
```

### 5.2 数据库操作

```bash
# 进入 MySQL 容器
docker exec -it opentcs-mysql mysql -uroot -pMySQL@2024!Root

# 导入 SQL 文件
docker exec -i opentcs-mysql mysql -uroot -pMySQL@2024!Root opentcs < /path/to/sql

# 备份数据库
docker exec opentcs-mysql mysqldump -uroot -pMySQL@2024!Root opentcs > backup.sql
```

### 5.3 更新部署

```bash
# 1. 在开发机构建新版本
cd opentcs-plus
mvn clean package -DskipTests -Pprod

# 2. 打包 Docker 镜像
docker build -t opentcs-admin:latest ./opentcs-admin

# 3. 上传到服务器
# 方式 A: 保存镜像文件
docker save opentcs-admin:latest > opentcs-admin.tar
scp opentcs-admin.tar user@server:/home/ubuntu/
docker load < opentcs-admin.tar

# 方式 B: 推送到镜像仓库 (推荐)
docker tag opentcs-admin:latest ghcr.io/yourrepo/opentcs-admin:latest
docker push ghcr.io/yourrepo/opentcs-admin:latest
# 服务器上拉取
docker pull ghcr.io/yourrepo/opentcs-admin:latest

# 4. 重启后端
docker restart opentcs-admin
```

### 5.4 前端更新

```bash
# 1. 在开发机构建前端
cd opentcs-plus-web
npm run build:prod

# 2. 上传静态文件到服务器
scp -r dist/* user@server:/var/www/opentcs/

# 或者使用 rsync
rsync -avz --delete dist/ user@server:/var/www/opentcs/
```

## 6. 访问信息

| 服务 | 地址 | 账号 |
|------|------|------|
| 前端 | http://106.54.43.41 | admin / admin123 |
| 后端 API | http://106.54.43.41:8088 | - |
| MinIO 控制台 | http://106.54.43.41:9001 | minioadmin / MinIO@2024!Admin |
| MySQL | 127.0.0.1:3306 | root / MySQL@2024!Root |
| Redis | 127.0.0.1:6379 | - |

## 7. 故障排查

### 7.1 常见问题

**问题: 后端无法连接 MySQL**
```bash
# 检查 MySQL 容器状态
docker logs opentcs-mysql

# 检查网络连通性
docker exec opentcs-admin ping mysql

# 检查 MySQL 日志
docker logs opentcs-mysql | grep ERROR
```

**问题: 前端无法访问 API**
```bash
# 检查 Nginx 状态
sudo systemctl status nginx

# 检查后端是否启动
docker logs opentcs-admin | grep Started

# 测试 API 直接访问
curl http://localhost:8088/auth/code
```

**问题: 图片/文件无法上传**
```bash
# 检查 MinIO 是否运行
docker logs opentcs-minio

# 检查 MinIO 健康状态
curl http://localhost:9000/minio/health/live
```

### 7.2 日志位置

- Nginx: `/var/log/nginx/`
- 后端: `docker logs opentcs-admin`
- MySQL: `docker logs opentcs-mysql`
- Redis: `docker logs opentcs-redis`

## 8. 安全建议

1. **修改默认密码**: 首次部署后立即修改所有默认密码
2. **配置防火墙**: 只开放必要端口 (80, 443, 22)
3. **定期备份**: 定期备份数据库和重要数据
4. **更新镜像**: 定期更新 Docker 镜像到最新版本
5. **日志监控**: 配置日志收集和告警

## 9. GitHub Actions CI/CD (可选)

参考 `.github/workflows/deploy.yml` 配置自动部署流程。

```yaml
# 触发条件: push 到 main 分支
# 自动执行: 构建 → 推送镜像 → 部署到服务器
```
