#!/usr/bin/env bash
# 本地 Flyway 环境变量（默认：127.0.0.1 / root / 无密码）
# 如需覆盖，可复制为 local.env（已 gitignore）或 export 环境变量

export FLYWAY_HOST="${FLYWAY_HOST:-127.0.0.1}"
export FLYWAY_PORT="${FLYWAY_PORT:-3306}"
export FLYWAY_DATABASE="${FLYWAY_DATABASE:-opentcsplus}"
export FLYWAY_USER="${FLYWAY_USER:-root}"
# 未设置时默认为空密码（本地开发库）
if [[ -z "${FLYWAY_PASSWORD+x}" ]]; then
  export FLYWAY_PASSWORD=""
fi

flyway_jdbc_url() {
  echo "jdbc:mysql://${FLYWAY_HOST}:${FLYWAY_PORT}/${FLYWAY_DATABASE}?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true"
}

mysqldump_docker_args() {
  local args=(
    run --rm --network host
  )
  if [[ -n "${FLYWAY_PASSWORD}" ]]; then
    args+=(-e "MYSQL_PWD=${FLYWAY_PASSWORD}")
  fi
  args+=(
    "${MYSQL_IMAGE:-mysql:8.0}"
    mysqldump
    --no-data
    --routines
    --triggers
    --skip-add-drop-table
    --set-gtid-purged=OFF
    -h "${FLYWAY_HOST}"
    -P "${FLYWAY_PORT}"
    -u "${FLYWAY_USER}"
    "${FLYWAY_DATABASE}"
  )
  printf '%s\n' "${args[@]}"
}
