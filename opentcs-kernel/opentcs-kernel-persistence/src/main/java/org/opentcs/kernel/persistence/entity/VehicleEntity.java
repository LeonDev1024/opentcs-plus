package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BusinessEntity;

import java.math.BigDecimal;

/**
 * 车辆数据模型
 * 业务主表，保留完整审计字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("vehicle")
public class VehicleEntity extends BusinessEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 车辆名称
     */
    private String name;

    /**
     * 车辆VIN码
     */
    private String vinCode;

    /**
     * 车辆类型ID
     */
    private Long vehicleTypeId;

    /**
     * 当前位置
     */
    private String currentPosition;

    /**
     * 下一个位置
     */
    private String nextPosition;

    /**
     * 车辆状态：UNKNOWN, UNAVAILABLE, IDLE, CHARGING, WORKING, ERROR
     */
    private String state;

    /**
     * 集成级别：TO_BE_IGNORED, TO_BE_NOTICED, TO_BE_RESPECTED, TO_BE_UTILIZED
     */
    private String integrationLevel;

    /**
     * 能量级别
     */
    private BigDecimal energyLevel;

    /**
     * 当前运输订单
     */
    private String currentTransportOrder;

    /**
     * 扩展属性
     */
    private String properties;
}
