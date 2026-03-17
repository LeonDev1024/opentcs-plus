package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 车辆数据传输对象（数据库实体）
 */
@Data
public class VehicleEntityDTO {

    private Long id;

    private String name;

    private String vinCode;

    private Long vehicleTypeId;

    private String currentPosition;

    private String nextPosition;

    private String state;

    private String integrationLevel;

    private BigDecimal energyLevel;

    private String currentTransportOrder;

    private String properties;

    private Date createTime;

    private Date updateTime;
}
