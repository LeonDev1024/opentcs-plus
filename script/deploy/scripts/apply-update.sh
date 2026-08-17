#!/bin/bash
# ============================================================
# 服务器端热更新：用本地上传的 jar + dist 替换运行中的容器内容
#
# 用法（在更新包目录内执行）:
#   ./apply-update.sh
#
# 目录结构:
#   app.jar
#   dist/          # 前端构建产物
#   nginx.conf     # 可选，前端 nginx 配置
#   db/migration/  # 可选，有新迁移时执行 Flyway
# ============================================================

set -euo pipefail

UPDATE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_CTR="${BACKEND_CTR:-opentcs-admin}"
FRONTEND_CTR="${FRONTEND_CTR:-opentcs-web}"
BACKEND_PORT="${BACKEND_PORT:-8088}"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
log_info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}===== $1 =====${NC}\n"; }
die()       { log_error "$1"; exit 1; }

need_ctr() {
    docker inspect "$1" &>/dev/null || die "容器不存在: $1（请先完成首次 Docker 安装）"
}

update_backend() {
    log_step "更新后端 JAR"
    local jar="$UPDATE_DIR/app.jar"
    [ -f "$jar" ] || die "缺少 app.jar"
    need_ctr "$BACKEND_CTR"

    docker cp "$jar" "$BACKEND_CTR:/app/app.jar"
    docker restart "$BACKEND_CTR" >/dev/null
    log_info "已重启 $BACKEND_CTR"

    log_info "等待后端就绪..."
    local i
    for i in $(seq 1 36); do
        if curl -sf "http://127.0.0.1:${BACKEND_PORT}/actuator/health" 2>/dev/null | grep -q UP; then
            log_info "后端已就绪"
            return 0
        fi
        sleep 5
    done
    log_warn "后端健康检查超时，请查看: docker logs $BACKEND_CTR"
}

update_frontend() {
    log_step "更新前端 dist"
    local dist="$UPDATE_DIR/dist"
    [ -d "$dist" ] || die "缺少 dist/"
    need_ctr "$FRONTEND_CTR"

    docker exec "$FRONTEND_CTR" sh -c 'rm -rf /usr/share/nginx/html/*'
    docker cp "$dist/." "$FRONTEND_CTR:/usr/share/nginx/html/"

    if [ -f "$UPDATE_DIR/nginx.conf" ]; then
        docker cp "$UPDATE_DIR/nginx.conf" "$FRONTEND_CTR:/etc/nginx/nginx.conf"
        docker exec "$FRONTEND_CTR" nginx -t
    fi
    docker exec "$FRONTEND_CTR" nginx -s reload
    log_info "前端已更新并 reload nginx"
}

run_flyway_if_needed() {
    if [ ! -d "$UPDATE_DIR/db/migration" ]; then
        log_info "跳过数据库迁移（未包含 db/migration）"
        return 0
    fi
    log_step "执行 Flyway 迁移"
    local network="${DOCKER_NETWORK:-opentcs-net}"
    local env_file=""
    for candidate in "$UPDATE_DIR/.env" "$(dirname "$UPDATE_DIR")/.env" \
        "$HOME/opentcs-plus-2.0.2-linux-amd64/.env" \
        "$HOME/opentcs-plus-2.0.1-linux-amd64/.env"; do
        if [ -f "$candidate" ]; then
            env_file="$candidate"
            break
        fi
    done
    # shellcheck disable=SC1090
    [ -n "$env_file" ] && source "$env_file"

    local locations="filesystem:/flyway/sql/migration"
    local vol_args=(-v "$UPDATE_DIR/db/migration:/flyway/sql/migration:ro")
    if [ -d "$UPDATE_DIR/db/repeatable" ] && [ "$(ls -A "$UPDATE_DIR/db/repeatable" 2>/dev/null)" ]; then
        vol_args+=(-v "$UPDATE_DIR/db/repeatable:/flyway/sql/repeatable:ro")
        locations="${locations},filesystem:/flyway/sql/repeatable"
    fi

    docker run --rm \
        --network "$network" \
        "${vol_args[@]}" \
        flyway/flyway:10-alpine \
        -url="jdbc:mysql://mysql:3306/${MYSQL_DATABASE:-opentcsplus}?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true" \
        -user=root \
        -password="${MYSQL_ROOT_PASSWORD:-MySQL@2024!Root}" \
        -locations="$locations" \
        -connectRetries=30 \
        migrate
    log_info "数据库迁移完成"
}

verify() {
    log_step "验证"
    curl -sf -o /dev/null -w "frontend:%{http_code}\n" http://127.0.0.1/ || true
    local js
    js=$(curl -sS http://127.0.0.1/ 2>/dev/null | grep -oE '/assets/[^"]+\.js' | head -1 || true)
    if [ -n "$js" ]; then
        curl -sS -I "http://127.0.0.1$js" 2>/dev/null | grep -iE 'HTTP/|Content-Type' || true
    fi
    curl -sf "http://127.0.0.1:${BACKEND_PORT}/actuator/health" 2>/dev/null || log_warn "后端 health 未通过"
    echo
}

main() {
    echo ""
    echo "  OpenTCS Plus 热更新（jar + dist）"
    echo ""
    docker info &>/dev/null || die "Docker 未运行"

    run_flyway_if_needed
    update_backend
    update_frontend
    verify

    log_info "更新完成"
}

main "$@"
