#!/bin/bash
# ============================================================
# OpenTCS Plus 编译打包脚本
# 在开发机上运行，生成可发布到 Ubuntu 20.04 x86 服务器的部署包
#
# 用法:
#   ./build-package.sh [版本号]
#   ./build-package.sh 1.0.0
#
# 产出:
#   dist/opentcs-plus-{version}-ubuntu20.04-x86.tar.gz
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(cd "$DEPLOY_DIR/../.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT"
FRONTEND_DIR="$(cd "$PROJECT_ROOT/../opentcs-plus-web" 2>/dev/null && pwd || echo "")"
DIST_DIR="$DEPLOY_DIR/dist"

VERSION="${1:-$(date +%Y%m%d%H%M)}"
PACKAGE_NAME="opentcs-plus-${VERSION}-ubuntu20.04-x86"
BACKEND_IMAGE="opentcs-admin:${VERSION}"
FRONTEND_IMAGE="opentcs-web:${VERSION}"

# 颜色
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
log_info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}===== $1 =====${NC}\n"; }

# 检查工具
check_deps() {
    log_step "检查构建环境"
    local missing=()
    command -v docker   &>/dev/null || missing+=("docker")
    command -v mvn      &>/dev/null || missing+=("maven")
    command -v node     &>/dev/null || missing+=("node")

    if [ "${#missing[@]}" -gt 0 ]; then
        log_error "缺少工具: ${missing[*]}"
        log_error "请先安装后再运行"
        exit 1
    fi

    if ! docker info &>/dev/null; then
        log_error "Docker 未运行，请先启动 Docker"
        exit 1
    fi

    # 检查 buildx 支持（跨平台构建需要）
    if ! docker buildx version &>/dev/null; then
        log_warn "docker buildx 不可用，跨平台构建可能受限"
    else
        # 确保 amd64 builder 存在
        docker buildx inspect opentcs-builder &>/dev/null || \
            docker buildx create --name opentcs-builder --driver docker-container --bootstrap &>/dev/null || true
    fi

    log_info "构建环境检查通过 (Node $(node -v), mvn $(mvn -v 2>&1 | head -1 | awk '{print $3}'))"
}

# 构建后端 JAR
build_backend_jar() {
    log_step "编译后端 (Maven)"
    cd "$BACKEND_DIR"
    mvn clean package -DskipTests -Pprod -q
    log_info "后端编译完成: opentcs-admin/target/opentcs-admin.jar"
}

# 构建前端静态资源
build_frontend_dist() {
    log_step "编译前端 (pnpm)"
    if [[ -z "$FRONTEND_DIR" || ! -d "$FRONTEND_DIR" ]]; then
        log_warn "未找到 opentcs-plus-web，跳过前端编译"
        return 0
    fi
    cd "$FRONTEND_DIR"

    if ! command -v pnpm &>/dev/null; then
        npm install -g pnpm --registry=https://registry.npmmirror.com
    fi

    pnpm install --frozen-lockfile --registry=https://registry.npmmirror.com
    pnpm build:prod
    log_info "前端编译完成: dist/"
}

# 构建后端 Docker 镜像 (linux/amd64)
build_backend_image() {
    log_step "构建后端 Docker 镜像 (linux/amd64)"
    cd "$BACKEND_DIR"

    docker buildx build \
        --platform linux/amd64 \
        --file script/deploy/backend/Dockerfile \
        --tag "$BACKEND_IMAGE" \
        --tag "opentcs-admin:latest" \
        --load \
        .

    log_info "后端镜像构建完成: $BACKEND_IMAGE"
}

# 构建前端 Docker 镜像 (linux/amd64)
build_frontend_image() {
    log_step "构建前端 Docker 镜像 (linux/amd64)"
    if [[ -z "$FRONTEND_DIR" || ! -d "$FRONTEND_DIR" ]]; then
        log_warn "未找到 opentcs-plus-web，跳过前端镜像"
        return 0
    fi
    cd "$FRONTEND_DIR"

    docker buildx build \
        --platform linux/amd64 \
        --file "$DEPLOY_DIR/frontend/Dockerfile" \
        --tag "$FRONTEND_IMAGE" \
        --tag "opentcs-web:latest" \
        --load \
        .

    log_info "前端镜像构建完成: $FRONTEND_IMAGE"
}

# 导出镜像为 tar 文件
export_images() {
    log_step "导出 Docker 镜像"
    local img_dir="$DIST_DIR/$PACKAGE_NAME/images"
    mkdir -p "$img_dir"

    log_info "导出后端镜像..."
    docker save "$BACKEND_IMAGE" | gzip > "$img_dir/opentcs-admin.tar.gz"

    log_info "导出前端镜像..."
    docker save "$FRONTEND_IMAGE" | gzip > "$img_dir/opentcs-web.tar.gz"

    log_info "拉取并导出中间件镜像 (linux/amd64)..."
    for img in "mysql:8.0" "redis:7.2" "emqx/emqx:5.7" "flyway/flyway:10-alpine"; do
        local fname
        fname=$(echo "$img" | tr '/:' '--')
        log_info "  导出 $img -> ${fname}.tar.gz"
        docker pull --platform linux/amd64 "$img" &>/dev/null || true
        docker save "$img" | gzip > "$img_dir/${fname}.tar.gz"
    done

    log_info "镜像导出完成"
}

# 打包部署配置文件
package_configs() {
    log_step "打包部署配置"
    local pkg_dir="$DIST_DIR/$PACKAGE_NAME"
    mkdir -p "$pkg_dir/config/mysql/init.d" "$pkg_dir/config/redis" "$pkg_dir/config/emqx"

    # docker-compose 和环境变量
    cp "$DEPLOY_DIR/docker-compose.yml"      "$pkg_dir/"
    cp "$DEPLOY_DIR/.env.example"            "$pkg_dir/.env.example" 2>/dev/null || \
        cp "$DEPLOY_DIR/scripts/.env.example" "$pkg_dir/.env.example" 2>/dev/null || true
    cp "$DEPLOY_DIR/application.yml"         "$pkg_dir/"

    # 中间件配置
    cp "$DEPLOY_DIR/config/mysql/my.cnf"     "$pkg_dir/config/mysql/"
    cp "$DEPLOY_DIR/config/redis/redis.conf" "$pkg_dir/config/redis/" 2>/dev/null || true
    cp "$DEPLOY_DIR/config/emqx/emqx.conf"  "$pkg_dir/config/emqx/"

    # MySQL init（仅建库；结构由 Flyway 负责）
    cp "$DEPLOY_DIR/config/mysql/init.d/01_init_database.sql" "$pkg_dir/config/mysql/init.d/"
    if [[ -f "$DEPLOY_DIR/config/mysql/init.d/03_demo_data.sql" ]]; then
        cp "$DEPLOY_DIR/config/mysql/init.d/03_demo_data.sql" "$pkg_dir/config/mysql/init.d/"
    fi

    # Flyway SQL
    if [[ -d "$BACKEND_DIR/db/migration" ]]; then
        mkdir -p "$pkg_dir/db/migration" "$pkg_dir/db/repeatable"
        cp -r "$BACKEND_DIR/db/migration/." "$pkg_dir/db/migration/"
        cp -r "$BACKEND_DIR/db/repeatable/." "$pkg_dir/db/repeatable/" 2>/dev/null || true
    fi

    # 安装与迁移脚本
    cp "$SCRIPT_DIR/install.sh" "$pkg_dir/"
    cp "$SCRIPT_DIR/flyway-migrate.sh" "$pkg_dir/"
    chmod +x "$pkg_dir/install.sh" "$pkg_dir/flyway-migrate.sh"

    # 写版本信息
    cat > "$pkg_dir/VERSION" <<EOF
version=${VERSION}
build_time=$(date '+%Y-%m-%d %H:%M:%S')
platform=linux/amd64
os=ubuntu20.04
backend_image=${BACKEND_IMAGE}
frontend_image=${FRONTEND_IMAGE}
EOF

    log_info "配置文件打包完成"
}

# 生成最终压缩包
create_tarball() {
    log_step "生成部署包"
    cd "$DIST_DIR"
    tar -czf "${PACKAGE_NAME}.tar.gz" "$PACKAGE_NAME"
    local size
    size=$(du -sh "${PACKAGE_NAME}.tar.gz" | cut -f1)
    log_info "部署包已生成: $DIST_DIR/${PACKAGE_NAME}.tar.gz ($size)"
    rm -rf "$DIST_DIR/$PACKAGE_NAME"
}

# 主流程
main() {
    echo ""
    echo "  ╔════════════════════════════════════════╗"
    echo "  ║   OpenTCS Plus 编译打包工具             ║"
    echo "  ║   目标: Ubuntu 20.04 x86 (linux/amd64) ║"
    echo "  ╚════════════════════════════════════════╝"
    echo ""
    log_info "版本: $VERSION"
    log_info "项目根目录: $PROJECT_ROOT"

    mkdir -p "$DIST_DIR"

    check_deps
    build_backend_jar
    build_frontend_dist
    build_backend_image
    build_frontend_image
    export_images
    package_configs
    create_tarball

    echo ""
    echo "  ╔════════════════════════════════════════════════╗"
    echo "  ║   打包完成！                                    ║"
    echo "  ╚════════════════════════════════════════════════╝"
    echo ""
    log_info "部署包: $DIST_DIR/${PACKAGE_NAME}.tar.gz"
    echo ""
    log_info "在目标服务器 (Ubuntu 20.04 x86) 上执行:"
    echo ""
    echo "    scp $DIST_DIR/${PACKAGE_NAME}.tar.gz user@server:/opt/"
    echo "    ssh user@server"
    echo "    cd /opt && tar xzf ${PACKAGE_NAME}.tar.gz"
    echo "    cd ${PACKAGE_NAME} && ./install.sh"
    echo ""
}

main "$@"
