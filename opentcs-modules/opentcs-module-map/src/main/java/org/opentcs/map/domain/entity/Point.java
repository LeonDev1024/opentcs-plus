package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 点位实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("point")
public class Point extends BaseEntity {

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
     * 点位名称
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
     * 车辆方向角度（弧度）
     */
    private BigDecimal vehicleOrientation;

    /**
     * 点位类型：HALT_POSITION, PARK_POSITION, REPORT_POSITION
     */
    private String type;

    /**
     * 点位半径
     */
    private BigDecimal radius;

    /**
     * 是否被锁定
     */
    private Boolean locked;

    /**
     * 是否被阻塞
     */
    private Boolean isBlocked;

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