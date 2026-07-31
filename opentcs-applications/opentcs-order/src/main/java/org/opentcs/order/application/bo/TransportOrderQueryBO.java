package org.opentcs.order.application.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 运输订单查询/响应业务对象。
 */
@Data
public class TransportOrderQueryBO {

    private Long id;

    /** 任务号（创建时写入 name） */
    private String name;

    private String orderNo;

    /** 订单状态：RAW, ACTIVE, RECOVERING, FINISHED, FAILED, CANCELLED */
    private String state;

    /**
     * 业务展示状态：
     * PENDING 待执行 / DISPATCHING 寻车中 / EXECUTING 执行中 /
     * FINISHED 已完成 / CANCELLED 已取消 / PAUSED 暂停中 / FAILED 失败
     */
    private String displayState;

    private String intendedVehicle;

    private String processingVehicle;

    /** 列表展示用车辆名 */
    private String vehicleName;

    private String vehicleVin;

    private String destinations;

    /** 起点点位 */
    private String sourcePoint;

    /** 终点点位 */
    private String destPoint;

    /** 优先级（来自 properties） */
    private Integer priority;

    /** 外部订单号（创建表单「订单号」，来自 properties） */
    private String externalOrderNo;

    /** 任务模板号（来自 properties） */
    private String templateCode;

    /** 备注（来自 properties） */
    private String remark;

    private LocalDateTime creationTime;

    private LocalDateTime createTime;

    private LocalDateTime finishedTime;

    /** 预约/截止时间 */
    private LocalDateTime deadline;

    private String properties;
}
