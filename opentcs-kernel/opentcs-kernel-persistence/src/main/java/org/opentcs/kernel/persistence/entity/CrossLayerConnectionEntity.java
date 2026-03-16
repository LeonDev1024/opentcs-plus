package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.ConfigEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 跨层连接实体
 * 核心实体：支持电梯、传送带、提升机等跨楼层调度
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("cross_layer_connection")
public class CrossLayerConnectionEntity extends ConfigEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属工厂ID
     */
    private Long factoryModelId;

    /**
     * 连接唯一标识
     */
    private String connectionId;

    /**
     * 连接名称（如：电梯A、1号提升机）
     */
    private String name;

    /**
     * 连接类型：ELEVATOR/CONVEYOR/PHYSICAL_DOOR
     */
    private String connectionType;

    /**
     * 源地图ID
     */
    private Long sourceNavigationMapId;

    /**
     * 源点位ID（电梯口/传送带头）
     */
    private String sourcePointId;

    /**
     * 源楼层
     */
    private Integer sourceFloor;

    /**
     * 目标地图ID
     */
    private Long destNavigationMapId;

    /**
     * 目标点位ID
     */
    private String destPointId;

    /**
     * 目标楼层
     */
    private Integer destFloor;

    /**
     * 容量（电梯可同时承载车辆数）
     */
    private Integer capacity;

    /**
     * 最大承重
     */
    private BigDecimal maxWeight;

    /**
     * 运行时间（秒）
     */
    private Integer travelTime;

    /**
     * 是否可用
     */
    private Boolean available;

    /**
     * 当前负载
     */
    private Integer currentLoad;

    /**
     * 扩展属性
     */
    private String properties;
}
