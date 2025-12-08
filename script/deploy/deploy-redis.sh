#!/bin/bash

# Redis一键部署脚本

# 配置变量
REDIS_PORT=6379
REDIS_PASSWORD="your_redis_password"  # 请修改为你的密码
REDIS_DATA_DIR="/data/redis/data"
REDIS_CONF_DIR="/data/redis/conf"
REDIS_CONF_FILE="${REDIS_CONF_DIR}/redis.conf"

echo "开始部署单机Redis..."

# 创建数据目录和配置目录
mkdir -p ${REDIS_DATA_DIR}
mkdir -p ${REDIS_CONF_DIR}

# 创建Redis配置文件
cat > ${REDIS_CONF_FILE} << EOF
# 绑定地址，0.0.0.0表示允许所有连接
bind 0.0.0.0

# 端口
port ${REDIS_PORT}

# 设置密码
requirepass ${REDIS_PASSWORD}

# 启用持久化
save 900 1
save 300 10
save 60 10000

# 持久化文件存储目录
dir ${REDIS_DATA_DIR}

# 日志级别
loglevel notice

# 日志文件（标准输出，便于Docker日志收集）
logfile ""

# 最大内存限制（根据需求调整）
maxmemory 1gb

# 内存淘汰策略
maxmemory-policy allkeys-lru

# 启用AOF持久化
appendonly yes
appendfilename "appendonly.aof"
appendfsync everysec
EOF

echo "Redis配置文件创建完成：${REDIS_CONF_FILE}"

# 拉取Redis镜像
docker pull redis:7-alpine

# 停止并删除旧容器（如果存在）
docker stop redis-server 2>/dev/null || true
docker rm redis-server 2>/dev/null || true

# 运行Redis容器
docker run -d \
  --name redis-server \
  --restart unless-stopped \
  -p ${REDIS_PORT}:${REDIS_PORT} \
  -v ${REDIS_DATA_DIR}:/data \
  -v ${REDIS_CONF_FILE}:/usr/local/etc/redis/redis.conf \
  redis:7-alpine \
  redis-server /usr/local/etc/redis/redis.conf

echo "等待Redis启动..."
sleep 5

# 测试Redis连接
if docker exec redis-server redis-cli -a ${REDIS_PASSWORD} ping | grep -q "PONG"; then
    echo "✅ Redis部署成功！"
    echo "连接信息："
    echo "  地址: $(hostname -I | awk '{print $1}')"
    echo "  端口: ${REDIS_PORT}"
    echo "  密码: ${REDIS_PASSWORD}"
else
    echo "❌ Redis部署失败，请检查日志。"
    docker logs redis-server
fi