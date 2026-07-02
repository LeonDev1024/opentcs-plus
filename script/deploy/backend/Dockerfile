# 目标平台: linux/amd64 (Ubuntu 20.04 x86)
# ========== 构建阶段 ==========
FROM --platform=linux/amd64 eclipse-temurin:21-jdk-focal AS builder

WORKDIR /build

# 安装 Maven
RUN apt-get update && apt-get install -y --no-install-recommends maven && rm -rf /var/lib/apt/lists/*

# 先复制 pom 文件，利用 Docker 层缓存加速依赖下载
COPY pom.xml .
COPY opentcs-admin/pom.xml opentcs-admin/
COPY opentcs-applications opentcs-applications/
COPY opentcs-kernel opentcs-kernel/
COPY opentcs-driver opentcs-driver/
COPY opentcs-common opentcs-common/
COPY opentcs-security opentcs-security/
COPY opentcs-infrastructure opentcs-infrastructure/
COPY opentcs-algorithm opentcs-algorithm/

RUN mvn dependency:go-offline -B -q

# 复制剩余源码并打包
COPY . .
RUN mvn clean package -DskipTests -Pprod -q

# ========== 运行镜像 (Ubuntu 20.04 focal) ==========
FROM --platform=linux/amd64 eclipse-temurin:21-jre-focal

# 安装必要工具并设置时区
RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata curl && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

ENV TZ=Asia/Shanghai

RUN mkdir -p /app/logs /app/temp /app/upload

WORKDIR /app

COPY --from=builder /build/opentcs-admin/target/opentcs-admin.jar ./app.jar

RUN chown -R 1000:1000 /app

USER 1000

EXPOSE 8088

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
    CMD curl -sf http://localhost:8088/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "/app/app.jar", "--spring.profiles.active=prod"]
