# 后端运行镜像：仅打包 JAR，不在镜像内编译、不执行 apt
# 构建上下文: deploy/.build/backend/ (仅含 app.jar)
FROM eclipse-temurin:21-jre-jammy

ENV TZ=Asia/Shanghai \
    LANG=C.UTF-8

WORKDIR /app

RUN mkdir -p /app/logs /app/temp /app/upload \
    && chown -R 1000:1000 /app

COPY --chown=1000:1000 app.jar ./app.jar

USER 1000

EXPOSE 8088

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
    CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/8088' || exit 1

ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", \
    "-XX:+UseG1GC", "-XX:+UseContainerSupport", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "/app/app.jar", "--spring.profiles.active=prod"]
