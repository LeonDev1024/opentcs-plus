package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.ConfigEntity;

import java.util.Date;

/**
 * 电梯调度记录实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("elevator_schedule")
public class ElevatorScheduleEntity extends ConfigEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 跨层连接ID
     */
    private String connectionId;

    /**
     * 预约车辆ID
     */
    private Long vehicleId;

    /**
     * 预约车辆名称
     */
    private String vehicleName;

    /**
     * 源楼层
     */
    private Integer sourceFloor;

    /**
     * 目标楼层
     */
    private Integer destFloor;

    /**
     * 调度类型：RESERVE/CANCEL/COMPLETE
     */
    private String scheduleType;

    /**
     * 预计接载时间
     */
    private Date pickupTime;

    /**
     * 预计送达时间
     */
    private Date deliveryTime;

    /**
     * 实际接载时间
     */
    private Date actualPickupTime;

    /**
     * 实际送达时间
     */
    private Date actualDeliveryTime;

    /**
     * 状态：PENDING/RUNNING/COMPLETED/CANCELLED
     */
    private String status;

    /**
     * 扩展属性
     */
    private String properties;
}
