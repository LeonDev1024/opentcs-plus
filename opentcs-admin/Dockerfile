# 使用Java 17作为基础镜像
FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制构建好的jar文件到容器中
COPY target/opentcs-admin.jar /app/opentcs-admin.jar

# 暴露端口
EXPOSE 8080

# 启动命令
CMD ["java", "-jar", "/app/opentcs-admin.jar"]