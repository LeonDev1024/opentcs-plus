#!/bin/bash
# ============================================================
# OpenTCS Plus MVP 一键启动脚本
# 用法：./mvp-start.sh [build|start|stop|restart|status|logs]
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(dirname "$(dirname "$DEPLOY_DIR")")"

COMPOSE_FILE="$DEPLOY_DIR/docker-compose.yml"
ENV_FILE="$DEPLOY_DIR/.env"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info()    { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()    { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $1"; }
log_header()  { echo -e "\n${BLUE}========== $1 ==========${NC}\n"; }

# 检查依赖
check_deps() {
    for cmd in docker docker-compose; do
        if ! command -v "$cmd" &>/dev/null; then
            # docker compose v2 作为 docker 子命令
            if [ "$cmd" = "docker-compose" ] && docker compose version &>/dev/null; then
                continue
            fi
            log_error "$cmd 未安装，请先安装 Docker Desktop"
            exit 1
        fi
    done
}

# 兼容 docker compose v1/v2
compose() {
    if command -v docker-compose &>/dev/null; then
        docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" "$@"
    else
        docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" "$@"
    fi
}

# 初始化 .env 文件
init_env() {
    if [ ! -f "$ENV_FILE" ]; then
        if [ -f "$DEPLOY_DIR/.env.example" ]; then
            cp "$DEPLOY_DIR/.env.example" "$ENV_FILE"
            log_info "已从 .env.example 创建 .env，请检查配置"
        else
            cat > "$ENV_FILE" << 'EOF'
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=opentcs
MYSQL_USER=opentcs
MYSQL_PASSWORD=opentcs123
REDIS_PASSWORD=redis123
MINIO_USER=minioadmin
MINIO_PASSWORD=minioadmin123
EOF
            log_warn "已创建默认 .env，生产环境请修改密码"
        fi
    fi
}

# 构建镜像
cmd_build() {
    log_header "构建 Docker 镜像"
    log_info "构建后端（约 5-10 分钟）..."
    compose build --no-cache backend
    log_info "构建前端（约 2-5 分钟）..."
    compose build --no-cache frontend
    log_info "构建完成"
}

# 启动服务
cmd_start() {
    log_header "启动 OpenTCS Plus MVP"

    init_env

    log_info "启动基础设施（MySQL + Redis + MinIO）..."
    compose up -d mysql redis minio

    log_info "等待 MySQL 就绪（最长 60 秒）..."
    for i in $(seq 1 12); do
        if compose exec -T mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
            log_info "MySQL 已就绪"
            break
        fi
        if [ "$i" -eq 12 ]; then
            log_error "MySQL 启动超时，请检查日志：docker logs opentcs-mysql"
            exit 1
        fi
        sleep 5
    done

    log_info "启动后端服务..."
    compose up -d backend

    log_info "等待后端就绪（最长 90 秒）..."
    for i in $(seq 1 18); do
        if curl -sf http://localhost:8088/actuator/health | grep -q '"status":"UP"' 2>/dev/null; then
            log_info "后端已就绪"
            break
        fi
        if [ "$i" -eq 18 ]; then
            log_warn "后端健康检查超时，请检查日志：docker logs opentcs-admin"
        fi
        sleep 5
    done

    log_info "启动前端服务..."
    compose up -d frontend

    log_header "启动完成"
    echo ""
    log_info "访问地址：http://localhost"
    log_info "后端 API：http://localhost:8088"
    log_info "MinIO 控制台：http://localhost:9001"
    echo ""
    log_info "默认账号：admin / admin123"
    echo ""
    log_info "查看日志：$0 logs"
}

# 停止服务
cmd_stop() {
    log_header "停止 OpenTCS Plus"
    compose down
    log_info "已停止所有服务"
}

# 重启服务
cmd_restart() {
    cmd_stop
    cmd_start
}

# 查看状态
cmd_status() {
    log_header "服务状态"
    compose ps
    echo ""
    log_info "后端健康："
    curl -sf http://localhost:8088/actuator/health 2>/dev/null | python3 -m json.tool 2>/dev/null || echo "  无法连接"
}

# 查看日志
cmd_logs() {
    SERVICE="${2:-}"
    if [ -n "$SERVICE" ]; then
        compose logs -f "$SERVICE"
    else
        compose logs -f --tail=100
    fi
}

# 清理数据（危险操作）
cmd_clean() {
    log_warn "将删除所有容器和数据卷！"
    read -r -p "确认删除？(yes/no): " confirm
    if [ "$confirm" = "yes" ]; then
        compose down -v
        log_info "已清理所有数据"
    else
        log_info "操作已取消"
    fi
}

# 主入口
check_deps

case "${1:-start}" in
    build)   cmd_build ;;
    start)   cmd_start ;;
    stop)    cmd_stop ;;
    restart) cmd_restart ;;
    status)  cmd_status ;;
    logs)    cmd_logs "$@" ;;
    clean)   cmd_clean ;;
    *)
        echo "用法: $0 [build|start|stop|restart|status|logs [service]|clean]"
        echo ""
        echo "命令说明:"
        echo "  build    构建 Docker 镜像（首次部署或代码变更后执行）"
        echo "  start    启动所有服务（默认）"
        echo "  stop     停止所有服务"
        echo "  restart  重启所有服务"
        echo "  status   查看服务状态和健康检查"
        echo "  logs     查看日志（可指定服务：backend/frontend/mysql/redis）"
        echo "  clean    清理所有容器和数据（危险！）"
        exit 1
        ;;
esac
