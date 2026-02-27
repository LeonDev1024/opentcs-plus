package org.opentcs.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.opentcs.common.mybatis.core.domain.BaseEntity;

import java.time.LocalDateTime;

/**
 * 运输订单实体类
 */
@Data
@TableName("transport_order")
public class TransportOrder extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单名称
     */
    private String name;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单状态：RAW, ACTIVE, FINISHED, FAILED
     */
    private String state;

    /**
     * 指定车辆
     */
    private String intendedVehicle;

    /**
     * 处理车辆
     */
    private String processingVehicle;

    /**
     * 目的地序列
     */
    private String destinations;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime creationTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishedTime;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 临时字段：车辆VIN码（前端传递）
     */
    @TableField(exist = false)
    private String vehicleVin;

    /**
     * 临时字段：订单状态（前端传递）
     */
    @TableField(exist = false)
    private String status;
}