package org.opentcs.vehicle.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AMR 运维动作审计实体。
 */
@Data
@TableName("tcs_ops_action")
public class OpsActionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String actionId;

    private String requestId;

    private String traceId;

    private String vehicleName;

    private String actionCategory;

    private String actionType;

    /** JSON 字符串 */
    private String requestPayload;

    private String executeStatus;

    private String reasonCode;

    private String reasonMessage;

    private String operatorName;

    private LocalDateTime operatedAt;

    private LocalDateTime finishedAt;
}
