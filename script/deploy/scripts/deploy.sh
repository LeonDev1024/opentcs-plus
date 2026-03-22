#!/bin/bash

#===========================================
# OpenTCS Plus 一键部署脚本
#===========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

#===========================================
# 配置区域 - 根据实际情况修改
#===========================================

# 项目路径
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 服务版本
BACKEND_VERSION="latest"
FRONTEND_VERSION="latest"

# 基础配置
export MYSQL_ROOT_PASSWORD="root123"
export MYSQL_DATABASE="opentcs"
export MYSQL_USER="opentcs"
export MYSQL_PASSWORD="opentcs123"
export REDIS_PASSWORD="redis123"
export MINIO_USER="minioadmin"
export MINIO_PASSWORD="minioadmin123"

# 部署模式: all|infra|backend|frontend
DEPLOY_MODE="${1:-all}"

#===========================================
# 辅助函数
#===========================================

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# 检查 Docker 环境
check_docker() {
    log_step "检查 Docker 环境..."

    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装，请先安装 Docker: https://docs.docker.com/get-docker/"
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        log_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi

    # 启动 Docker 守护进程（如果未启动）
    if ! docker info &> /dev/null; then
        log_warn "Docker 守护进程未运行，尝试启动..."
        if [[ "$OSTYPE" == "darwin"* ]]; then
            log_info "macOS 用户请通过 Docker Desktop 启动 Docker"
            open -a Docker
            sleep 5
        else
            sudo systemctl start docker || sudo service docker start
        fi
    fi

    log_info "Docker 环境检查通过"
}

# 创建必要目录
create_directories() {
    log_step "创建配置目录..."

    mkdir -p "${DEPLOY_DIR}/config/mysql"
    mkdir -p "${DEPLOY_DIR}/config/redis"
    mkdir -p "${DEPLOY_DIR}/logs"

    # MySQL 配置
    if [ ! -f "${DEPLOY_DIR}/config/mysql/my.cnf" ]; then
        cat > "${DEPLOY_DIR}/config/mysql/my.cnf" << 'EOF'
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
max_connections=500
innodb_buffer_pool_size=256M
log_error=/var/log/mysql/error.log
slow_query_log=1
slow_query_log_file=/var/log/mysql/slow.log
long_query_time=2
EOF
    fi

    # Redis 配置
    if [ ! -f "${DEPLOY_DIR}/config/redis/redis.conf" ]; then
        cat > "${DEPLOY_DIR}/config/redis/redis.conf" << 'EOF'
bind 0.0.0.0
port 6379
databases 16
save 900 1
save 300 10
save 60 10000
appendonly yes
appendfsync everysec
maxmemory 512mb
maxmemory-policy allkeys-lru
EOF
    fi

    log_info "配置目录创建完成"
}

# 部署基础设施 (MySQL/Redis/MinIO)
deploy_infrastructure() {
    log_step "部署基础设施..."

    cd "${DEPLOY_DIR}"

    # 检查并停止旧容器
    docker compose down --remove-orphans 2>/dev/null || true

    # 只启动基础设施
    docker compose up -d mysql redis minio

    # 等待基础设施就绪
    log_info "等待 MySQL 就绪..."
    sleep 30
    for i in {1..30}; do
        if docker compose exec -T mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
            log_info "MySQL 已就绪"
            break
        fi
        sleep 2
    done

    log_info "等待 Redis 就绪..."
    for i in {1..15}; do
        if docker compose exec -T redis redis-cli -a "${REDIS_PASSWORD}" ping &>/dev/null; then
            log_info "Redis 已就绪"
            break
        fi
        sleep 2
    done

    log_info "基础设施部署完成"
}

# 部署后端
deploy_backend() {
    log_step "部署后端服务..."

    cd "${DEPLOY_DIR}"

    # 检查是否需要构建
    if [ ! -f "${PROJECT_ROOT}/opentcs-plus/opentcs-admin/target/opentcs-admin.jar" ]; then
        log_warn "后端 JAR 包不存在，先进行构建..."
        build_backend
    fi

    # 启动后端
    docker compose up -d backend

    # 等待后端启动
    log_info "等待后端服务启动..."
    for i in {1..60}; do
        if curl -sf http://localhost:8088/actuator/health &>/dev/null; then
            log_info "后端服务已就绪"
            return 0
        fi
        sleep 3
    done

    log_error "后端服务启动失败，请检查日志"
    docker compose logs backend
    return 1
}

# 部署前端
deploy_frontend() {
    log_step "部署前端服务..."

    cd "${DEPLOY_DIR}"

    # 启动前端
    docker compose up -d frontend

    # 等待前端启动
    log_info "等待前端服务启动..."
    for i in {1..15}; do
        if curl -sf http://localhost/health &>/dev/null; then
            log_info "前端服务已就绪"
            return 0
        fi
        sleep 2
    done

    log_error "前端服务启动失败，请检查日志"
    docker compose logs frontend
    return 1
}

# 构建后端
build_backend() {
    log_step "构建后端服务..."

    cd "${PROJECT_ROOT}/opentcs-plus"

    # Maven 构建
    if ! command -v mvn &> /dev/null; then
        log_error "Maven 未安装"
        exit 1
    fi

    mvn clean package -DskipTests -Pprod

    log_info "后端构建完成"
}

# 构建前端
build_frontend() {
    log_step "构建前端服务..."

    cd "${PROJECT_ROOT}/opentcs-plus-web"

    # 检查 Node.js
    if ! command -v node &> /dev/null; then
        log_error "Node.js 未安装"
        exit 1
    fi

    # 安装依赖
    pnpm install --frozen-lockfile --registry=https://registry.npmmirror.com

    # 构建
    pnpm build:prod

    log_info "前端构建完成"
}

# 部署所有服务
deploy_all() {
    log_step "开始完整部署..."

    # 创建目录
    create_directories

    # 部署基础设施
    deploy_infrastructure

    # 部署后端
    deploy_backend

    # 部署前端
    deploy_frontend

    # 显示部署信息
    show_deployment_info
}

# 显示部署信息
show_deployment_info() {
    local local_ip=$(hostname -I 2>/dev/null | awk '{print $1}' || echo "localhost")

    echo ""
    echo "============================================"
    echo "       OpenTCS Plus 部署完成！              "
    echo "============================================"
    echo ""
    echo "访问地址:"
    echo "  🌐 前端: http://${local_ip}:80"
    echo "  🔧 后端: http://${local_ip}:8088"
    echo "  📦 MinIO: http://${local_ip}:9000"
    echo "  📦 MinIO Console: http://${local_ip}:9001"
    echo ""
    echo "默认账号:"
    echo "  👤 用户名: admin"
    echo "  🔑 密码: admin123"
    echo ""
    echo "服务状态:"
    docker compose ps
    echo ""
    echo "常用命令:"
    echo "  查看日志: cd ${DEPLOY_DIR} && docker compose logs -f"
    echo "  重启服务: cd ${DEPLOY_DIR} && docker compose restart"
    echo "  停止服务: cd ${DEPLOY_DIR} && docker compose down"
    echo "  重新部署: cd ${DEPLOY_DIR} && ./scripts/deploy.sh"
    echo "============================================"
}

# 显示帮助
show_help() {
    cat << EOF
用法: $(basename "$0") [模式]

模式:
  all         部署所有服务 (默认)
  infra       只部署基础设施 (MySQL/Redis/MinIO)
  backend     只部署后端服务
  frontend    只部署前端服务
  build       构建所有镜像
  build:be    只构建后端镜像
  build:fe    只构建前端镜像
  stop        停止所有服务
  restart     重启所有服务
  logs        查看日志
  status      查看服务状态
  clean       清理所有容器和数据

示例:
  $(basename "$0")              # 部署所有服务
  $(basename "$0") infra        # 只部署基础设施
  $(basename "$0") logs         # 查看日志
  $(basename "$0") clean        # 清理环境

EOF
}

#===========================================
# 主逻辑
#===========================================

case "$DEPLOY_MODE" in
    all)
        check_docker
        deploy_all
        ;;
    infra)
        check_docker
        create_directories
        deploy_infrastructure
        ;;
    backend)
        check_docker
        deploy_backend
        ;;
    frontend)
        check_docker
        deploy_frontend
        ;;
    build)
        build_backend
        build_frontend
        ;;
    build:be)
        build_backend
        ;;
    build:fe)
        build_frontend
        ;;
    stop)
        cd "${DEPLOY_DIR}" && docker compose stop
        ;;
    restart)
        cd "${DEPLOY_DIR}" && docker compose restart
        ;;
    logs)
        cd "${DEPLOY_DIR}" && docker compose logs -f
        ;;
    status)
        cd "${DEPLOY_DIR}" && docker compose ps
        ;;
    clean)
        cd "${DEPLOY_DIR}" && docker compose down -v
        log_info "已清理所有容器和数据"
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        log_error "未知模式: $DEPLOY_MODE"
        show_help
        exit 1
        ;;
esac

log_info "操作完成"
