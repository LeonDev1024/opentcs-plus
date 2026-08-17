#!/bin/bash
# ============================================================
# OpenTCS Plus 编译打包脚本
#
# 用法:
#   ./scripts/pull-images.sh          # 预拉 Docker 运行镜像
#   ./build-package.sh [版本号]       # 本地 JDK21+Maven 编译 + Docker 打镜像
#   SKIP_SOURCE_BUILD=1 ./build-package.sh [版本]  # 复用已有 dist/jar
#
# 产出: dist/opentcs-plus-{version}-linux-amd64.tar.gz
# ============================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(cd "$DEPLOY_DIR/../.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT"
FRONTEND_DIR="$(cd "$PROJECT_ROOT/../opentcs-plus-web" 2>/dev/null && pwd || echo "")"
DIST_DIR="$DEPLOY_DIR/dist"
BACKEND_STAGE="$DEPLOY_DIR/.build/backend"

VERSION="${1:-$(date +%Y%m%d%H%M)}"
PACKAGE_NAME="opentcs-plus-${VERSION}-linux-amd64"
BACKEND_IMAGE="opentcs-admin:${VERSION}"
FRONTEND_IMAGE="opentcs-web:${VERSION}"
TARGET_PLATFORM="${TARGET_PLATFORM:-linux/amd64}"
EMQX_IMAGE="${EMQX_IMAGE:-emqx/emqx:5.7.2}"
SKIP_SOURCE_BUILD="${SKIP_SOURCE_BUILD:-0}"
REQUIRED_JAVA_VERSION="${REQUIRED_JAVA_VERSION:-21}"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
log_info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}===== $1 =====${NC}\n"; }
die()       { log_error "$1"; exit 1; }

pull_image() {
    local image="$1"
    if docker image inspect "$image" &>/dev/null; then
        local platform
        platform=$(docker image inspect "$image" --format '{{.Os}}/{{.Architecture}}' 2>/dev/null || echo "")
        if [ "$platform" = "$TARGET_PLATFORM" ] || [ -z "$platform" ]; then
            log_info "复用本地镜像: ${image} (${platform:-unknown})"
            return 0
        fi
        log_warn "本地镜像 ${image} 平台为 ${platform}，需要 ${TARGET_PLATFORM}，尝试拉取..."
    fi
    local attempt
    for attempt in 1 2 3; do
        log_info "拉取镜像: ${image} (第 ${attempt} 次)"
        if docker pull --platform "$TARGET_PLATFORM" "$image"; then
            return 0
        fi
        sleep 2
    done
    die "镜像拉取失败: ${image}。请先执行 ./scripts/pull-images.sh；若出现 reg-mirror.qiniu.com EOF，请在 Docker Desktop 中移除该镜像加速器后重启 Docker"
}

docker_build() {
    local context="$1"
    local dockerfile="$2"
    shift 2
    local tags=("$@")
    local tag_args=()
    local attempt

    for tag in "${tags[@]}"; do
        tag_args+=(-t "$tag")
    done

    for attempt in 1 2; do
        if DOCKER_BUILDKIT=1 docker build \
            --platform "$TARGET_PLATFORM" \
            --provenance=false \
            --sbom=false \
            --pull=false \
            -f "$dockerfile" \
            "${tag_args[@]}" \
            "$context"; then
            return 0
        fi
        if [ "$attempt" -eq 1 ]; then
            log_warn "Docker 构建失败，清理 builder 缓存后重试..."
            docker builder prune -f >/dev/null 2>&1 || true
        fi
    done
    die "Docker 构建失败。建议: docker builder prune -af && ./scripts/pull-images.sh"
}

check_deps() {
    log_step "检查构建环境"
    local missing=()
    command -v docker &>/dev/null || missing+=("docker")
    if [ "$SKIP_SOURCE_BUILD" != "1" ]; then
        command -v mvn  &>/dev/null || missing+=("maven")
        command -v java &>/dev/null || missing+=("java")
        command -v node &>/dev/null || missing+=("node")
        command -v npm  &>/dev/null || missing+=("npm")
    fi
    [ "${#missing[@]}" -eq 0 ] || die "缺少工具: ${missing[*]}"
    docker info &>/dev/null || die "Docker 未运行"

    if [ "$SKIP_SOURCE_BUILD" != "1" ]; then
        local java_version
        java_version=$(java -version 2>&1 | awk -F '[".]' '/version/ {print $2; exit}')
        if [ "${java_version:-0}" -lt "$REQUIRED_JAVA_VERSION" ]; then
            die "本机 Java 版本过低 (当前: ${java_version:-unknown}, 需要: ${REQUIRED_JAVA_VERSION}+)。请先切换 JDK 21: export JAVA_HOME=\$(/usr/libexec/java_home -v 21)"
        fi
        log_info "Java 版本: $(java -version 2>&1 | head -1)"
    fi

    log_info "构建环境检查通过（本地 Maven 编译 JAR，Docker 仅打运行镜像）"
}

prepare_base_images() {
    log_step "准备 Docker 运行镜像"
    pull_image "eclipse-temurin:21-jre-jammy"
    pull_image "nginx:1.25-alpine"
}

build_backend_jar() {
    log_step "编译后端 (本地 Maven, JDK ${REQUIRED_JAVA_VERSION})"
    local jar="$BACKEND_DIR/opentcs-admin/target/opentcs-admin.jar"
    if [ -f "$jar" ] && [ "${FORCE_BACKEND_BUILD:-0}" != "1" ]; then
        log_info "复用已有 JAR: $jar"
        return 0
    fi

    cd "$BACKEND_DIR"
    mvn clean package -DskipTests -Pprod -q
    [ -f "$jar" ] || die "Maven 编译失败，未生成 opentcs-admin.jar"
    log_info "后端 JAR 编译完成"
}

build_frontend_dist() {
    log_step "编译前端 (npm)"
    [ -d "$FRONTEND_DIR" ] || die "未找到 opentcs-plus-web"
    cd "$FRONTEND_DIR"
    npm ci --registry=https://registry.npmmirror.com
    npm run build:prod
    log_info "前端编译完成"
}

build_backend_image() {
    log_step "构建后端运行镜像 (仅 COPY JAR，无 apt)"
    local jar="$BACKEND_DIR/opentcs-admin/target/opentcs-admin.jar"
    [ -f "$jar" ] || die "缺少后端 JAR，请先编译后端"

    mkdir -p "$BACKEND_STAGE"
    cp "$jar" "$BACKEND_STAGE/app.jar"

    docker_build \
        "$BACKEND_STAGE" \
        "$DEPLOY_DIR/backend/Dockerfile" \
        "$BACKEND_IMAGE" \
        "opentcs-admin:latest"

    rm -rf "$BACKEND_STAGE"
    log_info "后端镜像: $BACKEND_IMAGE"
}

build_frontend_image() {
    log_step "构建前端 Docker 镜像"
    [ -d "$FRONTEND_DIR/dist" ] || die "缺少前端 dist"

    rm -rf "$DEPLOY_DIR/frontend/dist"
    cp -r "$FRONTEND_DIR/dist" "$DEPLOY_DIR/frontend/dist"

    docker_build \
        "$DEPLOY_DIR/frontend" \
        "$DEPLOY_DIR/frontend/Dockerfile" \
        "$FRONTEND_IMAGE" \
        "opentcs-web:latest"

    rm -rf "$DEPLOY_DIR/frontend/dist"
    log_info "前端镜像: $FRONTEND_IMAGE"
}

export_images() {
    log_step "导出 Docker 镜像"
    local img_dir="$DIST_DIR/$PACKAGE_NAME/images"
    mkdir -p "$img_dir"

    for img in "$BACKEND_IMAGE" "$FRONTEND_IMAGE"; do
        docker image inspect "$img" &>/dev/null || die "缺少镜像: $img"
        local fname
        fname=$(echo "$img" | tr '/:' '--')
        log_info "导出 $img"
        docker save "$img" | gzip > "$img_dir/${fname}.tar.gz"
    done

    log_info "导出中间件镜像..."
    for img in "mysql:8.0" "redis:7-alpine" "$EMQX_IMAGE" "flyway/flyway:10-alpine"; do
        pull_image "$img"
        local fname
        fname=$(echo "$img" | tr '/:' '--')
        log_info "导出 $img"
        docker save "$img" | gzip > "$img_dir/${fname}.tar.gz"
    done
}

package_configs() {
    log_step "打包部署配置"
    local pkg_dir="$DIST_DIR/$PACKAGE_NAME"
    mkdir -p "$pkg_dir/config/mysql/init.d" "$pkg_dir/scripts"

    cp "$DEPLOY_DIR/docker-compose.yml" "$pkg_dir/"
    cp "$DEPLOY_DIR/.env.example"     "$pkg_dir/.env.example"
    cp "$DEPLOY_DIR/application.yml"  "$pkg_dir/"
    cp "$DEPLOY_DIR/README.md"        "$pkg_dir/"
    cp "$DEPLOY_DIR/deploy.sh"        "$pkg_dir/"
    cp "$DEPLOY_DIR/config/mysql/my.cnf" "$pkg_dir/config/mysql/"
    cp "$DEPLOY_DIR/config/mysql/init.d/01_init_database.sql" "$pkg_dir/config/mysql/init.d/"

    if [[ -f "$DEPLOY_DIR/config/mysql/init.d/03_demo_data.sql" ]]; then
        cp "$DEPLOY_DIR/config/mysql/init.d/03_demo_data.sql" "$pkg_dir/config/mysql/init.d/"
    fi

    if [[ -d "$BACKEND_DIR/db/migration" ]]; then
        mkdir -p "$pkg_dir/db/migration" "$pkg_dir/db/repeatable"
        cp -r "$BACKEND_DIR/db/migration/." "$pkg_dir/db/migration/"
        cp -r "$BACKEND_DIR/db/repeatable/." "$pkg_dir/db/repeatable/" 2>/dev/null || true
    fi

    cp "$SCRIPT_DIR/install.sh" "$pkg_dir/"
    cp "$SCRIPT_DIR/flyway-migrate.sh" "$pkg_dir/"
    cp "$SCRIPT_DIR/flyway-migrate.sh" "$pkg_dir/scripts/"
    chmod +x "$pkg_dir/deploy.sh" "$pkg_dir/install.sh" "$pkg_dir/flyway-migrate.sh" "$pkg_dir/scripts/flyway-migrate.sh"

    cat > "$pkg_dir/VERSION" <<EOF
version=${VERSION}
build_time=$(date '+%Y-%m-%d %H:%M:%S')
platform=linux/amd64
backend_image=${BACKEND_IMAGE}
frontend_image=${FRONTEND_IMAGE}
emqx_image=${EMQX_IMAGE}
EOF

    sed -i.bak 's|../../db/migration|./db/migration|g; s|../../db/repeatable|./db/repeatable|g' \
        "$pkg_dir/docker-compose.yml" 2>/dev/null || \
    sed -i '' 's|../../db/migration|./db/migration|g; s|../../db/repeatable|./db/repeatable|g' \
        "$pkg_dir/docker-compose.yml"
    rm -f "$pkg_dir/docker-compose.yml.bak"
}

create_tarball() {
    log_step "生成部署包"
    cd "$DIST_DIR"
    tar -czf "${PACKAGE_NAME}.tar.gz" "$PACKAGE_NAME"
    shasum -a 256 "${PACKAGE_NAME}.tar.gz" > "${PACKAGE_NAME}.tar.gz.sha256"
    log_info "部署包: $DIST_DIR/${PACKAGE_NAME}.tar.gz ($(du -sh "${PACKAGE_NAME}.tar.gz" | cut -f1))"
    rm -rf "$DIST_DIR/$PACKAGE_NAME"
}

main() {
    echo ""
    echo "  OpenTCS Plus 离线打包  |  目标: linux/amd64"
    echo ""
    log_info "版本: $VERSION"

    mkdir -p "$DIST_DIR" "$DEPLOY_DIR/.build"
    rm -rf "$DIST_DIR/$PACKAGE_NAME" "$DIST_DIR/${PACKAGE_NAME}.tar.gz" "$DIST_DIR/${PACKAGE_NAME}.tar.gz.sha256"

    check_deps
    prepare_base_images

    if [ "$SKIP_SOURCE_BUILD" = "1" ]; then
        [ -d "$FRONTEND_DIR/dist" ] || die "SKIP_SOURCE_BUILD=1 但缺少前端 dist"
        [ -f "$BACKEND_DIR/opentcs-admin/target/opentcs-admin.jar" ] || die "SKIP_SOURCE_BUILD=1 但缺少后端 JAR"
        log_info "跳过源码编译，复用已有产物"
    else
        build_frontend_dist
        build_backend_jar
    fi

    build_backend_image
    build_frontend_image
    export_images
    package_configs
    create_tarball

    echo ""
    log_info "打包完成: $DIST_DIR/${PACKAGE_NAME}.tar.gz"
    echo "服务器安装: tar xzf ${PACKAGE_NAME}.tar.gz && cd ${PACKAGE_NAME} && ./install.sh"
    echo ""
}

main "$@"
