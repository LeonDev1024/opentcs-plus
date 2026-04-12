package org.opentcs.vehicle.application.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 车辆业务对象（CRUD 入参/出参，应用层边界）。
 * <p>
 * Controller 层使用此对象作为请求/响应体，屏蔽持久化实体细节。
 * </p>
 */
@Data
public class VehicleBO {

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
