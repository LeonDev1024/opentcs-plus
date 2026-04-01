package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.DataEntity;

import java.math.BigDecimal;

/**
 * 位置数据模型
 * 地图数据表，简化审计字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("location")
public class LocationEntity extends DataEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 归属导航地图ID
     */
    private Long navigationMapId;

    /**
     * 位置类型ID
     */
    private Long locationTypeId;

    /**
     * 归属图层ID
     */
    private Long layerId;

    /**
     * 位置唯一标识
     */
    private String locationId;

    /**
     * 位置名称
     */
    private String name;

    /**
     * X坐标
     */
    @TableField("position_x")
    private BigDecimal xPosition;

    /**
     * Y坐标
     */
    @TableField("position_y")
    private BigDecimal yPosition;

    /**
     * Z坐标
     */
    @TableField("position_z")
    private BigDecimal zPosition;

    /**
     * 是否被锁定
     */
    private Boolean locked;

    /**
     * 是否被占用
     */
    private Boolean isOccupied;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 位置布局数据（JSON）。
     */
    private String layout;
}
