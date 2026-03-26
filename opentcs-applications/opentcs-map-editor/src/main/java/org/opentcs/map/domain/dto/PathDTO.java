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

    /**
     * 几何连接类型（仅导入 openTCS XML 时使用）：
     * DIRECT / ELBOW / SLANTED / POLYPATH / BEZIER / BEZIER_3
     */
    private String connectionType;

    /**
     * 布局控制点（用于前端重建几何形状）
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