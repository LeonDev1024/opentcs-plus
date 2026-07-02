#!/usr/bin/env bash
# ============================================================
# 创建新的 Flyway 版本化迁移文件
# 用法: ./script/db/new-migration.sh 1.0.3 add_vehicle_battery_column
# ============================================================
set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 <version> <description_snake_case>"
  echo "Example: $0 1.0.3 add_vehicle_battery_column"
  exit 1
fi

VERSION="$1"
DESC="$2"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
TARGET="$PROJECT_ROOT/db/migration/V${VERSION}__${DESC}.sql"

if [[ ! "$VERSION" =~ ^[0-9]+(\.[0-9]+)*$ ]]; then
  echo "ERROR: version must look like 1.0.3"
  exit 1
fi

if [[ ! "$DESC" =~ ^[a-z0-9_]+$ ]]; then
  echo "ERROR: description must be snake_case"
  exit 1
fi

if [[ -f "$TARGET" ]]; then
  echo "ERROR: already exists: $TARGET"
  exit 1
fi

cat > "$TARGET" <<EOF
-- ============================================================
-- V${VERSION} ${DESC//_/ }
-- ============================================================

USE opentcsplus;

-- TODO: write migration SQL

EOF

echo "Created: $TARGET"
echo "Next:"
echo "  ./script/db/dev-db.sh migrate"
echo "  ./script/db/dev-db.sh snapshot"
