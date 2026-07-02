#!/bin/bash
# ============================================================
# OpenTCS Plus 一键安装脚本
# 在目标服务器 (Ubuntu 20.04 x86) 上运行
#
# 用法:
#   ./install.sh          # 首次安装
#   ./install.sh upgrade  # 升级（保留数据）
#   ./install.sh uninstall # 卸载（保留数据卷）
#   ./install.sh purge    # 完全卸载（删除所有数据）
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
IMAGES_DIR="$SCRIPT_DIR/images"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"
ENV_FILE="$SCRIPT_DIR/.env"
VERSION_FILE="$SCRIPT_DIR/VERSION"

# 颜色
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
log_info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}===== $1 =====${NC}\n"; }

# 读取版本
VERSION=$(grep '^version=' "$VERSION_FILE" 2>/dev/null | cut -d= -f2 || echo "unknown")

# ── 系统检查 ──────────────────────────────────────────────

check_system() {
    log_step "系统环境检查"

    # 架构检查
    local arch
    arch=$(uname -m)
    if [ "$arch" != "x86_64" ]; then
        log_error "不支持的架构: $arch (需要 x86_64)"
        exit 1
    fi

    # 系统版本检查（宽松：Ubuntu 20.04+ 或其他 Linux 均可）
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        log_info "操作系统: $PRETTY_NAME"
    fi

    # 内存检查 (建议 >= 4GB)
    local mem_kb
    mem_kb=$(grep MemTotal /proc/meminfo | awk '{print $2}')
    local mem_gb=$(( mem_kb / 1024 / 1024 ))
    if [ "$mem_gb" -lt 2 ]; then
        log_warn "可用内存 ${mem_gb}GB，建议至少 4GB"
    else
        log_info "内存: ${mem_gb}GB"
    fi

    # 磁盘检查 (建议 >= 20GB)
    local disk_avail
    disk_avail=$(df -BG /opt 2>/dev/null | tail -1 | awk '{print $4}' | tr -d 'G' || echo 0)
    if [ "${disk_avail:-0}" -lt 10 ]; then
        log_warn "磁盘可用空间 ${disk_avail}GB，建议至少 20GB"
    else
        log_info "磁盘可用: ${disk_avail}GB"
    fi

    log_info "系统检查通过"
}

# ── Docker 安装 ───────────────────────────────────────────

install_docker() {
    if command -v docker &>/dev/null && docker info &>/dev/null; then
        log_info "Docker 已安装: $(docker --version)"
        return 0
    fi

    log_step "安装 Docker Engine"

    # 检测是否可以用 apt
    if ! command -v apt-get &>/dev/null; then
        log_error "仅支持 apt 包管理器（Ubuntu/Debian），请手动安装 Docker"
        exit 1
    fi

    log_info "安装依赖..."
    apt-get update -qq
    apt-get install -y -qq ca-certificates curl gnupg lsb-release

    log_info "添加 Docker GPG key..."
    mkdir -p /etc/apt/keyrings
    curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | \
        gpg --dearmor -o /etc/apt/keyrings/docker.gpg

    log_info "添加 Docker 软件源..."
    echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/docker.gpg] \
https://mirrors.aliyun.com/docker-ce/linux/ubuntu \
$(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list

    apt-get update -qq
    apt-get install -y -qq docker-ce docker-ce-cli containerd.io docker-compose-plugin

    systemctl enable docker
    systemctl start docker

    log_info "Docker 安装完成: $(docker --version)"
}

# ── docker compose 命令封装 ───────────────────────────────

compose() {
    if command -v docker-compose &>/dev/null; then
        docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" "$@"
    else
        docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" "$@"
    fi
}

# ── 初始化 .env ───────────────────────────────────────────

init_env() {
    if [ -f "$ENV_FILE" ]; then
        log_info ".env 文件已存在，跳过创建"
        return 0
    fi

    log_step "初始化配置文件"

    if [ -f "$SCRIPT_DIR/.env.example" ]; then
        cp "$SCRIPT_DIR/.env.example" "$ENV_FILE"
    else
        cat > "$ENV_FILE" <<'EOF'
# OpenTCS Plus 环境变量配置
# 生产环境请修改以下密码

# MySQL
MYSQL_ROOT_PASSWORD=MySQL@2024!Root
MYSQL_DATABASE=opentcs
MYSQL_USER=opentcs
MYSQL_PASSWORD=Opentcs@2024!DB
MYSQL_PORT=3306

# Redis
REDIS_PASSWORD=Redis@2024!Cache
REDIS_PORT=6379

# EMQX (MQTT Broker)
EMQX_DASHBOARD_USER=admin
EMQX_DASHBOARD_PASSWORD=Emqx@2024!Admin
MQTT_PORT=1883
MQTT_WS_PORT=8083
EMQX_DASHBOARD_PORT=18083

# MQTT 应用账号 (后端连接 EMQX 使用)
MQTT_USERNAME=opentcs
MQTT_PASSWORD=Opentcs@2024!MQTT
MQTT_ENABLED=true

# 后端
BACKEND_PORT=8088

# 前端
FRONTEND_PORT=80

# 应用版本 (与镜像 tag 对应)
APP_VERSION=latest
EOF
    fi

    log_info ".env 已创建，路径: $ENV_FILE"
    log_warn "请检查并修改 $ENV_FILE 中的密码后继续"
}

# ── 加载 Docker 镜像 ──────────────────────────────────────

load_images() {
    log_step "加载 Docker 镜像"

    if [ ! -d "$IMAGES_DIR" ]; then
        log_error "镜像目录不存在: $IMAGES_DIR"
        exit 1
    fi

    local count=0
    for img_file in "$IMAGES_DIR"/*.tar.gz; do
        [ -f "$img_file" ] || continue
        log_info "加载: $(basename "$img_file")..."
        docker load < "$img_file"
        count=$((count + 1))
    done

    if [ "$count" -eq 0 ]; then
        log_error "未找到镜像文件 (*.tar.gz) 在 $IMAGES_DIR"
        exit 1
    fi

    log_info "已加载 $count 个镜像"
}

# ── 创建数据目录 ──────────────────────────────────────────

setup_directories() {
    log_step "创建数据目录"
    mkdir -p \
        "$SCRIPT_DIR/logs/backend" \
        "$SCRIPT_DIR/logs/emqx" \
        "$SCRIPT_DIR/data/mysql" \
        "$SCRIPT_DIR/data/redis" \
        "$SCRIPT_DIR/data/emqx" \
        "$SCRIPT_DIR/upload"
    log_info "数据目录已创建"
}

# ── 等待服务健康 ──────────────────────────────────────────

wait_healthy() {
    local service="$1"
    local max_wait="${2:-120}"
    local elapsed=0

    log_info "等待 $service 就绪..."
    while [ $elapsed -lt $max_wait ]; do
        local status
        status=$(compose ps --format json "$service" 2>/dev/null | \
            python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('Health',''))" 2>/dev/null || echo "")

        if [ "$status" = "healthy" ]; then
            log_info "$service 已就绪"
            return 0
        fi

        # 兼容旧版 docker compose
        if compose exec -T "$service" true &>/dev/null 2>&1; then
            sleep 3
            elapsed=$((elapsed + 3))
            printf "."
        else
            sleep 3
            elapsed=$((elapsed + 3))
        fi
    done
    echo ""
    log_warn "$service 健康检查超时 (${max_wait}s)，请查看日志"
}

# ── 启动服务 ──────────────────────────────────────────────

start_services() {
    log_step "启动所有服务"

    # 先启动中间件
    log_info "启动中间件 (MySQL + Redis + EMQX)..."
    compose up -d mysql redis emqx

    log_info "等待 MySQL 就绪 (最长 120s)..."
    for i in $(seq 1 24); do
        if compose exec -T mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
            log_info "MySQL 已就绪"
            break
        fi
        if [ "$i" -eq 24 ]; then
            log_warn "MySQL 启动超时，请检查: docker logs opentcs-mysql"
        fi
        sleep 5
    done

    log_info "等待 Redis 就绪..."
    local redis_pass
    redis_pass=$(grep REDIS_PASSWORD "$ENV_FILE" | cut -d= -f2 | tr -d '"')
    for i in $(seq 1 12); do
        if compose exec -T redis redis-cli -a "$redis_pass" ping 2>/dev/null | grep -q PONG; then
            log_info "Redis 已就绪"
            break
        fi
        sleep 5
    done

    log_info "等待 EMQX 就绪 (最长 90s)..."
    for i in $(seq 1 18); do
        if compose exec -T emqx /opt/emqx/bin/emqx ctl status 2>/dev/null | grep -q "is running"; then
            log_info "EMQX 已就绪"
            break
        fi
        sleep 5
    done

    log_info "执行数据库迁移 (Flyway)..."
    if [[ -x "$SCRIPT_DIR/flyway-migrate.sh" ]]; then
        (cd "$SCRIPT_DIR" && ./flyway-migrate.sh) || {
            log_error "Flyway 迁移失败，请检查 db/migration 与 MySQL 日志"
            exit 1
        }
    elif compose config --services 2>/dev/null | grep -qx flyway; then
        compose run --rm flyway || {
            log_error "Flyway 迁移失败"
            exit 1
        }
    else
        log_warn "未找到 Flyway 迁移脚本，跳过"
    fi

    # 启动应用服务
    log_info "启动后端服务..."
    compose up -d backend

    log_info "等待后端就绪 (最长 120s)..."
    for i in $(seq 1 24); do
        if curl -sf http://localhost:8088/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
            log_info "后端已就绪"
            break
        fi
        if [ "$i" -eq 24 ]; then
            log_warn "后端健康检查超时，请检查: docker logs opentcs-admin"
        fi
        sleep 5
    done

    log_info "启动前端服务..."
    compose up -d frontend

    log_info "全部服务已启动"
}

# ── 显示访问信息 ──────────────────────────────────────────

show_info() {
    local ip
    ip=$(hostname -I 2>/dev/null | awk '{print $1}' || echo "your-server-ip")

    echo ""
    echo "  ╔══════════════════════════════════════════════════════╗"
    echo "  ║         OpenTCS Plus 安装完成！                      ║"
    echo "  ║         版本: $VERSION"
    echo "  ╚══════════════════════════════════════════════════════╝"
    echo ""
    echo "  访问地址:"
    echo "    🌐 前端界面:     http://${ip}"
    echo "    🔧 后端 API:     http://${ip}:8088"
    echo "    📡 EMQX 控制台:  http://${ip}:18083"
    echo "    📡 MQTT TCP:     ${ip}:1883"
    echo "    📡 MQTT WS:      ${ip}:8083"
    echo ""
    echo "  默认账号:"
    echo "    系统登录:    admin / admin123"
    echo "    EMQX 控制台: admin / (见 .env EMQX_DASHBOARD_PASSWORD)"
    echo ""
    echo "  常用命令:"
    echo "    查看状态: cd $SCRIPT_DIR && docker compose -f docker-compose.yml ps"
    echo "    查看日志: docker compose -f $COMPOSE_FILE logs -f [backend|frontend|mysql|redis|emqx]"
    echo "    停止服务: docker compose -f $COMPOSE_FILE down"
    echo "    重启服务: docker compose -f $COMPOSE_FILE restart"
    echo ""
}

# ── 主逻辑 ────────────────────────────────────────────────

cmd_install() {
    echo ""
    echo "  ╔════════════════════════════════════════╗"
    echo "  ║   OpenTCS Plus 一键安装                ║"
    echo "  ║   版本: $VERSION"
    echo "  ╚════════════════════════════════════════╝"
    echo ""

    check_system
    install_docker
    setup_directories
    init_env
    load_images
    start_services
    show_info
}

cmd_upgrade() {
    log_step "升级 OpenTCS Plus (保留数据)"
    load_images
    compose pull 2>/dev/null || true

    log_info "执行数据库迁移 (Flyway)..."
    if [[ -x "$SCRIPT_DIR/flyway-migrate.sh" ]]; then
        (cd "$SCRIPT_DIR" && ./flyway-migrate.sh)
    elif compose config --services 2>/dev/null | grep -qx flyway; then
        compose run --rm flyway
    fi

    compose up -d --no-deps backend frontend
    log_info "升级完成"
    show_info
}

cmd_uninstall() {
    log_warn "停止并删除容器（数据卷保留）"
    read -r -p "确认？(yes/no): " confirm
    [ "$confirm" = "yes" ] || { log_info "已取消"; exit 0; }
    compose down
    log_info "容器已删除，数据卷保留"
}

cmd_purge() {
    log_warn "完全卸载，所有数据将被删除！"
    read -r -p "确认删除所有数据？(yes/no): " confirm
    [ "$confirm" = "yes" ] || { log_info "已取消"; exit 0; }
    compose down -v
    log_info "所有容器和数据卷已删除"
}

case "${1:-install}" in
    install)   cmd_install ;;
    upgrade)   cmd_upgrade ;;
    uninstall) cmd_uninstall ;;
    purge)     cmd_purge ;;
    status)    compose ps ;;
    logs)      compose logs -f "${2:-}" ;;
    restart)   compose restart "${2:-}" ;;
    stop)      compose stop ;;
    start)     compose up -d ;;
    *)
        echo "用法: $0 [install|upgrade|uninstall|purge|status|logs|restart|stop|start]"
        exit 1
        ;;
esac
