#!/bin/bash

# MySQL 一键部署脚本
# 版本: 1.0
# 功能: 部署单机MySQL，支持配置、备份、监控

set -e  # 遇到错误立即退出

echo "=============================================="
echo "           MySQL 单机一键部署脚本             "
echo "=============================================="

# ==================== 配置区 ====================
# 可修改的配置参数
MYSQL_PORT=3306
MYSQL_ROOT_PASSWORD="MySql@2024!Root"  # Root密码，生产环境请修改
MYSQL_DATABASE="app_db"                # 默认创建的数据库
MYSQL_USER="app_user"                  # 默认创建的用户
MYSQL_PASSWORD="MySql@2024!User"       # 用户密码
MYSQL_VERSION="8.0"                    # MySQL版本
DATA_DIR="/data/mysql"                 # MySQL数据目录
CONF_DIR="/etc/mysql"                  # MySQL配置目录
BACKUP_DIR="/data/mysql/backup"        # 备份目录
LOG_DIR="/var/log/mysql"               # 日志目录
CONTAINER_NAME="mysql-server"          # 容器名称
INNODB_BUFFER_SIZE="256M"              # InnoDB缓冲池大小
MAX_CONNECTIONS=1000                   # 最大连接数
CHARACTER_SET="utf8mb4"                # 字符集
# ===============================================

# 颜色输出函数
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# 检查 Docker 是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    print_info "Docker 已安装"
}

# 检查端口是否被占用
check_port() {
    if netstat -tuln | grep -q ":${1} "; then
        print_error "端口 ${1} 已被占用"
        exit 1
    fi
}

# 检查数据目录是否为空
check_data_dir() {
    if [ -d "${DATA_DIR}/mysql" ] && [ "$(ls -A ${DATA_DIR}/mysql 2>/dev/null)" ]; then
        print_warn "数据目录 ${DATA_DIR} 非空，可能是已有数据库"
        read -p "是否继续？可能会覆盖现有数据 (y/n): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_info "已取消部署"
            exit 0
        fi
    fi
}

# 创建必要的目录
create_directories() {
    print_step "1. 创建目录结构..."

    for dir in "$DATA_DIR" "$CONF_DIR" "$BACKUP_DIR" "$LOG_DIR"; do
        if [ ! -d "$dir" ]; then
            mkdir -p "$dir"
            chmod 755 "$dir"
            print_info "创建目录: $dir"
        fi
    done
}

# 生成 MySQL 配置文件
generate_mysql_config() {
    print_step "2. 生成 MySQL 配置文件..."

    local config_file="${CONF_DIR}/my.cnf"

    cat > "$config_file" << EOF
# MySQL 配置文件 - 自动生成
[mysqld]
# 基础配置
user = mysql
port = ${MYSQL_PORT}
bind-address = 0.0.0.0
socket = /var/run/mysqld/mysqld.sock
pid-file = /var/run/mysqld/mysqld.pid
basedir = /usr
datadir = /var/lib/mysql
tmpdir = /tmp
lc-messages-dir = /usr/share/mysql

# 字符集配置
character-set-server = ${CHARACTER_SET}
collation-server = ${CHARACTER_SET}_unicode_ci
init_connect = 'SET NAMES ${CHARACTER_SET}'
skip-character-set-client-handshake

# 连接配置
max_connections = ${MAX_CONNECTIONS}
max_connect_errors = 100000
wait_timeout = 600
interactive_timeout = 600
connect_timeout = 10

# 表名大小写
lower_case_table_names = 1

# 存储引擎
default-storage-engine = InnoDB
innodb_file_per_table = 1

# InnoDB 配置
innodb_buffer_pool_size = ${INNODB_BUFFER_SIZE}
innodb_log_file_size = 256M
innodb_log_buffer_size = 16M
innodb_flush_log_at_trx_commit = 2
innodb_lock_wait_timeout = 50
innodb_flush_method = O_DIRECT
innodb_read_io_threads = 8
innodb_write_io_threads = 8
innodb_io_capacity = 2000

# 日志配置
log_error = /var/log/mysql/error.log
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2
log_queries_not_using_indexes = 1
log_throttle_queries_not_using_indexes = 10

# 二进制日志（用于主从复制）
server-id = 1
log_bin = /var/log/mysql/mysql-bin
binlog_format = ROW
expire_logs_days = 7
max_binlog_size = 100M
binlog_cache_size = 32K
sync_binlog = 1

# 缓存配置
query_cache_type = 0
query_cache_size = 0
table_open_cache = 2000
table_definition_cache = 1400
thread_cache_size = 16

# 安全配置
local_infile = 0
symbolic-links = 0
skip-name-resolve

# 性能优化
sort_buffer_size = 4M
read_buffer_size = 2M
read_rnd_buffer_size = 8M
join_buffer_size = 4M
tmp_table_size = 64M
max_heap_table_size = 64M

[mysql]
default-character-set = ${CHARACTER_SET}

[client]
default-character-set = ${CHARACTER_SET}
port = ${MYSQL_PORT}
socket = /var/run/mysqld/mysqld.sock

[mysqld_safe]
log-error = /var/log/mysql/error.log
pid-file = /var/run/mysqld/mysqld.pid
EOF

    print_info "MySQL 配置文件已生成: ${config_file}"
}

# 拉取 MySQL 镜像
pull_mysql_image() {
    print_step "3. 拉取 MySQL 镜像..."

    local image_name="mysql:${MYSQL_VERSION}"

    if docker images | grep -q "mysql.*${MYSQL_VERSION}"; then
        print_info "MySQL 镜像已存在，跳过拉取"
    else
        print_info "正在拉取 MySQL ${MYSQL_VERSION} 镜像..."
        if docker pull ${image_name}; then
            print_info "MySQL 镜像拉取成功"
        else
            print_error "MySQL 镜像拉取失败，尝试使用镜像加速器..."
            docker pull hub-mirror.c.163.com/library/mysql:${MYSQL_VERSION} && \
            docker tag hub-mirror.c.163.com/library/mysql:${MYSQL_VERSION} mysql:${MYSQL_VERSION}
        fi
    fi
}

# 部署 MySQL 容器
deploy_mysql_container() {
    print_step "4. 部署 MySQL 容器..."

    # 停止并删除旧容器
    docker stop ${CONTAINER_NAME} 2>/dev/null || true
    docker rm ${CONTAINER_NAME} 2>/dev/null || true

    # 设置数据目录权限
    chmod 755 ${DATA_DIR}

    # 运行 MySQL 容器
    docker run -d \
        --name ${CONTAINER_NAME} \
        --restart unless-stopped \
        -p ${MYSQL_PORT}:${MYSQL_PORT} \
        -v ${DATA_DIR}:/var/lib/mysql \
        -v ${CONF_DIR}/my.cnf:/etc/mysql/my.cnf \
        -v ${BACKUP_DIR}:/backup \
        -v ${LOG_DIR}:/var/log/mysql \
        -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
        -e MYSQL_DATABASE=${MYSQL_DATABASE} \
        -e MYSQL_USER=${MYSQL_USER} \
        -e MYSQL_PASSWORD=${MYSQL_PASSWORD} \
        -e TZ=Asia/Shanghai \
        --memory=2g \
        --cpus=2 \
        --ulimit nofile=65536:65536 \
        mysql:${MYSQL_VERSION} \
        --character-set-server=${CHARACTER_SET} \
        --collation-server=${CHARACTER_SET}_unicode_ci \
        --default-authentication-plugin=mysql_native_password

    if [ $? -eq 0 ]; then
        print_info "MySQL 容器启动成功"
    else
        print_error "MySQL 容器启动失败"
        exit 1
    fi
}

# 等待 MySQL 启动
wait_mysql_start() {
    print_step "5. 等待 MySQL 启动..."

    local max_attempts=30
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        if docker exec ${CONTAINER_NAME} mysqladmin ping -h localhost -uroot -p${MYSQL_ROOT_PASSWORD} --silent 2>/dev/null; then
            print_info "✅ MySQL 已启动"
            return 0
        fi
        print_info "等待 MySQL 启动... (${attempt}/${max_attempts})"
        sleep 5
        ((attempt++))
    done

    print_error "MySQL 启动超时"
    docker logs ${CONTAINER_NAME} --tail 20
    exit 1
}

# 初始化数据库和用户
init_database() {
    print_step "6. 初始化数据库..."

    # 创建额外的数据库（示例）
    local init_sql="/tmp/init.sql"

    cat > ${init_sql} << EOF
-- 创建额外的数据库
CREATE DATABASE IF NOT EXISTS test_db DEFAULT CHARACTER SET ${CHARACTER_SET} COLLATE ${CHARACTER_SET}_unicode_ci;

-- 创建额外用户
CREATE USER IF NOT EXISTS 'readonly_user'@'%' IDENTIFIED BY 'Readonly@2024!';
GRANT SELECT ON *.* TO 'readonly_user'@'%';

CREATE USER IF NOT EXISTS 'backup_user'@'%' IDENTIFIED BY 'Backup@2024!';
GRANT SELECT, RELOAD, LOCK TABLES, REPLICATION CLIENT ON *.* TO 'backup_user'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- 显示用户
SELECT user, host FROM mysql.user;
EOF

    # 执行初始化 SQL
    docker exec -i ${CONTAINER_NAME} mysql -uroot -p${MYSQL_ROOT_PASSWORD} < ${init_sql}

    rm -f ${init_sql}
    print_info "数据库初始化完成"
}

# 验证 MySQL 服务
verify_mysql() {
    print_step "7. 验证 MySQL 服务..."

    # 测试连接
    if docker exec ${CONTAINER_NAME} mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "SELECT 1;" > /dev/null 2>&1; then
        print_info "✅ MySQL 连接测试通过"

        # 显示版本信息
        echo ""
        print_info "MySQL 基本信息:"
        docker exec ${CONTAINER_NAME} mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "SELECT VERSION();" 2>/dev/null

        # 显示数据库列表
        echo ""
        print_info "数据库列表:"
        docker exec ${CONTAINER_NAME} mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "SHOW DATABASES;" 2>/dev/null
    else
        print_error "MySQL 连接测试失败"
        docker logs ${CONTAINER_NAME} --tail 20
    fi
}

# 设置定时备份
setup_backup() {
    print_step "8. 设置 MySQL 备份..."

    local backup_script="/usr/local/bin/mysql-backup.sh"

    # 创建备份脚本
    sudo tee ${backup_script} > /dev/null << EOF
#!/bin/bash
# MySQL 自动备份脚本

BACKUP_DIR="${BACKUP_DIR}"
MYSQL_CONTAINER="${CONTAINER_NAME}"
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}"
DATE=\$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="mysql_backup_\${DATE}.sql.gz"
LOG_FILE="/var/log/mysql/backup.log"

echo "\$(date '+%Y-%m-%d %H:%M:%S') 开始备份 MySQL" >> \${LOG_FILE}

# 备份所有数据库
docker exec \${MYSQL_CONTAINER} mysqldump -uroot -p\${MYSQL_ROOT_PASSWORD} \
  --all-databases \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  --set-gtid-purged=OFF 2>> \${LOG_FILE} | gzip > \${BACKUP_DIR}/\${BACKUP_FILE}

# 检查备份是否成功
if [ \$? -eq 0 ]; then
    BACKUP_SIZE=\$(du -h \${BACKUP_DIR}/\${BACKUP_FILE} | cut -f1)
    echo "\$(date '+%Y-%m-%d %H:%M:%S') 备份完成: \${BACKUP_FILE} (大小: \${BACKUP_SIZE})" >> \${LOG_FILE}

    # 保留最近7天的备份
    cd \${BACKUP_DIR}
    ls -t mysql_backup_*.sql.gz | tail -n +8 | xargs rm -f 2>/dev/null
else
    echo "\$(date '+%Y-%m-%d %H:%M:%S') 备份失败!" >> \${LOG_FILE}
    exit 1
fi
EOF

    sudo chmod +x ${backup_script}

    # 添加到 crontab（每天凌晨1点备份）
    (crontab -l 2>/dev/null | grep -v "mysql-backup"; echo "0 1 * * * ${backup_script}") | crontab -

    print_info "✅ 已设置定时备份 (每天凌晨1点)"
}

# 设置监控脚本
setup_monitoring() {
    print_step "9. 设置监控脚本..."

    local monitor_script="/usr/local/bin/mysql-monitor.sh"

    sudo tee ${monitor_script} > /dev/null << EOF
#!/bin/bash
# MySQL 监控脚本

MYSQL_CONTAINER="${CONTAINER_NAME}"
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}"
LOG_FILE="/var/log/mysql/monitor.log"
ALERT_EMAIL="admin@example.com"  # 修改为你的邮箱

# 检查 MySQL 是否运行
check_mysql_running() {
    if docker ps | grep -q \${MYSQL_CONTAINER}; then
        return 0
    else
        echo "\$(date '+%Y-%m-%d %H:%M:%S') [ERROR] MySQL 容器未运行" >> \${LOG_FILE}
        return 1
    fi
}

# 检查连接数
check_connections() {
    local connections=\$(docker exec \${MYSQL_CONTAINER} mysql -uroot -p\${MYSQL_ROOT_PASSWORD} -sN -e "SHOW STATUS LIKE 'Threads_connected';" 2>/dev/null | awk '{print \$2}')
    local max_connections=${MAX_CONNECTIONS}

    if [ \$connections -gt \$((max_connections * 80 / 100)) ]; then
        echo "\$(date '+%Y-%m-%d %H:%M:%S') [WARN] 连接数过高: \${connections}" >> \${LOG_FILE}
    fi
}

# 检查慢查询
check_slow_queries() {
    docker exec \${MYSQL_CONTAINER} mysql -uroot -p\${MYSQL_ROOT_PASSWORD} -e "SELECT * FROM mysql.slow_log ORDER BY start_time DESC LIMIT 5;" >> \${LOG_FILE} 2>&1
}

# 检查磁盘空间
check_disk_space() {
    local usage=\$(df -h ${DATA_DIR} | tail -1 | awk '{print \$5}' | sed 's/%//')
    if [ \$usage -gt 80 ]; then
        echo "\$(date '+%Y-%m-%d %H:%M:%S') [WARN] 磁盘使用率过高: \${usage}%" >> \${LOG_FILE}
    fi
}

# 执行检查
echo "\$(date '+%Y-%m-%d %H:%M:%S') 开始 MySQL 监控检查" >> \${LOG_FILE}
check_mysql_running && check_connections && check_disk_space
echo "\$(date '+%Y-%m-%d %H:%M:%S') 监控检查完成" >> \${LOG_FILE}
EOF

    sudo chmod +x ${monitor_script}

    # 每5分钟执行一次监控
    (crontab -l 2>/dev/null | grep -v "mysql-monitor"; echo "*/5 * * * * ${monitor_script}") | crontab -

    print_info "✅ 已设置监控脚本 (每5分钟检查一次)"
}

# 显示部署信息
show_deployment_info() {
    local local_ip=$(hostname -I | awk '{print $1}')

    echo ""
    echo "=============================================="
    echo "          MySQL 部署完成！                    "
    echo "=============================================="
    echo "连接信息:"
    echo "  🔗 地址: ${local_ip}:${MYSQL_PORT}"
    echo "  🔑 Root密码: ${MYSQL_ROOT_PASSWORD}"
    echo "  📊 默认数据库: ${MYSQL_DATABASE}"
    echo "  👤 应用用户: ${MYSQL_USER}"
    echo "  🔐 用户密码: ${MYSQL_PASSWORD}"
    echo ""
    echo "数据目录:"
    echo "  📁 数据: ${DATA_DIR}"
    echo "  📁 配置: ${CONF_DIR}"
    echo "  📁 备份: ${BACKUP_DIR}"
    echo "  📁 日志: ${LOG_DIR}"
    echo ""
    echo "容器信息:"
    echo "  🐳 容器名: ${CONTAINER_NAME}"
    echo "  📦 MySQL版本: ${MYSQL_VERSION}"
    echo "  🔤 字符集: ${CHARACTER_SET}"
    echo ""
    echo "管理命令:"
    echo "  📋 查看日志: docker logs -f ${CONTAINER_NAME}"
    echo "  🔧 进入容器: docker exec -it ${CONTAINER_NAME} bash"
    echo "  📊 连接MySQL: docker exec -it ${CONTAINER_NAME} mysql -uroot -p"
    echo "  ⏹️  停止服务: docker stop ${CONTAINER_NAME}"
    echo "  ▶️  启动服务: docker start ${CONTAINER_NAME}"
    echo "  🔄 重启服务: docker restart ${CONTAINER_NAME}"
    echo "  🗑️  删除容器: docker rm -f ${CONTAINER_NAME}"
    echo ""
    echo "客户端连接示例:"
    echo "  mysql -h ${local_ip} -P ${MYSQL_PORT} -u root -p"
    echo "  mysql -h ${local_ip} -P ${MYSQL_PORT} -u ${MYSQL_USER} -p"
    echo ""
    echo "备份恢复命令:"
    echo "  备份: docker exec ${CONTAINER_NAME} mysqldump -uroot -p数据库名 > backup.sql"
    echo "  恢复: docker exec -i ${CONTAINER_NAME} mysql -uroot -p < backup.sql"
    echo "=============================================="
}

# 主函数
main() {
    print_info "开始部署 MySQL..."

    # 检查环境
    check_docker
    check_port ${MYSQL_PORT}
    check_data_dir

    # 执行部署步骤
    create_directories
    generate_mysql_config
    pull_mysql_image
    deploy_mysql_container
    wait_mysql_start
    init_database
    verify_mysql
    setup_backup
    setup_monitoring
    show_deployment_info

    print_info "MySQL 部署完成！"
}

# 执行主函数
main "$@"