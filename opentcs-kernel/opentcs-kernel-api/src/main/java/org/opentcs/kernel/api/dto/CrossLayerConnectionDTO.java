package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 跨层连接数据传输对象
 */
@Data
public class CrossLayerConnectionDTO {

    private Long id;

    private Long factoryModelId;

    private String connectionId;

    private String name;

    private String connectionType;

    private Long sourceNavigationMapId;

    private String sourcePointId;

    private Integer sourceFloor;

    private Long destNavigationMapId;

    private String destPointId;

    private Integer destFloor;

    private Integer capacity;

    private BigDecimal maxWeight;

    private Integer travelTime;

    private Boolean available;

    private Integer currentLoad;

    private String properties;

    private Date createTime;

    private Date updateTime;
}
