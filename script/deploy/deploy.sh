#!/bin/bash
# ============================================================
# OpenTCS Plus 统一部署脚本
#
# 开发机构建:
#   ./deploy.sh quick user@host   简易发布：上传 jar+dist 热更新（推荐日常）
#   ./deploy.sh build [版本号]   编译并生成离线部署包（首次/换机）
#   ./deploy.sh up               本地构建镜像并启动完整栈
#
# 服务器运维:
#   ./deploy.sh up               启动所有服务
#   ./deploy.sh down             停止所有服务
#   ./deploy.sh restart          重启
#   ./deploy.sh status           查看状态
#   ./deploy.sh logs [服务名]    查看日志
#   ./deploy.sh health           健康检查
# ============================================================

set -euo pipefail

DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCRIPTS_DIR="$DEPLOY_DIR/scripts"
PROJECT_ROOT="$(cd "$DEPLOY_DIR/../.." && pwd)"
FRONTEND_DIR="$(cd "$PROJECT_ROOT/../opentcs-plus-web" 2>/dev/null && pwd || echo "")"
COMPOSE_FILE="$DEPLOY_DIR/docker-compose.yml"
ENV_FILE="$DEPLOY_DIR/.env"
ENV_EXAMPLE="$DEPLOY_DIR/.env.example"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
log_info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}===== $1 =====${NC}\n"; }

compose() {
    docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" "$@"
}

ensure_env() {
    if [ ! -f "$ENV_FILE" ]; then
        cp "$ENV_EXAMPLE" "$ENV_FILE"
        log_info "已从 .env.example 创建 .env"
    fi
}

ensure_docker() {
    if ! command -v docker &>/dev/null || ! docker info &>/dev/null; then
        log_error "Docker 未运行，请先启动 Docker"
        exit 1
    fi
}

build_images() {
    local version="${1:-latest}"

    log_step "构建后端镜像 (Docker 内 JDK 21 编译)"
    DOCKER_BUILDKIT=1 docker build \
        --platform linux/amd64 \
        --provenance=false \
        --sbom=false \
        -f script/deploy/backend/Dockerfile \
        -t "opentcs-admin:${version}" \
        -t "opentcs-admin:latest" \
        "$PROJECT_ROOT"

    if [[ -n "$FRONTEND_DIR" && -d "$FRONTEND_DIR" ]]; then
        log_step "构建前端镜像"
        cd "$FRONTEND_DIR"
        npm ci --registry=https://registry.npmmirror.com
        npm run build:prod
        rm -rf "$DEPLOY_DIR/frontend/dist"
        cp -r dist "$DEPLOY_DIR/frontend/dist"
        cd "$DEPLOY_DIR/frontend"
        DOCKER_BUILDKIT=1 docker build \
            --platform linux/amd64 \
            --provenance=false \
            --sbom=false \
            -t "opentcs-web:${version}" \
            -t "opentcs-web:latest" \
            .
        rm -rf "$DEPLOY_DIR/frontend/dist"
    else
        log_warn "未找到 opentcs-plus-web，跳过前端镜像"
    fi

    if [ "$version" != "latest" ]; then
        sed -i.bak "s/^APP_VERSION=.*/APP_VERSION=${version}/" "$ENV_FILE" 2>/dev/null || \
        sed -i '' "s/^APP_VERSION=.*/APP_VERSION=${version}/" "$ENV_FILE"
        rm -f "$ENV_FILE.bak"
    fi
}

cmd_pull() {
    exec "$SCRIPTS_DIR/pull-images.sh"
}

cmd_quick() {
    exec "$SCRIPTS_DIR/quick-deploy.sh" "${1:-}"
}

cmd_build() {
    exec "$SCRIPTS_DIR/build-package.sh" "${1:-}"
}

cmd_up() {
    ensure_docker
    ensure_env

    local app_version
    app_version=$(awk -F= '/^APP_VERSION=/{print $2; exit}' "$ENV_FILE")
    app_version="${app_version:-latest}"

    # 开发目录允许自动构建；离线部署目录只使用安装包内的版本镜像。
    if ! docker image inspect "opentcs-admin:${app_version}" &>/dev/null; then
        if [[ -f "$PROJECT_ROOT/pom.xml" ]]; then
            log_warn "未找到后端镜像 opentcs-admin:${app_version}，开始自动构建..."
            build_images "$app_version"
        else
            log_error "未找到后端镜像 opentcs-admin:${app_version}，请先执行 ./install.sh 加载离线镜像"
            exit 1
        fi
    fi

    log_step "启动 OpenTCS Plus 完整栈"
    compose up -d mysql redis emqx

    log_info "等待 MySQL..."
    for i in $(seq 1 24); do
        compose exec -T mysql mysqladmin ping -h localhost --silent 2>/dev/null && break
        sleep 5
    done

    if docker network inspect opentcs-net &>/dev/null && [[ -x "$SCRIPTS_DIR/flyway-migrate.sh" ]]; then
        DOCKER_NETWORK=opentcs-net "$SCRIPTS_DIR/flyway-migrate.sh"
    else
        compose run --rm flyway
    fi

    compose up -d backend frontend
    cmd_status
    show_access_info
}

cmd_down() {
    ensure_env
    compose down
    log_info "所有服务已停止"
}

cmd_restart() {
    cmd_down
    cmd_up
}

cmd_status() {
    ensure_env
    compose ps
}

cmd_logs() {
    ensure_env
    compose logs -f "${1:-}"
}

cmd_health() {
    ensure_env
    local ip="${SERVER_IP:-localhost}"
    local ok=0

    echo "OpenTCS Plus 健康检查 - $(date '+%Y-%m-%d %H:%M:%S')"
    echo ""

    for svc in "MySQL:${MYSQL_PORT:-3306}" "Redis:${REDIS_PORT:-6379}" "MQTT:${MQTT_PORT:-1883}" "EMQX Dashboard:${EMQX_DASHBOARD_PORT:-18083}" "Backend:${BACKEND_PORT:-8088}" "Frontend:${FRONTEND_PORT:-80}"; do
        local name="${svc%%:*}" port="${svc##*:}"
        if timeout 3 bash -c "echo >/dev/tcp/${ip}/${port}" 2>/dev/null; then
            log_info "$name (:$port) OK"
        else
            log_error "$name (:$port) 不可达"
            ok=1
        fi
    done

    curl -sf "http://${ip}:8088/actuator/health" | grep -q UP && log_info "后端健康检查 OK" || { log_error "后端健康检查失败"; ok=1; }
    curl -sf "http://${ip}/health" | grep -q OK && log_info "前端健康检查 OK" || { log_error "前端健康检查失败"; ok=1; }

    return $ok
}

show_access_info() {
    local ip
    ip=$(hostname -I 2>/dev/null | awk '{print $1}' || echo "localhost")
    echo ""
    echo "  前端:         http://${ip}"
    echo "  后端 API:     http://${ip}:8088"
    echo "  MQTT Broker:  tcp://${ip}:${MQTT_PORT:-1883}"
    echo "  EMQX 控制台:  http://${ip}:${EMQX_DASHBOARD_PORT:-18083}"
    echo ""
}

show_help() {
    cat <<EOF
OpenTCS Plus 统一部署脚本

用法: $0 <命令> [参数]

命令:
  quick user@host  简易发布：本地编译后上传 jar+dist，服务器热更新（日常推荐）
  pull             预拉取打包所需 Docker 镜像（网络好时先执行）
  build [版本]     编译前后端并生成离线部署包 (首次/换机)
  up               构建镜像(如需)并启动完整 Docker 栈
  down             停止所有服务
  restart          重启所有服务
  status           查看服务状态
  logs [服务]      查看日志 (mysql/redis/emqx/backend/frontend)
  health           健康检查
  help             显示帮助

日常发布:
  $0 quick ubuntu@106.54.43.41
  SKIP_BUILD=1 $0 quick ubuntu@106.54.43.41   # 跳过编译，只上传已有产物

首次离线部署:
  $0 build 2.0.1
  scp dist/opentcs-plus-2.0.1-linux-amd64.tar.gz server:/opt/
  ssh server "cd /opt && tar xzf opentcs-plus-2.0.1-linux-amd64.tar.gz && cd opentcs-plus-2.0.1-linux-amd64 && ./install.sh"

EOF
}

case "${1:-help}" in
    quick)   cmd_quick "${2:-}" ;;
    pull)    cmd_pull ;;
    build)   cmd_build "${2:-}" ;;
    up)      cmd_up ;;
    down)    cmd_down ;;
    restart) cmd_restart ;;
    status)  cmd_status ;;
    logs)    cmd_logs "${2:-}" ;;
    health)  cmd_health ;;
    help|-h|--help) show_help ;;
    *)
        log_error "未知命令: $1"
        show_help
        exit 1
        ;;
esac
