package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 运输订单数据传输对象（数据库实体）
 */
@Data
public class TransportOrderEntityDTO {

    private Long id;

    private String name;

    private String orderNo;

    private String state;

    private String intendedVehicle;

    private String processingVehicle;

    private String destinations;

    private LocalDateTime creationTime;

    private LocalDateTime finishedTime;

    private LocalDateTime deadline;

    private String properties;

    private Date createTime;

    private Date updateTime;
}
