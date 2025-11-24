package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 地图模型实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("plant_model")
public class PlantModel extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 地图模型ID, 系统生成随机的字符串
     */
    private String plantModelId;

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
     * 版本号，这个字段主要作为数据库乐观锁的
     */
    @Version
    private Long version;

    /**
     * 增加描述字段，用于描述地图模型
     */
    private String description;
}