package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 路径实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("path")
public class Path extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属地图模型ID
     */
    private String plantModelId;

    /**
     * 路径名称
     */
    private String name;

    /**
     * 起始点位ID
     */
    private Long sourcePointId;

    /**
     * 目标点位ID
     */
    private Long destPointId;

    /**
     * 路径长度
     */
    private BigDecimal length;

    /**
     * 最大允许速度
     */
    private BigDecimal maxVelocity;

    /**
     * 最大反向速度
     */
    private BigDecimal maxReverseVelocity;

    /**
     * 路径方向类型：BIDIRECTIONAL, FORWARD, BACKWARD
     */
    private String routingType;

    /**
     * 是否被锁定
     */
    private Boolean locked;

    /**
     * 是否被阻塞
     */
    private Boolean isBlocked;

    /**
     * 扩展属性
     */
    private String properties;
}