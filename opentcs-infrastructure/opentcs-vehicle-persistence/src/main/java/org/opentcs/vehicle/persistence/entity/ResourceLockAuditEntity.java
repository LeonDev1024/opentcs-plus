package org.opentcs.vehicle.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tcs_resource_lock_audit")
public class ResourceLockAuditEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String lockId;
    private String resourceType;
    private String resourceId;
    private String vehicleId;
    private String orderId;
    private String eventReason;
    private String status;
    private String operatorName;
    private String detail;
    private LocalDateTime eventTime;
}
