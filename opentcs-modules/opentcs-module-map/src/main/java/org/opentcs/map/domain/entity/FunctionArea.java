package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 功能区域 function_area
 * @author lyc
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("function_area")
public class FunctionArea {

    /**
     * 区域ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属地图ID
     */
    @TableField("plant_model_id")
    private Long plantModelId;

    /**
     * 区域名称
     */
    @TableField("name")
    private String name;

    /**
     * 区域编码
     */
    @TableField("code")
    private String code;

    /**
     * 区域类型
     */
    @TableField("area_type")
    private String areaType;

    /**
     * 区域边界
     */
    @TableField("boundary")
    private String boundary;

    /**
     * 所属科室
     */
    @TableField("department")
    private String department;

    /**
     * 功能描述
     */
    @TableField("functional_description")
    private String functionalDescription;

    /**
     * 访问权限
     */
    @TableField("access_level")
    private String accessLevel;

    /**
     * 最大容量
     */
    @TableField("max_capacity")
    private Integer maxCapacity;

    /**
     * 当前占用数
     */
    @TableField("current_occupancy")
    private Integer currentOccupancy;

    /**
     * 扩展属性
     */
    @TableField("properties")
    private String properties;

    /**
     * 是否激活
     */
    @TableField("is_active")
    private Boolean active;

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