-- I4: 资源锁当前态 + 审计日志，支持紧急解锁审计与启动恢复

CREATE TABLE IF NOT EXISTS tcs_resource_lock (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lock_id VARCHAR(64) NOT NULL,
    resource_type VARCHAR(32) NOT NULL,
    resource_id VARCHAR(128) NOT NULL,
    vehicle_id VARCHAR(128) NOT NULL,
    order_id VARCHAR(128) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'HELD',
    created_at DATETIME(3) NOT NULL,
    expires_at DATETIME(3) NOT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_resource_lock PRIMARY KEY (id),
    CONSTRAINT uk_resource_lock_id UNIQUE (lock_id),
    CONSTRAINT uk_resource_lock_res UNIQUE (resource_type, resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源锁当前持有态（恢复用）';

CREATE TABLE IF NOT EXISTS tcs_resource_lock_audit (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lock_id VARCHAR(64) NOT NULL,
    resource_type VARCHAR(32) NOT NULL,
    resource_id VARCHAR(128) NOT NULL,
    vehicle_id VARCHAR(128) NULL,
    order_id VARCHAR(128) NULL,
    event_reason VARCHAR(32) NOT NULL COMMENT 'ACQUIRED/RENEWED/RELEASED/EXPIRED/FORCE_RELEASED/RESTORED',
    status VARCHAR(32) NOT NULL,
    operator_name VARCHAR(64) NULL,
    detail VARCHAR(512) NULL,
    event_time DATETIME(3) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_resource_lock_audit PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源锁审计流水';

CREATE INDEX idx_lock_audit_time ON tcs_resource_lock_audit(event_time);
CREATE INDEX idx_lock_audit_resource ON tcs_resource_lock_audit(resource_type, resource_id);
