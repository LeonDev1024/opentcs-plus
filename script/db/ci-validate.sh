#!/usr/bin/env bash
# ============================================================
# CI 入口：命名检查 → Flyway migrate → 快照校验
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

export FLYWAY_HOST="${FLYWAY_HOST:-127.0.0.1}"
export FLYWAY_PORT="${FLYWAY_PORT:-3306}"
export FLYWAY_DATABASE="${FLYWAY_DATABASE:-opentcsplus}"
export FLYWAY_USER="${FLYWAY_USER:-root}"
if [[ -z "${FLYWAY_PASSWORD+x}" ]]; then
  export FLYWAY_PASSWORD="${FLYWAY_PASSWORD:-}"
fi

echo "==> [1/3] Check migration naming"
"$SCRIPT_DIR/check-migrations.sh"

echo "==> [2/3] Flyway migrate"
"$SCRIPT_DIR/dev-db.sh" migrate

echo "==> [3/3] Validate schema snapshot"
"$SCRIPT_DIR/validate-snapshot.sh"

echo "CI database validation passed."
