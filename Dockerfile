# 使用JDK 21作为基础镜像
FROM eclipse-temurin:21-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制构建好的jar文件到容器中
COPY opentcs-admin/target/opentcs-admin.jar /app/opentcs-admin.jar

# 复制配置文件
COPY script/deploy/application.yml /app/application.yml

# 暴露端口
EXPOSE 8088

# 启动命令
CMD ["java", "-jar", "-Dspring.config.additional-location=file:./", "/app/opentcs-admin.jar"]
