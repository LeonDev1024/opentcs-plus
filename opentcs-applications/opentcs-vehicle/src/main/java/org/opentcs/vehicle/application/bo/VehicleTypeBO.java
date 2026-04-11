package org.opentcs.vehicle.application.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 车辆类型业务对象，用于应用层与接口层之间的数据传递。
 * 仅包含业务字段，屏蔽 VehicleTypeEntity 的持久化注解和 MyBatis 类型处理器。
 */
@Data
public class VehicleTypeBO {

    /** 主键ID（查询/更新时使用，创建时为 null） */
    private Long id;

    /** 所属品牌ID */
    private Long brandId;

    /** 品牌名称（冗余展示字段） */
    private String brandName;

    /** 车辆类型名称 */
    private String name;

    /** 车辆长度（mm） */
    private BigDecimal length;

    /** 车辆宽度（mm） */
    private BigDecimal width;

    /** 车辆高度（mm） */
    private BigDecimal height;

    /** 最大速度（mm/s） */
    private BigDecimal maxVelocity;

    /** 最大反向速度（mm/s） */
    private BigDecimal maxReverseVelocity;

    /** 能量级别 */
    private BigDecimal energyLevel;

    /** 允许的订单操作 */
    private List<String> allowedOrders = new ArrayList<>();

    /** 允许的外围设备操作 */
    private List<String> allowedPeripheralOperations = new ArrayList<>();

    /** 扩展属性 */
    private List<String> properties = new ArrayList<>();
}
