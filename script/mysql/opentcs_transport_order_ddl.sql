-- 运输订单表
CREATE TABLE IF NOT EXISTS transport_order (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) DEFAULT NULL COMMENT '订单名称',
    order_no VARCHAR(100) DEFAULT NULL COMMENT '订单编号',
    state VARCHAR(50) DEFAULT 'RAW' COMMENT '订单状态：RAW, ACTIVE, FINISHED, FAILED',
    intended_vehicle VARCHAR(100) DEFAULT NULL COMMENT '指定车辆',
    processing_vehicle VARCHAR(100) DEFAULT NULL COMMENT '处理车辆',
    vehicle_vin VARCHAR(100) DEFAULT NULL COMMENT '车辆VIN',
    destinations TEXT DEFAULT NULL COMMENT '目的地序列',
    creation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    finished_time DATETIME DEFAULT NULL COMMENT '完成时间',
    deadline DATETIME DEFAULT NULL COMMENT '截止时间',
    properties TEXT DEFAULT NULL COMMENT '扩展属性',
    -- 审计字段
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 0,
    del_flag CHAR(1) DEFAULT '0',
    status CHAR(1) DEFAULT '0',
    INDEX idx_order_no (order_no),
    INDEX idx_processing_vehicle (processing_vehicle),
    INDEX idx_state (state)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运输订单表';
