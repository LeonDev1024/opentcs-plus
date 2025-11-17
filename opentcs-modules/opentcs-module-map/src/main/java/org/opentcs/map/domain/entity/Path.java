package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 路径 path
 * @author lyc
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("path")
public class Path {

    /**
     * 路径ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属地图ID
     */
    @TableField("plant_model_id")
    private Long plantModelId;

    /**
     * 路径名称
     */
    @TableField("name")
    private String name;

    /**
     * 路径编码
     */
    @TableField("code")
    private String code;

    /**
     * 起始点位ID
     */
    @TableField("source_point_id")
    private Long sourcePointId;

    /**
     * 目标点位ID
     */
    @TableField("dest_point_id")
    private Long destPointId;

    /**
     * 路径轨迹
     */
    @TableField("trajectory")
    private String trajectory;

    /**
     * 路径长度
     */
    @TableField("length")
    private BigDecimal length;

    /**
     * 路径方向类型
     */
    @TableField("routing_type")
    private String routingType;

    /**
     * 路径类型
     */
    @TableField("path_type")
    private String pathType;

    /**
     * 最大速度
     */
    @TableField("max_velocity")
    private BigDecimal maxVelocity;

    /**
     * 最大反向速度
     */
    @TableField("max_reverse_velocity")
    private BigDecimal maxReverseVelocity;

    /**
     * 最小宽度
     */
    @TableField("min_width")
    private BigDecimal minWidth;

    /**
     * 最大坡度
     */
    @TableField("max_slope")
    private BigDecimal maxSlope;

    /**
     * 访问权限级别
     */
    @TableField("access_level")
    private String accessLevel;

    /**
     * 扩展属性
     */
    @TableField("properties")
    private String properties;

    /**
     * 是否锁定
     */
    @TableField("is_locked")
    private Boolean locked;

    /**
     * 锁定持有者
     */
    @TableField("lock_holder")
    private String lockHolder;

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

    /**
     * 版本号
     */
    @TableField("version")
    private Long version;
}