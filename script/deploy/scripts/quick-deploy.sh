#!/bin/bash
# ============================================================
# 简易发布：本地编译 jar + dist，上传到服务器热更新
#
# 用法:
#   ./scripts/quick-deploy.sh ubuntu@106.54.43.41
#   DEPLOY_HOST=106.54.43.41 DEPLOY_USER=ubuntu ./scripts/quick-deploy.sh
#   SKIP_BUILD=1 ./scripts/quick-deploy.sh ubuntu@106.54.43.41
#
# 可选环境变量:
#   DEPLOY_HOST / DEPLOY_USER / DEPLOY_TARGET   目标主机
#   REMOTE_DIR     服务器更新目录，默认 ~/opentcs-update
#   SKIP_BUILD=1   跳过编译，直接上传已有产物
#   WITH_DB=1      同时上传并执行 Flyway（默认不做库迁移）
#   SSHPASS        若设置，则用 sshpass 密码登录
# ============================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_DIR="$(dirname "$SCRIPT_DIR")"
BACKEND_DIR="$(cd "$DEPLOY_DIR/../.." && pwd)"
FRONTEND_DIR="$(cd "$BACKEND_DIR/../opentcs-plus-web" 2>/dev/null && pwd || echo "")"
STAGING="${STAGING:-/tmp/opentcs-update-staging}"
REMOTE_DIR="${REMOTE_DIR:-~/opentcs-update}"
SKIP_BUILD="${SKIP_BUILD:-0}"
WITH_DB="${WITH_DB:-0}"
REQUIRED_JAVA_VERSION="${REQUIRED_JAVA_VERSION:-21}"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
log_info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}===== $1 =====${NC}\n"; }
die()       { log_error "$1"; exit 1; }

TARGET="${1:-}"
if [ -z "$TARGET" ]; then
    if [ -n "${DEPLOY_TARGET:-}" ]; then
        TARGET="$DEPLOY_TARGET"
    elif [ -n "${DEPLOY_HOST:-}" ]; then
        TARGET="${DEPLOY_USER:-ubuntu}@${DEPLOY_HOST}"
    else
        die "请指定目标: ./quick-deploy.sh user@host"
    fi
fi

SSH_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o PreferredAuthentications=password -o PubkeyAuthentication=no"

ssh_cmd() {
    if [ -n "${SSHPASS:-}" ] && command -v sshpass &>/dev/null; then
        # shellcheck disable=SC2086
        sshpass -p "$SSHPASS" ssh $SSH_OPTS "$@"
    else
        ssh -o StrictHostKeyChecking=no "$@"
    fi
}

scp_cmd() {
    if [ -n "${SSHPASS:-}" ] && command -v sshpass &>/dev/null; then
        # shellcheck disable=SC2086
        sshpass -p "$SSHPASS" scp $SSH_OPTS "$@"
    else
        scp -o StrictHostKeyChecking=no "$@"
    fi
}

rsync_cmd() {
    local rsync_ssh
    if [ -n "${SSHPASS:-}" ] && command -v sshpass &>/dev/null; then
        rsync_ssh="sshpass -p ${SSHPASS} ssh ${SSH_OPTS}"
    else
        rsync_ssh="ssh -o StrictHostKeyChecking=no"
    fi
    rsync -az --delete -e "$rsync_ssh" "$@"
}

build_backend() {
    log_step "编译后端 JAR"
    local jar="$BACKEND_DIR/opentcs-admin/target/opentcs-admin.jar"
    if [ "$SKIP_BUILD" = "1" ] && [ -f "$jar" ]; then
        log_info "跳过编译，复用 $jar"
        return 0
    fi
    command -v mvn &>/dev/null || die "未找到 mvn"
    command -v java &>/dev/null || die "未找到 java"
    local java_version
    java_version=$(java -version 2>&1 | awk -F '[".]' '/version/ {print $2; exit}')
    if [ "${java_version:-0}" -lt "$REQUIRED_JAVA_VERSION" ]; then
        die "需要 JDK ${REQUIRED_JAVA_VERSION}+，当前: ${java_version:-unknown}"
    fi
    cd "$BACKEND_DIR"
    mvn clean package -DskipTests -Pprod -q
    [ -f "$jar" ] || die "未生成 opentcs-admin.jar"
    log_info "JAR 就绪: $jar"
}

build_frontend() {
    log_step "编译前端 dist"
    [ -n "$FRONTEND_DIR" ] && [ -d "$FRONTEND_DIR" ] || die "未找到 opentcs-plus-web"
    if [ "$SKIP_BUILD" = "1" ] && [ -d "$FRONTEND_DIR/dist" ]; then
        log_info "跳过编译，复用 $FRONTEND_DIR/dist"
        return 0
    fi
    command -v npm &>/dev/null || die "未找到 npm"
    cd "$FRONTEND_DIR"
    npm ci --registry=https://registry.npmmirror.com
    npm run build:prod
    [ -f "$FRONTEND_DIR/dist/index.html" ] || die "前端构建失败"
    log_info "dist 就绪"
}

stage_files() {
    log_step "准备上传目录"
    rm -rf "$STAGING"
    mkdir -p "$STAGING/dist"
    cp "$BACKEND_DIR/opentcs-admin/target/opentcs-admin.jar" "$STAGING/app.jar"
    cp -R "$FRONTEND_DIR/dist/." "$STAGING/dist/"
    cp "$DEPLOY_DIR/frontend/nginx.conf" "$STAGING/nginx.conf"
    cp "$SCRIPT_DIR/apply-update.sh" "$STAGING/apply-update.sh"
    chmod +x "$STAGING/apply-update.sh"

    if [ "$WITH_DB" = "1" ]; then
        mkdir -p "$STAGING/db/migration" "$STAGING/db/repeatable"
        cp -R "$BACKEND_DIR/db/migration/." "$STAGING/db/migration/"
        cp -R "$BACKEND_DIR/db/repeatable/." "$STAGING/db/repeatable/" 2>/dev/null || true
    fi
    log_info "暂存: $STAGING ($(du -sh "$STAGING" | cut -f1))"
}

upload_and_apply() {
    log_step "上传到 $TARGET:$REMOTE_DIR"
    ssh_cmd "$TARGET" "mkdir -p $REMOTE_DIR"

    if command -v rsync &>/dev/null; then
        rsync_cmd "$STAGING/" "$TARGET:$REMOTE_DIR/"
    else
        # scp 整目录
        ssh_cmd "$TARGET" "rm -rf $REMOTE_DIR && mkdir -p $REMOTE_DIR"
        scp_cmd -r "$STAGING/." "$TARGET:$REMOTE_DIR/"
    fi

    log_step "服务器热更新"
    ssh_cmd "$TARGET" "cd $REMOTE_DIR && chmod +x apply-update.sh && ./apply-update.sh"
}

main() {
    echo ""
    echo "  OpenTCS Plus 简易发布 -> $TARGET"
    echo ""
    build_backend
    build_frontend
    stage_files
    upload_and_apply
    echo ""
    log_info "发布完成。访问: http://${TARGET#*@}/"
    echo ""
}

main "$@"
