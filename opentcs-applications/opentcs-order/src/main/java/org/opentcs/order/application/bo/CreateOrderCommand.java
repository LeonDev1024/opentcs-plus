package org.opentcs.order.application.bo;

import lombok.Data;

/**
 * 创建运输订单的命令对象（极简）
 */
@Data
public class CreateOrderCommand {

    /** 订单名称（可选，为空时系统自动生成） */
    private String name;

    /** 外部订单号（可选） */
    private String externalOrderNo;

    /** 起始点 ID */
    private String sourcePoint;

    /** 目标点 ID */
    private String destPoint;

    /** 指定车辆名称（可选，为空时由调度器自动分配） */
    private String intendedVehicle;

    /** 优先级，越大越优先（可选） */
    private Integer priority;

    /** 预约/截止时间（epoch millis，可选） */
    private Long deadline;

    /** 备注（可选，最长 200） */
    private String remark;

    /** 任务模板号（可选，写入 properties） */
    private String templateCode;
}
