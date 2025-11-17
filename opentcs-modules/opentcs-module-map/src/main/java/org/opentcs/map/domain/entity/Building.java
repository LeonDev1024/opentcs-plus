package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 建筑物表 building
 * @author lyc
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("building")
public class Building {

    /**
     * 建筑ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 建筑编码（如：HOSPITAL_01）
     */
    @TableField("code")
    private String code;

    /**
     * 建筑名称（如：住院部）
     */
    @TableField("name")
    private String name;

    /**
     * 建筑类型：MEDICAL-医疗, ADMIN-行政, TECH-医技
     */
    @TableField("type")
    private String type;

    /**
     * 建筑地址
     */
    @TableField("address")
    private String address;

    /**
     * 总楼层数
     */
    @TableField("total_floors")
    private Integer totalFloors;

    /**
     * 地下楼层数
     */
    @TableField("underground_floors")
    private Integer undergroundFloors;

    /**
     * 建筑描述
     */
    @TableField("description")
    private String description;

    /**
     * 扩展属性（如：建筑高度、建筑面积等）
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