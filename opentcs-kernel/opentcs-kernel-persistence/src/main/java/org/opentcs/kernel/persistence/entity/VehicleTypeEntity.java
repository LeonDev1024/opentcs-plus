package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.opentcs.common.mybatis.core.domain.ConfigEntity;

import java.math.BigDecimal;

/**
 * 车辆类型数据模型
 * 配置表，简化审计字段
 */
@Data
@TableName("vehicle_type")
public class VehicleTypeEntity extends ConfigEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 车辆类型名称
     */
    private String name;

    /**
     * 车辆长度
     */
    private BigDecimal length;

    /**
     * 车辆宽度
     */
    private BigDecimal width;

    /**
     * 车辆高度
     */
    private BigDecimal height;

    /**
     * 最大速度
     */
    private BigDecimal maxVelocity;

    /**
     * 最大反向速度
     */
    private BigDecimal maxReverseVelocity;

    /**
     * 能量级别
     */
    private BigDecimal energyLevel;

    /**
     * 允许的订单
     */
    private String allowedOrders;

    /**
     * 允许的外围设备操作
     */
    private String allowedPeripheralOperations;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 扩展属性
     */
    private String properties;
}
