#!/bin/bash
# ============================================================
# OpenTCS Plus 一键安装脚本 (离线部署包内使用)
#
# 用法:
#   ./install.sh          # 首次安装
#   ./install.sh upgrade  # 升级（保留数据）
#   ./install.sh uninstall
#   ./install.sh purge
# ============================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
IMAGES_DIR="$SCRIPT_DIR/images"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"
ENV_FILE="$SCRIPT_DIR/.env"
VERSION_FILE="$SCRIPT_DIR/VERSION"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
log_info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}===== $1 =====${NC}\n"; }

VERSION=$(grep '^version=' "$VERSION_FILE" 2>/dev/null | cut -d= -f2 || echo "unknown")
APP_VERSION="${VERSION}"

compose() {
    if command -v docker-compose &>/dev/null; then
        docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" "$@"
    else
        docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" "$@"
    fi
}

check_system() {
    log_step "系统环境检查"
    local arch
    arch=$(uname -m)
    if [ "$arch" != "x86_64" ]; then
        log_error "不支持的架构: $arch (需要 x86_64)"
        exit 1
    fi
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        log_info "操作系统: $PRETTY_NAME"
    fi
    log_info "系统检查通过"
}

require_docker() {
    command -v docker &>/dev/null || {
        log_error "未安装 Docker；离线安装前请准备 Docker Engine 20.10+ 和 Docker Compose"
        exit 1
    }
    docker info &>/dev/null || {
        log_error "Docker daemon 未运行或当前用户无访问权限"
        exit 1
    }
    if ! docker compose version &>/dev/null && ! command -v docker-compose &>/dev/null; then
        log_error "未安装 Docker Compose"
        exit 1
    fi
    log_info "Docker 已就绪: $(docker --version)"
}

init_env() {
    if [ -f "$ENV_FILE" ]; then
        log_info ".env 已存在"
        return 0
    fi

    log_step "初始化 .env"
    if [ -f "$SCRIPT_DIR/.env.example" ]; then
        cp "$SCRIPT_DIR/.env.example" "$ENV_FILE"
    fi

    if [ "$APP_VERSION" != "unknown" ]; then
        sed -i.bak "s/^APP_VERSION=.*/APP_VERSION=${APP_VERSION}/" "$ENV_FILE" 2>/dev/null || \
        sed -i '' "s/^APP_VERSION=.*/APP_VERSION=${APP_VERSION}/" "$ENV_FILE"
        rm -f "$ENV_FILE.bak"
    fi

    log_warn "生产环境请修改 $ENV_FILE 中的密码"
}

load_images() {
    log_step "加载 Docker 镜像"
    local count=0
    for img_file in "$IMAGES_DIR"/*.tar.gz; do
        [ -f "$img_file" ] || continue
        log_info "加载: $(basename "$img_file")"
        gunzip -c "$img_file" | docker load
        count=$((count + 1))
    done
    [ "$count" -gt 0 ] || { log_error "未找到镜像文件"; exit 1; }
    log_info "已加载 $count 个镜像"
}

wait_mysql() {
    log_info "等待 MySQL 就绪..."
    for i in $(seq 1 24); do
        if compose exec -T mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
            log_info "MySQL 已就绪"
            return 0
        fi
        sleep 5
    done
    log_error "MySQL 启动超时，请执行 ./deploy.sh logs mysql 查看日志"
    return 1
}

wait_redis() {
    local redis_pass
    redis_pass=$(grep '^REDIS_PASSWORD=' "$ENV_FILE" | cut -d= -f2 | tr -d '"')
    log_info "等待 Redis 就绪..."
    for i in $(seq 1 12); do
        if compose exec -T redis redis-cli -a "$redis_pass" ping 2>/dev/null | grep -q PONG; then
            log_info "Redis 已就绪"
            return 0
        fi
        sleep 5
    done
    log_error "Redis 启动超时，请执行 ./deploy.sh logs redis 查看日志"
    return 1
}

wait_emqx() {
    log_info "等待 EMQX 就绪..."
    for i in $(seq 1 36); do
        if compose exec -T emqx /usr/lib/emqx/bin/emqx ctl status 2>/dev/null | grep -qE 'is started|is running'; then
            log_info "EMQX 已就绪"
            return 0
        fi
        if compose ps emqx 2>/dev/null | grep -qE '\(healthy\)|healthy'; then
            log_info "EMQX 已就绪 (healthcheck)"
            return 0
        fi
        sleep 5
    done
    log_error "EMQX 启动超时，请执行 ./deploy.sh logs emqx 查看日志"
    return 1
}

run_flyway() {
    log_info "执行数据库迁移 (Flyway)..."
    if [[ -x "$SCRIPT_DIR/flyway-migrate.sh" ]]; then
        DOCKER_NETWORK=opentcs-net "$SCRIPT_DIR/flyway-migrate.sh" || exit 1
    else
        compose run --rm flyway || exit 1
    fi
}

start_services() {
    log_step "启动所有服务"

    compose up -d mysql redis emqx
    wait_mysql
    wait_redis
    wait_emqx

    run_flyway

    compose up -d backend

    log_info "等待后端就绪..."
    local backend_ready=0
    for i in $(seq 1 24); do
        if curl -sf "http://localhost:${BACKEND_PORT:-8088}/actuator/health" 2>/dev/null | grep -q '"status":"UP"'; then
            log_info "后端已就绪"
            backend_ready=1
            break
        fi
        sleep 5
    done
    [ "$backend_ready" -eq 1 ] || {
        log_error "后端启动超时，请执行 ./deploy.sh logs backend 查看日志"
        return 1
    }

    compose up -d frontend
    log_info "全部服务已启动"
}

show_info() {
    local ip
    ip=$(hostname -I 2>/dev/null | awk '{print $1}' || echo "localhost")

    echo ""
    echo "  ╔══════════════════════════════════════════════════════╗"
    echo "  ║         OpenTCS Plus 部署完成                         ║"
    echo "  ║         版本: $VERSION"
    echo "  ╚══════════════════════════════════════════════════════╝"
    echo ""
    echo "  访问地址:"
    echo "    前端界面:      http://${ip}"
    echo "    后端 API:      http://${ip}:8088"
    echo "    MQTT Broker:   tcp://${ip}:${MQTT_PORT:-1883}"
    echo "    MQTT WebSocket: ws://${ip}:${MQTT_WS_PORT:-8083}/mqtt"
    echo "    EMQX 控制台:   http://${ip}:${EMQX_DASHBOARD_PORT:-18083}"
    echo ""
    echo "  默认账号:"
    echo "    系统登录:  admin / admin123"
    echo ""
    echo "  常用命令:"
    echo "    ./deploy.sh status"
    echo "    ./deploy.sh logs backend"
    echo "    ./deploy.sh down"
    echo ""
}

cmd_install() {
    echo ""
    echo "  ╔════════════════════════════════════════╗"
    echo "  ║   OpenTCS Plus 一键安装                ║"
    echo "  ╚════════════════════════════════════════╝"
    echo ""

    check_system
    require_docker
    init_env
    load_images
    start_services
    show_info
}

cmd_upgrade() {
    log_step "升级 (保留数据)"
    init_env
    load_images
    compose up -d emqx
    wait_emqx
    run_flyway
    compose up -d --no-deps backend frontend
    show_info
}

cmd_uninstall() {
    read -r -p "停止并删除容器（保留数据卷）? (yes/no): " confirm
    [ "$confirm" = "yes" ] || exit 0
    compose down
}

cmd_purge() {
    read -r -p "完全卸载并删除所有数据? (yes/no): " confirm
    [ "$confirm" = "yes" ] || exit 0
    compose down -v
}

case "${1:-install}" in
    install)   cmd_install ;;
    upgrade)   cmd_upgrade ;;
    uninstall) cmd_uninstall ;;
    purge)     cmd_purge ;;
    *)         echo "用法: $0 [install|upgrade|uninstall|purge]"; exit 1 ;;
esac
