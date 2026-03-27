package org.opentcs.map.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 路径 DTO
 */
@Data
public class PathDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 归属导航地图ID
     */
    private Long navigationMapId;

    /**
     * 路径唯一标识
     */
    private String pathId;

    /**
     * 路径名称
     */
    private String name;

    /**
     * 起始点位标识
     */
    private String sourcePointId;

    /**
     * 目标点位标识
     */
    private String destPointId;

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

    /**
     * DIRECT / ELBOW / BEZIER
     */
    private String connectionType;

    /**
     * 布局控制点
     * （控制点）用于描述路径的绘制方式
     *  定义路径在图形界面上的具体形状和走向
     *  仅当连接类型为 BEZIER时使用
     */
    private List<LayoutControlPointDTO> layoutControlPoints;

    /**
     * 布局控制点 DTO
     */
    @Data
    public static class LayoutControlPointDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private BigDecimal x;
        private BigDecimal y;
    }
}