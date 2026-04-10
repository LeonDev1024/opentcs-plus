package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 车辆类型 DTO（Brand → VehicleType → Vehicle 三级层级的中间层）
 */
@Data
public class VehicleTypeDTO {

    /** 数据库主键 */
    private Long id;

    /** 领域 typeId（与 kernel-domain 中 VehicleType.typeId 对应） */
    private String typeId;

    /** 所属品牌 ID（数据库外键） */
    private Long brandId;

    /** 品牌名称（冗余展示字段） */
    private String brandName;

    private String name;

    private BigDecimal length;

    private BigDecimal width;

    private BigDecimal height;

    private BigDecimal maxVelocity;

    private BigDecimal maxReverseVelocity;

    private BigDecimal energyLevel;

    private List<String> allowedOrders = new ArrayList<>();

    private List<String> allowedPeripheralOperations = new ArrayList<>();

    private List<String> properties = new ArrayList<>();

    private Date createTime;

    private Date updateTime;
}
