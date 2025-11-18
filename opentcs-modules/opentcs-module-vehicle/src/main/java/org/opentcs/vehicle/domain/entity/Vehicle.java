package org.opentcs.vehicle.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车辆实体类
 */
@Data
@TableName("vehicle")
public class Vehicle {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 车辆名称
     */
    private String name;

    /**
     * 车辆类型ID
     */
    private Long vehicleTypeId;

    /**
     * 当前位置
     */
    private String currentPosition;

    /**
     * 下一个位置
     */
    private String nextPosition;

    /**
     * 车辆状态：UNKNOWN, UNAVAILABLE, IDLE, CHARGING, WORKING, ERROR
     */
    private String state;

    /**
     * 集成级别：TO_BE_IGNORED, TO_BE_NOTICED, TO_BE_RESPECTED, TO_BE_UTILIZED
     */
    private String integrationLevel;

    /**
     * 能量级别
     */
    private BigDecimal energyLevel;

    /**
     * 当前运输订单
     */
    private String currentTransportOrder;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}