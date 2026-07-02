#!/usr/bin/env bash
# ============================================================
# 本地一键：Docker MySQL + Flyway 迁移 + 开发引导数据
# 用法: ./script/db/dev-db-up.sh [--rebuild]
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=script/db/common.sh
source "$SCRIPT_DIR/common.sh"
if [[ -f "$SCRIPT_DIR/local.env" ]]; then
  # shellcheck disable=SC1091
  source "$SCRIPT_DIR/local.env"
fi

CONTAINER_NAME="${DEV_MYSQL_CONTAINER:-opentcs-dev-mysql}"
MYSQL_IMAGE="${MYSQL_IMAGE:-mysql:8.0}"
REBUILD="${1:-}"

if ! command -v docker >/dev/null 2>&1; then
  echo "ERROR: docker is required for dev-db-up.sh"
  echo "Use ./script/db/dev-db.sh migrate if you already have MySQL at ${FLYWAY_HOST}:${FLYWAY_PORT}"
  exit 1
fi

if [[ "$REBUILD" == "--rebuild" ]]; then
  docker rm -f "$CONTAINER_NAME" >/dev/null 2>&1 || true
fi

if ! docker ps --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
  if docker ps -a --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
    echo "Starting existing container: $CONTAINER_NAME"
    docker start "$CONTAINER_NAME" >/dev/null
  else
    echo "Creating dev MySQL container: $CONTAINER_NAME (port ${FLYWAY_PORT})"
    docker run -d \
      --name "$CONTAINER_NAME" \
      -e MYSQL_ALLOW_EMPTY_PASSWORD=yes \
      -e MYSQL_DATABASE="${FLYWAY_DATABASE}" \
      -p "${FLYWAY_PORT}:3306" \
      "$MYSQL_IMAGE" \
      --character-set-server=utf8mb4 \
      --collation-server=utf8mb4_unicode_ci \
      >/dev/null
  fi
fi

echo "Waiting for MySQL..."
for _ in $(seq 1 30); do
  if docker exec "$CONTAINER_NAME" mysqladmin ping -h127.0.0.1 --silent 2>/dev/null; then
    break
  fi
  sleep 2
done

if [[ "$REBUILD" == "--rebuild" ]]; then
  exec "$SCRIPT_DIR/dev-db.sh" rebuild
fi

exec "$SCRIPT_DIR/dev-db.sh" migrate
