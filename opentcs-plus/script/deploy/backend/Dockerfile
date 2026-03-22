FROM eclipse-temurin:21-jdk-alpine AS builder

# 设置工作目录
WORKDIR /build

# 复制 pom.xml 和源码
COPY pom.xml .
COPY opentcs-admin/pom.xml opentcs-admin/
COPY opentcs-applications opentcs-applications/
COPY opentcs-kernel opentcs-kernel/
COPY opentcs-driver opentcs-driver/
COPY opentcs-common opentcs-common/
COPY opentcs-security opentcs-security/

# 下载依赖
RUN apk add --no-cache maven
RUN mvn dependency:go-offline -B

# 复制剩余源码并构建
COPY . .
RUN mvn clean package -DskipTests -Pprod

# ========== 运行镜像 ==========
FROM eclipse-temurin:21-jre-alpine

# 设置时区
RUN apk add --no-cache tzdata
ENV TZ=Asia/Shanghai

# 创建目录
RUN mkdir -p /app /app/logs /app/temp /app/upload

WORKDIR /app

# 复制构建产物
COPY --from=builder /build/opentcs-admin/target/opentcs-admin.jar ./app.jar

# 设置权限
RUN chown -R 1000:1000 /app

# 切换到非 root 用户
USER 1000

# 暴露端口
EXPOSE 8088

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8088/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", "/app/app.jar", "--spring.profiles.active=prod"]
