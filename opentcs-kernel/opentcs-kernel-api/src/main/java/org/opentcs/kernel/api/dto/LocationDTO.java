package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 位置数据传输对象
 */
@Data
public class LocationDTO {

    private Long id;

    private Long plantModelId;

    private Long navigationMapId;

    private Long locationTypeId;

    private String locationId;

    private String name;

    private BigDecimal xPosition;

    private BigDecimal yPosition;

    private BigDecimal zPosition;

    private BigDecimal vehicleOrientation;

    private Boolean locked;

    private Boolean isOccupied;

    private String label;

    private String properties;

    private Date createTime;

    private Date updateTime;
}
