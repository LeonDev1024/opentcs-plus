package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 楼层 floor
 * @author lyc
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("floor")
public class Floor {

    /**
     * 楼层ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属建筑ID
     */
    @TableField("building_id")
    private Long buildingId;

    /**
     * 楼层数字编号（-1, 0, 1, 2...）
     */
    @TableField("floor_number")
    private Integer floorNumber;

    /**
     * 楼层编码（B1, 1F, 2F）
     */
    @TableField("floor_code")
    private String floorCode;

    /**
     * 楼层名称（如：一楼门诊区）
     */
    @TableField("floor_name")
    private String floorName;

    /**
     * 楼层类型：GENERAL-普通, UNDERGROUND-地下, SPECIAL-特殊
     */
    @TableField("floor_type")
    private String floorType;

    /**
     * 楼层描述
     */
    @TableField("description")
    private String description;

    /**
     * 楼层布局宽度（米）
     */
    @TableField("layout_width")
    private BigDecimal layoutWidth;

    /**
     * 楼层布局高度（米）
     */
    @TableField("layout_height")
    private BigDecimal layoutHeight;

    /**
     * 楼层比例尺
     */
    @TableField("scale")
    private BigDecimal scale;

    /**
     * 扩展属性
     */
    @TableField("properties")
    private String properties;

    /**
     * 是否激活
     */
    @TableField("is_active")
    private Boolean isActive;

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