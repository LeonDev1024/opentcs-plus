#!/usr/bin/env bash
# ============================================================
# 校验仓库内 schema-snapshot.sql 是否与 Flyway 迁移结果一致
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
SNAPSHOT_DIR="$PROJECT_ROOT/db/snapshot"
COMMITTED="$SNAPSHOT_DIR/schema-snapshot.sql"
GENERATED="$SNAPSHOT_DIR/.schema-snapshot.generated.sql"

if [[ ! -f "$COMMITTED" ]]; then
  echo "ERROR: Missing committed snapshot: $COMMITTED"
  echo "Run: ./script/db/generate-snapshot.sh"
  exit 1
fi

export FLYWAY_HOST="${FLYWAY_HOST:-127.0.0.1}"
export FLYWAY_PORT="${FLYWAY_PORT:-3306}"
export FLYWAY_DATABASE="${FLYWAY_DATABASE:-opentcsplus}"
export FLYWAY_USER="${FLYWAY_USER:-root}"
if [[ -z "${FLYWAY_PASSWORD+x}" ]]; then
  export FLYWAY_PASSWORD=""
fi

cp "$COMMITTED" "$SNAPSHOT_DIR/.schema-snapshot.committed.backup.sql"

echo "Regenerating snapshot for comparison..."
"$SCRIPT_DIR/generate-snapshot.sh"

cp "$SNAPSHOT_DIR/schema-snapshot.sql" "$GENERATED"
mv "$SNAPSHOT_DIR/.schema-snapshot.committed.backup.sql" "$COMMITTED"

# 规范化：去掉头部注释与空行后比较
normalize() {
  sed -e '/^--/d' -e '/^$/d' "$1" | tr -d '\r'
}

normalize "$COMMITTED" > "$SNAPSHOT_DIR/.committed.norm"
normalize "$GENERATED" > "$SNAPSHOT_DIR/.generated.norm"

if diff -u "$SNAPSHOT_DIR/.committed.norm" "$SNAPSHOT_DIR/.generated.norm" > "$SNAPSHOT_DIR/.snapshot.diff"; then
  echo "OK: schema-snapshot.sql matches Flyway migration output."
  rm -f "$SNAPSHOT_DIR/.committed.norm" "$SNAPSHOT_DIR/.generated.norm" \
        "$SNAPSHOT_DIR/.schema-snapshot.committed.norm.sql" "$GENERATED" \
        "$SNAPSHOT_DIR/.snapshot.diff"
  exit 0
fi

echo "ERROR: schema-snapshot.sql is out of date."
echo "Diff saved to: $SNAPSHOT_DIR/.snapshot.diff"
echo "Run ./script/db/generate-snapshot.sh and commit db/snapshot/"
exit 1
