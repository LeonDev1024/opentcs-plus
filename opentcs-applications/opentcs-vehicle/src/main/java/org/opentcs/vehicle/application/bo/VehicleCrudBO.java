package org.opentcs.vehicle.application.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 车辆查询业务对象（含关联字段，用于分页列表场景）。
 * <p>
 * 相比 {@link VehicleBO}，额外携带 {@code vehicleTypeName} 等关联展示字段。
 * </p>
 */
@Data
public class VehicleCrudBO {

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
