package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 车辆 CRUD 数据传输对象
 */
@Data
public class VehicleCrudDTO {

    private Long id;

    private String name;

    private String vinCode;

    private Long vehicleTypeId;

    private String vehicleTypeName;

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
