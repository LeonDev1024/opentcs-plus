package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 位置数据模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("location")
public class LocationEntity extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属地图模型ID
     */
    private Long plantModelId;

    /**
     * 位置类型ID
     */
    private Long locationTypeId;

    /**
     * 位置名称
     */
    private String name;

    /**
     * X坐标
     */
    private BigDecimal xPosition;

    /**
     * Y坐标
     */
    private BigDecimal yPosition;

    /**
     * Z坐标
     */
    private BigDecimal zPosition;

    /**
     * 车辆方向
     */
    private BigDecimal vehicleOrientation;

    /**
     * 是否被锁定
     */
    private Boolean locked;

    /**
     * 是否被占用
     */
    private Boolean isOccupied;

    /**
     * 标签
     */
    private String label;

    /**
     * 扩展属性
     */
    private String properties;
}
