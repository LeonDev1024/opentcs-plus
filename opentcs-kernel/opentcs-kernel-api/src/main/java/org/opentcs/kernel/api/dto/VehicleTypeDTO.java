package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 车辆类型数据传输对象
 */
@Data
public class VehicleTypeDTO {

    private Long id;

    private String name;

    private BigDecimal length;

    private BigDecimal width;

    private BigDecimal height;

    private BigDecimal maxVelocity;

    private BigDecimal maxReverseVelocity;

    private BigDecimal energyLevel;

    private String allowedOrders;

    private String allowedPeripheralOperations;

    private String properties;

    private Date createTime;

    private Date updateTime;
}
