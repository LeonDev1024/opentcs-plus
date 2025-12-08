#!/bin/bash

# 配置变量
DB_HOST="your-db-host"
DB_PORT="3306"
DB_NAME="xxl_job"
DB_USER="your_username"
DB_PASSWORD="your_password"
ADMIN_PORT="8080"
ACCESS_TOKEN="xxl_job_token_2024"

echo "开始部署 XXL-Job Admin..."

# 创建日志目录
mkdir -p ./logs
mkdir -p ./applogs

# 部署容器
docker run -d \
  --name xxl-job-admin \
  --network bridge \
  -p ${ADMIN_PORT}:8080 \
  -e PARAMS="
    --spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&serverTimezone=Asia/Shanghai
    --spring.datasource.username=${DB_USER}
    --spring.datasource.password=${DB_PASSWORD}
    --xxl.job.i18n=zh_CN
    --xxl.job.login.username=admin
    --xxl.job.login.password=123456
    --xxl.job.accessToken=${ACCESS_TOKEN}
  " \
  -v $(pwd)/logs:/data/applogs \
  -v $(pwd)/applogs:/tmp \
  --restart=unless-stopped \
  xuxueli/xxl-job-admin:latest

# 检查容器状态
sleep 5
echo "检查容器状态..."
docker ps | grep xxl-job-admin

echo "部署完成！"
echo "访问地址: http://$(hostname -I | awk '{print $1}'):${ADMIN_PORT}/xxl-job-admin"
echo "用户名: admin"
echo "密码: 123456"
echo "Access Token: ${ACCESS_TOKEN}"