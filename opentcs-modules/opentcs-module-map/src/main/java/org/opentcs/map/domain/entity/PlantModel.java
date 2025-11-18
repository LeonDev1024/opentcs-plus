package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 地图模型实体类
 */
@Data
@TableName("plant_model")
public class PlantModel {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 地图模型名称，唯一标识
     */
    private String name;

    /**
     * 模型版本
     */
    private String modelVersion;

    /**
     * 长度单位：mm, cm, m
     */
    private String lengthUnit;

    /**
     * 比例尺
     */
    private BigDecimal scale;

    /**
     * 布局宽度
     */
    private BigDecimal layoutWidth;

    /**
     * 布局高度
     */
    private BigDecimal layoutHeight;

    /**
     * 地图状态：UNLOADED, LOADING, LOADED, LOCKED, ERROR
     */
    private String modelState;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 版本号
     */
    @Version
    private Long version;
}