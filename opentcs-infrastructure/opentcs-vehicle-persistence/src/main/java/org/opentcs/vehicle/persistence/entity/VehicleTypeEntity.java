package org.opentcs.vehicle.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.opentcs.common.mybatis.core.domain.ConfigEntity;
import org.opentcs.common.mybatis.handler.MySqlJsonTypeHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 车辆类型数据模型
 * 配置表，完整审计字段
 */
@Data
@TableName(value = "vehicle_type", autoResultMap = true)
public class VehicleTypeEntity extends ConfigEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属品牌ID
     */
    private Long brandId;

    /**
     * 品牌名称（非数据库字段，来自JOIN查询）
     */
    @TableField(exist = false)
    private String brandName;

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
     * 允许的订单操作
     */
    @TableField(typeHandler = MySqlJsonTypeHandler.class)
    private List<String> allowedOrders = new ArrayList<>();

    /**
     * 允许的外围设备操作
     */
    @TableField(typeHandler = MySqlJsonTypeHandler.class)
    private List<String> allowedPeripheralOperations = new ArrayList<>();

    /**
     * 扩展属性
     */
    @TableField(typeHandler = MySqlJsonTypeHandler.class)
    private List<String> properties = new ArrayList<>();
}
