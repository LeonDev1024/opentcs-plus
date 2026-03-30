package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 点位数据传输对象
 */
@Data
public class PointDTO {

    private Long id;

    private Long navigationMapId;

    private Long layerId;

    private String pointId;

    private String name;

    private BigDecimal xPosition;

    private BigDecimal yPosition;

    private BigDecimal zPosition;

    private BigDecimal vehicleOrientation;

    private String type;

    private BigDecimal radius;

    private Boolean locked;

    private Boolean isBlocked;

    private Boolean isOccupied;

    private String label;

    private String properties;

    private Date createTime;

    private Date updateTime;
}
