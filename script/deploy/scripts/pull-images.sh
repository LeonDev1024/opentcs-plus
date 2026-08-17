#!/bin/bash
# 预拉取离线打包所需的 Docker 运行镜像（不含 Maven，JAR 在本地编译）
set -euo pipefail

TARGET_PLATFORM="${TARGET_PLATFORM:-linux/amd64}"

IMAGES=(
    "eclipse-temurin:21-jre-jammy"
    "nginx:1.25-alpine"
    "mysql:8.0"
    "redis:7-alpine"
    "emqx/emqx:5.7.2"
    "flyway/flyway:10-alpine"
)

pull_one() {
    local image="$1"
    local attempt
    for attempt in 1 2 3 4 5; do
        echo ">>> 拉取 $image (尝试 $attempt/5)"
        if docker pull --platform "$TARGET_PLATFORM" "$image"; then
            echo ">>> 完成: $image"
            return 0
        fi
        sleep 3
    done
    echo "!!! 拉取失败: $image"
    return 1
}

echo "目标平台: $TARGET_PLATFORM"
echo ""
echo "说明: 后端 JAR 在本地用 JDK 21 + Maven 编译，此处只拉运行镜像。"
echo "提示: 若镜像拉取 EOF，请暂时关闭 Docker Desktop 中有问题的 registry-mirrors。"
echo ""

failed=0
for img in "${IMAGES[@]}"; do
    pull_one "$img" || failed=$((failed + 1))
done

if [ "$failed" -gt 0 ]; then
    echo ""
    echo "有 $failed 个镜像拉取失败，请检查网络或镜像加速器配置。"
    exit 1
fi

echo ""
echo "全部镜像已就绪。本地编译后端: cd opentcs-plus && mvn clean package -DskipTests -Pprod"
echo "然后打包: ./deploy.sh build <版本号>"
