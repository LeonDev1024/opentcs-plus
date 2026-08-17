#!/usr/bin/env bash
# ============================================================
# Docker 部署前执行 Flyway 迁移
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE=""

for candidate in "$SCRIPT_DIR" "$(dirname "$SCRIPT_DIR")"; do
  if [[ -f "$candidate/.env" ]]; then
    ENV_FILE="$candidate/.env"
    break
  fi
done

DB_DIR=""
for candidate in "$SCRIPT_DIR" "$(dirname "$SCRIPT_DIR")" "$(cd "$SCRIPT_DIR/../.." 2>/dev/null && pwd)"; do
  if [[ -d "$candidate/db/migration" ]]; then
    DB_DIR="$candidate"
    break
  fi
done

if [[ -z "$DB_DIR" ]]; then
  echo "ERROR: db/migration not found"
  exit 1
fi

if [[ -n "$ENV_FILE" ]]; then
  # shellcheck disable=SC1090
  source "$ENV_FILE"
fi

MYSQL_HOST="${MYSQL_HOST:-mysql}"
MYSQL_PORT="${MYSQL_CONTAINER_PORT:-3306}"
MYSQL_DATABASE="${MYSQL_DATABASE:-opentcsplus}"
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-MySQL@2024!Root}"
FLYWAY_IMAGE="${FLYWAY_IMAGE:-flyway/flyway:10-alpine}"
NETWORK="${DOCKER_NETWORK:-opentcs-net}"

echo "Flyway migrate -> ${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}"

docker run --rm \
  --network "$NETWORK" \
  -v "$DB_DIR/db/migration:/flyway/sql/migration:ro" \
  -v "$DB_DIR/db/repeatable:/flyway/sql/repeatable:ro" \
  "$FLYWAY_IMAGE" \
  -url="jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true" \
  -user=root \
  -password="$MYSQL_ROOT_PASSWORD" \
  -locations="filesystem:/flyway/sql/migration,filesystem:/flyway/sql/repeatable" \
  -connectRetries=60 \
  migrate

echo "Database migration complete."
