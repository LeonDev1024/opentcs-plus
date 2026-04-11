package org.opentcs.order.application.bo;

import lombok.Data;

/**
 * 创建运输订单的命令对象
 * 替代通过逗号拼接的 destinations 字段传参方式
 */
@Data
public class CreateOrderCommand {

    /** 订单名称（可选，为空时系统自动生成） */
    private String name;

    /** 起始点 ID */
    private String sourcePoint;

    /** 目标点 ID */
    private String destPoint;

    /** 指定车辆 ID（可选，为空时由调度器自动分配） */
    private String intendedVehicle;
}
