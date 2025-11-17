package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 地图模型 plant_model
 * @author lyc
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("plant_model")
public class PlantModel {

    /**
     * 地图ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属建筑ID
     */
    @TableField("building_id")
    private Long buildingId;

    /**
     * 所属楼层ID
     */
    @TableField("floor_id")
    private Long floorId;

    /**
     * 地图名称
     */
    @TableField("name")
    private String name;

    /**
     * 地图版本
     */
    @TableField("model_version")
    private String modelVersion;

    /**
     * 地图比例尺
     */
    @TableField("scale")
    private BigDecimal scale;

    /**
     * 地图布局宽度（米）
     */
    @TableField("layout_width")
    private BigDecimal layoutWidth;

    /**
     * 地图布局高度（米）
     */
    @TableField("layout_height")
    private BigDecimal layoutHeight;

    /**
     * 坐标参考系统
     */
    @TableField("crs")
    private String crs;

    /**
     * 地图边界
     */
    @TableField("bounds")
    private String bounds;

    /**
     * 地图状态
     */
    @TableField("model_state")
    private String modelState;

    /**
     * 地图描述
     */
    @TableField("description")
    private String description;

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

    /**
     * 版本号（乐观锁）
     */
    @TableField("version")
    private Long version;
}