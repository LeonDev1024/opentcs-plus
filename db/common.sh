#!/usr/bin/env bash
# 本地 Flyway 环境变量（默认：127.0.0.1 / root / 无密码）

export FLYWAY_HOST="${FLYWAY_HOST:-127.0.0.1}"
export FLYWAY_PORT="${FLYWAY_PORT:-3306}"
export FLYWAY_DATABASE="${FLYWAY_DATABASE:-opentcsplus}"
export FLYWAY_USER="${FLYWAY_USER:-root}"
if [[ -z "${FLYWAY_PASSWORD+x}" ]]; then
  export FLYWAY_PASSWORD=""
fi

flyway_jdbc_url() {
  echo "jdbc:mysql://${FLYWAY_HOST}:${FLYWAY_PORT}/${FLYWAY_DATABASE}?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true"
}
