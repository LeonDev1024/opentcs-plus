package org.opentcs.kernel.api.dto;

import lombok.Data;
import org.opentcs.common.core.dto.PathLayoutControlPointTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 路径数据传输对象
 */
@Data
public class PathDTO {

    private Long id;

    private Long plantModelId;

    private Long navigationMapId;

    private String pathId;

    private String name;

    private String sourcePointId;

    private String destPointId;

    private BigDecimal length;

    private BigDecimal maxVelocity;

    private BigDecimal maxReverseVelocity;

    private String routingType;

    private Boolean locked;

    private Boolean isBlocked;

    private String properties;

    /**
     * 路径几何连接类型（DIRECT/ELBOW/BEZIER...）
     */
    private String connectionType;

    /**
     * 路径布局控制点（用于前端还原几何形状）
     */
    private List<PathLayoutControlPointTO> layoutControlPoints;

    private Date createTime;

    private Date updateTime;
}
