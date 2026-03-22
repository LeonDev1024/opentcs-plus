#!/bin/bash

#===========================================
# OpenTCS Plus 健康检查脚本
# 用于监控服务状态并发送告警
#===========================================

# 配置
SERVER_IP="${SERVER_IP:-localhost}"
ALERT_EMAIL="${ALERT_EMAIL:-admin@example.com}"
SLACK_WEBHOOK="${SLACK_WEBHOOK:-}"
DINGTALK_WEBHOOK="${DINGTALK_WEBHOOK:-}"

# 服务端口
FRONTEND_PORT=80
BACKEND_PORT=8088
MYSQL_PORT=3306
REDIS_PORT=6379
MINIO_PORT=9000

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

#===========================================
# 辅助函数
#===========================================

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# 发送告警
send_alert() {
    local subject="$1"
    local message="$2"

    # 邮件告警
    if [ -n "$ALERT_EMAIL" ]; then
        echo "$message" | mail -s "[OpenTCS] $subject" "$ALERT_EMAIL" 2>/dev/null || true
    fi

    # Slack 告警
    if [ -n "$SLACK_WEBHOOK" ]; then
        curl -s -X POST -H 'Content-type: application/json' \
            --data "{\"text\":\"[OpenTCS] $subject: $message\"}" \
            "$SLACK_WEBHOOK" 2>/dev/null || true
    fi

    # 钉钉告警
    if [ -n "$DINGTALK_WEBHOOK" ]; then
        curl -s -X POST -H 'Content-Type: application/json' \
            --data "{\"msgtype\":\"text\",\"text\":{\"content\":\"[OpenTCS] $subject: $message\"}}" \
            "$DINGTALK_WEBHOOK" 2>/dev/null || true
    fi
}

# 检查端口
check_port() {
    local host="$1"
    local port="$2"
    local name="$3"

    if timeout 5 bash -c "echo >/dev/tcp/$host/$port" 2>/dev/null; then
        log_info "$name 端口 $port: OK"
        return 0
    else
        log_error "$name 端口 $port: 不可达"
        return 1
    fi
}

# 检查 HTTP 服务
check_http() {
    local url="$1"
    local name="$2"

    local status=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 5 "$url" 2>/dev/null || echo "000")

    if [ "$status" = "200" ]; then
        log_info "$name: OK (HTTP $status)"
        return 0
    else
        log_error "$name: 异常 (HTTP $status)"
        return 1
    fi
}

# 检查 MySQL
check_mysql() {
    local status=0

    # 检查端口
    check_port "$SERVER_IP" "$MYSQL_PORT" "MySQL" || status=1

    # 检查连接
    if docker exec opentcs-mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
        log_info "MySQL 连接: OK"
    else
        log_error "MySQL 连接: 失败"
        status=1
    fi

    return $status
}

# 检查 Redis
check_redis() {
    local password="${REDIS_PASSWORD:-redis123}"
    local status=0

    # 检查端口
    check_port "$SERVER_IP" "$REDIS_PORT" "Redis" || status=1

    # 检查连接
    if docker exec opentcs-redis redis-cli -a "$password" ping 2>/dev/null | grep -q "PONG"; then
        log_info "Redis 连接: OK"
    else
        log_error "Redis 连接: 失败"
        status=1
    fi

    return $status
}

# 检查 MinIO
check_minio() {
    local status=0
    local user="${MINIO_USER:-minioadmin}"
    local password="${MINIO_PASSWORD:-minioadmin123}"

    check_port "$SERVER_IP" "$MINIO_PORT" "MinIO" || status=1

    # 检查健康状态
    if curl -s -u "$user:$password" "http://${SERVER_IP}:${MINIO_PORT}/minio/health/live" &>/dev/null; then
        log_info "MinIO 服务: OK"
    else
        log_error "MinIO 服务: 异常"
        status=1
    fi

    return $status
}

# 检查后端
check_backend() {
    check_http "http://${SERVER_IP}:${BACKEND_PORT}/actuator/health" "后端服务"
}

# 检查前端
check_frontend() {
    check_http "http://${SERVER_IP}:${FRONTEND_PORT}/health" "前端服务"
}

# 检查容器状态
check_containers() {
    local failed=0

    echo ""
    echo "容器状态:"
    docker compose ps 2>/dev/null | grep -E "opentcs-|STATUS" || echo "无运行中的容器"

    # 检查失败的容器
    while IFS= read -r line; do
        if echo "$line" | grep -q "Exit"; then
            log_error "容器异常退出: $line"
            failed=1
        fi
    done < <(docker compose ps 2>/dev/null)

    return $failed
}

# 主检查
main() {
    echo "========================================"
    echo "    OpenTCS Plus 健康检查"
    echo "    时间: $(date '+%Y-%m-%d %H:%M:%S')"
    echo "========================================"

    local failed=0

    echo ""
    echo "--- 基础设施 ---"
    check_mysql || failed=1
    check_redis || failed=1
    check_minio || failed=1

    echo ""
    echo "--- 应用服务 ---"
    check_backend || failed=1
    check_frontend || failed=1

    check_containers || failed=1

    echo ""
    if [ $failed -eq 0 ]; then
        log_info "所有服务健康"
        return 0
    else
        log_error "部分服务异常，请检查"
        send_alert "健康检查失败" "部分服务检查失败，请立即处理"
        return 1
    fi
}

# 根据参数执行
case "${1:-check}" in
    check)
        main
        ;;
    watch)
        # 持续监控模式
        while true; do
            clear
            main
            echo ""
            echo "等待 30 秒后重新检查..."
            sleep 30
        done
        ;;
    alert)
        # 发送测试告警
        send_alert "测试告警" "这是一条测试告警消息"
        ;;
    *)
        echo "用法: $0 {check|watch|alert}"
        echo "  check  - 执行一次健康检查"
        echo "  watch  - 持续监控模式"
        echo " alert  - 发送测试告警"
        exit 1
        ;;
esac
