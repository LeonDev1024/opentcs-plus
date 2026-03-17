package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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

    private Date createTime;

    private Date updateTime;
}
