#!/usr/bin/env bash
# ============================================================
# 生成全量 Schema 快照（仅结构，不含业务数据）
# 流程: Flyway migrate → mysqldump --no-data → 写入 db/snapshot/
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
SNAPSHOT_DIR="$PROJECT_ROOT/db/snapshot"
SNAPSHOT_FILE="$SNAPSHOT_DIR/schema-snapshot.sql"
META_FILE="$SNAPSHOT_DIR/.snapshot-meta.json"

# shellcheck source=script/db/common.sh
source "$SCRIPT_DIR/common.sh"
if [[ -f "$SCRIPT_DIR/local.env" ]]; then
  # shellcheck disable=SC1091
  source "$SCRIPT_DIR/local.env"
fi

JDBC_URL="$(flyway_jdbc_url)"

mkdir -p "$SNAPSHOT_DIR"

echo "[1/4] Flyway migrate (Maven)..."
(
  cd "$PROJECT_ROOT"
  mvn -q -N flyway:migrate \
    -Dflyway.url="$JDBC_URL" \
    -Dflyway.user="$FLYWAY_USER" \
    -Dflyway.password="$FLYWAY_PASSWORD"
)

echo "[2/4] mysqldump schema..."
dump_schema() {
  local dump_args=(
    --no-data --routines --triggers
    --skip-add-drop-table --set-gtid-purged=OFF
    -h "$FLYWAY_HOST" -P "$FLYWAY_PORT" -u "$FLYWAY_USER"
    "$FLYWAY_DATABASE"
  )
  if command -v mysqldump >/dev/null 2>&1; then
    if [[ -n "$FLYWAY_PASSWORD" ]]; then
      MYSQL_PWD="$FLYWAY_PASSWORD" mysqldump "${dump_args[@]}"
    else
      mysqldump "${dump_args[@]}"
    fi
    return
  fi
  if [[ -n "$FLYWAY_PASSWORD" ]]; then
    docker run --rm --network host -e "MYSQL_PWD=$FLYWAY_PASSWORD" mysql:8.0 mysqldump "${dump_args[@]}"
  else
    docker run --rm --network host mysql:8.0 mysqldump "${dump_args[@]}"
  fi
}

dump_schema \
  | sed -e '/^\/\*!40101 SET @OLD_CHARACTER_SET_CLIENT/d' \
        -e '/^\/\*!40101 SET @OLD_CHARACTER_SET_RESULTS/d' \
        -e '/^\/\*!40101 SET @OLD_COLLATION_CONNECTION/d' \
        -e '/^\/\*!40103 SET @OLD_TIME_ZONE/d' \
        -e '/^\/\*!40014 SET @OLD_UNIQUE_CHECKS/d' \
        -e '/^\/\*!40014 SET @OLD_FOREIGN_KEY_CHECKS/d' \
        -e '/^\/\*!40101 SET @OLD_SQL_MODE/d' \
        -e '/^\/\*!40111 SET @OLD_SQL_NOTES/d' \
        -e '/^\/\*!40101 SET CHARACTER_SET_CLIENT/d' \
        -e '/^\/\*!40101 SET CHARACTER_SET_RESULTS/d' \
        -e '/^\/\*!40101 SET COLLATION_CONNECTION/d' \
        -e '/^\/\*!40103 SET TIME_ZONE/d' \
        -e '/^\/\*!40014 SET UNIQUE_CHECKS/d' \
        -e '/^\/\*!40014 SET FOREIGN_KEY_CHECKS/d' \
        -e '/^\/\*!40101 SET SQL_MODE/d' \
        -e '/^\/\*!40111 SET SQL_NOTES/d' \
  > "$SNAPSHOT_FILE.tmp"

{
  echo "-- ============================================================"
  echo "-- OpenTCS Plus Schema Snapshot (auto-generated, DO NOT EDIT)"
  echo "-- Generated: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  echo "-- ============================================================"
  echo ""
  echo "SET NAMES utf8mb4;"
  echo ""
  cat "$SNAPSHOT_FILE.tmp"
} > "$SNAPSHOT_FILE"
rm -f "$SNAPSHOT_FILE.tmp"

CHECKSUM=$(shasum -a 256 "$SNAPSHOT_FILE" | awk '{print $1}')
LATEST_VERSION=$(cd "$PROJECT_ROOT" && mvn -q -N flyway:info \
  -Dflyway.url="$JDBC_URL" \
  -Dflyway.user="$FLYWAY_USER" \
  -Dflyway.password="$FLYWAY_PASSWORD" 2>/dev/null \
  | grep -E '^\| [0-9A-Za-z]' | grep Versioned | tail -1 | awk -F'|' '{print $3}' | tr -d ' ' || echo "1.0.0")

cat > "$META_FILE" <<EOF
{
  "generatedAt": "$(date -u +"%Y-%m-%dT%H:%M:%SZ")",
  "latestVersion": "${LATEST_VERSION}",
  "checksumSha256": "${CHECKSUM}",
  "database": "${FLYWAY_DATABASE}"
}
EOF

echo "[3/4] Snapshot written: $SNAPSHOT_FILE"
echo "[4/4] Meta written: $META_FILE (checksum=${CHECKSUM:0:12}...)"
