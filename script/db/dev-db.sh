#!/usr/bin/env bash
# ============================================================
# 本地开发数据库快捷命令（默认 127.0.0.1 / root / 无密码 / opentcsplus）
# 用法: ./script/db/dev-db.sh rebuild|migrate|info|snapshot|validate|seed
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# shellcheck source=script/db/common.sh
source "$SCRIPT_DIR/common.sh"
if [[ -f "$SCRIPT_DIR/local.env" ]]; then
  # shellcheck disable=SC1091
  source "$SCRIPT_DIR/local.env"
fi

JDBC_URL="$(flyway_jdbc_url)"

mysql_exec() {
  local sql="$1"
  if command -v mysql >/dev/null 2>&1; then
    if [[ -n "$FLYWAY_PASSWORD" ]]; then
      mysql -h"$FLYWAY_HOST" -P"$FLYWAY_PORT" -u"$FLYWAY_USER" -p"$FLYWAY_PASSWORD" -e "$sql"
    else
      mysql -h"$FLYWAY_HOST" -P"$FLYWAY_PORT" -u"$FLYWAY_USER" -e "$sql"
    fi
    return
  fi
  local docker_args=(run --rm -i --network host)
  if [[ -n "$FLYWAY_PASSWORD" ]]; then
    docker_args+=(-e "MYSQL_PWD=$FLYWAY_PASSWORD")
  fi
  docker_args+=(mysql:8.0 mysql -h"$FLYWAY_HOST" -P"$FLYWAY_PORT" -u"$FLYWAY_USER" -e "$sql")
  docker "${docker_args[@]}"
}

mysql_import() {
  local file="$1"
  if command -v mysql >/dev/null 2>&1; then
    if [[ -n "$FLYWAY_PASSWORD" ]]; then
      mysql -h"$FLYWAY_HOST" -P"$FLYWAY_PORT" -u"$FLYWAY_USER" -p"$FLYWAY_PASSWORD" "$FLYWAY_DATABASE" < "$file"
    else
      mysql -h"$FLYWAY_HOST" -P"$FLYWAY_PORT" -u"$FLYWAY_USER" "$FLYWAY_DATABASE" < "$file"
    fi
    return
  fi
  docker run --rm -i --network host \
    ${FLYWAY_PASSWORD:+-e "MYSQL_PWD=$FLYWAY_PASSWORD"} \
    mysql:8.0 \
    mysql -h"$FLYWAY_HOST" -P"$FLYWAY_PORT" -u"$FLYWAY_USER" "$FLYWAY_DATABASE" < "$file"
}

flyway_mvn() {
  local goal="$1"
  (
    cd "$PROJECT_ROOT"
    mvn -N "flyway:${goal}" \
      -Dflyway.url="$JDBC_URL" \
      -Dflyway.user="$FLYWAY_USER" \
      -Dflyway.password="$FLYWAY_PASSWORD"
  )
}

ensure_database() {
  mysql_exec "CREATE DATABASE IF NOT EXISTS \`${FLYWAY_DATABASE}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
  echo "Database '${FLYWAY_DATABASE}' ready."
}

rebuild_database() {
  echo "Rebuilding database '${FLYWAY_DATABASE}'..."
  mysql_exec "DROP DATABASE IF EXISTS \`${FLYWAY_DATABASE}\`;" || true
  mysql_exec "CREATE DATABASE \`${FLYWAY_DATABASE}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
  flyway_mvn migrate
  mysql_import "$PROJECT_ROOT/db/seed/dev_bootstrap.sql"
  echo "Rebuild complete (admin / admin123)."
}

usage() {
  cat <<'EOF'
用法: ./script/db/dev-db.sh <command>

命令:
  rebuild    删除并重建数据库，执行 Flyway 全量迁移（全新初始化）
  init       创建数据库（若不存在）
  migrate    创建数据库并执行 Flyway 迁移
  info       查看迁移状态
  snapshot   生成 schema 快照
  validate   校验快照与迁移结果一致
  seed       导入演示数据（需先 migrate）
  bootstrap  导入开发引导数据（admin 账号、OAuth 客户端等）
EOF
}

cmd="${1:-}"
case "$cmd" in
  rebuild)
    rebuild_database
    ;;
  init)
    ensure_database
    ;;
  migrate)
    ensure_database
    flyway_mvn migrate
    client_count="$(mysql -h"$FLYWAY_HOST" -P"$FLYWAY_PORT" -u"$FLYWAY_USER" ${FLYWAY_PASSWORD:+-p"$FLYWAY_PASSWORD"} -N -e "SELECT COUNT(*) FROM \`${FLYWAY_DATABASE}\`.sys_client" 2>/dev/null || echo "0")"
    if [[ "${client_count:-0}" == "0" ]]; then
      echo "sys_client empty, importing dev bootstrap data..."
      mysql_import "$PROJECT_ROOT/db/seed/dev_bootstrap.sql"
    fi
    ;;
  info)
    flyway_mvn info
    ;;
  snapshot)
    exec "$SCRIPT_DIR/generate-snapshot.sh"
    ;;
  validate)
    exec "$SCRIPT_DIR/validate-snapshot.sh"
    ;;
  seed)
    mysql_import "$PROJECT_ROOT/db/seed/demo_factory_data.sql"
    echo "Seed data imported."
    ;;
  bootstrap)
    mysql_import "$PROJECT_ROOT/db/seed/dev_bootstrap.sql"
    echo "Dev bootstrap data imported (admin / admin123)."
    ;;
  *)
    usage
    exit 1
    ;;
esac
