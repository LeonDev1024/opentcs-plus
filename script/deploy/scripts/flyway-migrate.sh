#!/usr/bin/env bash
# ============================================================
# Docker / 服务器部署前执行 Flyway 迁移
# 在部署包根目录运行，需存在 db/migration 与 db/repeatable
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${SCRIPT_DIR}/.env"

if [[ -f "$ENV_FILE" ]]; then
  # shellcheck disable=SC1090
  source "$ENV_FILE"
fi

MYSQL_HOST="${MYSQL_HOST:-mysql}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_DATABASE="${MYSQL_DATABASE:-opentcsplus}"
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-MySQL@2024!Root}"
FLYWAY_IMAGE="${FLYWAY_IMAGE:-flyway/flyway:10-alpine}"
NETWORK="${DOCKER_NETWORK:-opentcs-net}"

if [[ ! -d "$SCRIPT_DIR/db/migration" ]]; then
  echo "ERROR: $SCRIPT_DIR/db/migration not found"
  exit 1
fi

echo "Flyway migrate -> ${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}"

docker run --rm \
  --network "$NETWORK" \
  -v "$SCRIPT_DIR/db/migration:/flyway/sql/migration:ro" \
  -v "$SCRIPT_DIR/db/repeatable:/flyway/sql/repeatable:ro" \
  "$FLYWAY_IMAGE" \
  -url="jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true" \
  -user=root \
  -password="$MYSQL_ROOT_PASSWORD" \
  -locations="filesystem:/flyway/sql/migration,filesystem:/flyway/sql/repeatable" \
  -connectRetries=60 \
  migrate

echo "Database migration complete."
