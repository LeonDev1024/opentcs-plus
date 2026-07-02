#!/usr/bin/env bash
# ============================================================
# 生产存量库 Flyway baseline（已有表结构、无 flyway_schema_history）
#
# 用法（先备份！）:
#   FLYWAY_HOST=... FLYWAY_DATABASE=opentcsplus FLYWAY_PASSWORD=... \\
#     ./script/db/baseline-prod.sh 1.0.2
#
# 然后执行增量:
#   ./script/db/dev-db.sh migrate
# ============================================================
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <baseline_version>"
  echo "Example: $0 1.0.2"
  exit 1
fi

BASELINE_VERSION="$1"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# shellcheck source=script/db/common.sh
source "$SCRIPT_DIR/common.sh"
if [[ -f "$SCRIPT_DIR/local.env" ]]; then
  # shellcheck disable=SC1091
  source "$SCRIPT_DIR/local.env"
fi

JDBC_URL="$(flyway_jdbc_url)"

echo "WARNING: This will baseline existing schema at version ${BASELINE_VERSION}"
echo "Target: ${JDBC_URL}"
read -r -p "Have you backed up the database? (yes/no): " confirm
[[ "$confirm" == "yes" ]] || { echo "Aborted."; exit 1; }

(
  cd "$PROJECT_ROOT"
  mvn -N flyway:baseline \
    -Dflyway.url="$JDBC_URL" \
    -Dflyway.user="$FLYWAY_USER" \
    -Dflyway.password="$FLYWAY_PASSWORD" \
    -Dflyway.baselineVersion="$BASELINE_VERSION" \
    -Dflyway.baselineDescription="production_existing_schema"
)

echo "Baseline complete. Run ./script/db/dev-db.sh info to verify."
