package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 跨楼层连接 floor_connection
 * @author lyc
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("floor_connection")
public class FloorConnection {

    /**
     * 连接ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 连接名称
     */
    @TableField("name")
    private String name;

    /**
     * 连接编码
     */
    @TableField("code")
    private String code;

    /**
     * 连接类型
     */
    @TableField("connection_type")
    private String connectionType;

    /**
     * 起始地图ID
     */
    @TableField("from_plant_model_id")
    private Long fromPlantModelId;

    /**
     * 起始点位ID
     */
    @TableField("from_point_id")
    private Long fromPointId;

    /**
     * 目标地图ID
     */
    @TableField("to_plant_model_id")
    private Long toPlantModelId;

    /**
     * 目标点位ID
     */
    @TableField("to_point_id")
    private Long toPointId;

    /**
     * 通行时间（秒）
     */
    @TableField("travel_time")
    private Integer travelTime;

    /**
     * 同时通行容量
     */
    @TableField("capacity")
    private Integer capacity;

    /**
     * 当前使用量
     */
    @TableField("current_usage")
    private Integer currentUsage;

    /**
     * 访问权限
     */
    @TableField("access_level")
    private String accessLevel;

    /**
     * 是否激活
     */
    @TableField("is_active")
    private Boolean active;

    /**
     * 扩展属性
     */
    @TableField("properties")
    private String properties;

    /**
     * 创建人
     */
    @TableField("created_by")
    private String createdBy;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    @TableField("updated_by")
    private String updatedBy;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private LocalDateTime updatedTime;
}