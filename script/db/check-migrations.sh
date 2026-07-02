#!/usr/bin/env bash
# ============================================================
# 校验 Flyway 迁移文件命名与版本号唯一性（CI / 本地）
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
MIGRATION_DIR="$PROJECT_ROOT/db/migration"
REPEATABLE_DIR="$PROJECT_ROOT/db/repeatable"

errors=0

check_name() {
  local file="$1"
  local base
  base="$(basename "$file")"
  if [[ ! "$base" =~ ^V[0-9]+(\.[0-9]+)*__[a-z0-9_]+\.sql$ ]]; then
    echo "ERROR: Invalid migration name: $base"
    echo "       Expected: V{major.minor.patch}__{snake_case}.sql"
    errors=$((errors + 1))
  fi
}

check_repeatable_name() {
  local file="$1"
  local base
  base="$(basename "$file")"
  if [[ ! "$base" =~ ^R__[a-z0-9_]+\.sql$ ]]; then
    echo "ERROR: Invalid repeatable name: $base"
    echo "       Expected: R__{snake_case}.sql"
    errors=$((errors + 1))
  fi
}

if [[ ! -d "$MIGRATION_DIR" ]]; then
  echo "ERROR: Missing directory: $MIGRATION_DIR"
  exit 1
fi

count=0
while IFS= read -r file; do
  [[ -n "$file" ]] || continue
  check_name "$file"
  count=$((count + 1))
done < <(find "$MIGRATION_DIR" -maxdepth 1 -name 'V*.sql' | sort)

dupes="$(find "$MIGRATION_DIR" -maxdepth 1 -name 'V*.sql' -print0 | while IFS= read -r -d '' f; do basename "$f"; done | sed -E 's/^V([0-9.]+)__.*/\1/' | sort | uniq -d)"
if [[ -n "$dupes" ]]; then
  echo "ERROR: Duplicate migration version(s):"
  echo "$dupes"
  errors=$((errors + 1))
fi

if [[ -d "$REPEATABLE_DIR" ]]; then
  while IFS= read -r file; do
    [[ -n "$file" ]] || continue
    check_repeatable_name "$file"
  done < <(find "$REPEATABLE_DIR" -maxdepth 1 -name 'R*.sql' | sort)
fi

if [[ "$errors" -gt 0 ]]; then
  echo ""
  echo "Found $errors migration issue(s)."
  exit 1
fi

echo "OK: ${count} versioned migration(s) validated."
