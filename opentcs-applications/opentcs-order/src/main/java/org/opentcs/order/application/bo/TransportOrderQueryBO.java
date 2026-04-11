package org.opentcs.order.application.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 运输订单查询/响应业务对象。
 * 用于接口层与应用层之间的数据传递，屏蔽 TransportOrderEntity 的持久化注解。
 */
@Data
public class TransportOrderQueryBO {

    private Long id;

    private String name;

    private String orderNo;

    /** 订单状态：RAW, ACTIVE, FINISHED, FAILED, CANCELLED */
    private String state;

    private String intendedVehicle;

    private String processingVehicle;

    private String vehicleVin;

    private String destinations;

    private LocalDateTime creationTime;

    private LocalDateTime finishedTime;

    private LocalDateTime deadline;

    private String properties;
}
