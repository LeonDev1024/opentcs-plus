package org.opentcs.order.persistence.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.opentcs.common.mybatis.core.domain.BusinessEntity;

import java.time.LocalDateTime;

/**
 * 运输订单数据模型
 * 业务主表，保留完整审计字段
 */
@Data
@TableName("transport_order")
public class TransportOrderEntity extends BusinessEntity {

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
     * 车辆VIN（用于前端展示）
     */
    private String vehicleVin;

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
}
