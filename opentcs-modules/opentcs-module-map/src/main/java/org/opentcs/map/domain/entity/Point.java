package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 导航点 point
 * @author lyc
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("point")
public class Point {

    /**
     * 点位ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属地图ID
     */
    @TableField("plant_model_id")
    private Long plantModelId;

    /**
     * 点位名称
     */
    @TableField("name")
    private String name;

    /**
     * 点位编码
     */
    @TableField("code")
    private String code;

    /**
     * 点位类型
     */
    @TableField("point_type")
    private String pointType;

    /**
     * 点位位置
     */
    @TableField("position")
    private String position;

    /**
     * X坐标
     */
    @TableField("x_position")
    private BigDecimal xPosition;

    /**
     * Y坐标
     */
    @TableField("y_position")
    private BigDecimal yPosition;

    /**
     * Z坐标
     */
    @TableField("z_position")
    private BigDecimal zPosition;

    /**
     * 车辆方向
     */
    @TableField("vehicle_orientation")
    private BigDecimal vehicleOrientation;

    /**
     * 点位半径
     */
    @TableField("radius")
    private BigDecimal radius;

    /**
     * 功能类型
     */
    @TableField("functional_type")
    private String functionalType;

    /**
     * 房间号
     */
    @TableField("room_number")
    private String roomNumber;

    /**
     * 所属科室
     */
    @TableField("department")
    private String department;

    /**
     * 扩展属性
     */
    @TableField("properties")
    private String properties;

    /**
     * 是否锁定
     */
    @TableField("is_locked")
    private Boolean locked;

    /**
     * 锁定持有者
     */
    @TableField("lock_holder")
    private String lockHolder;

    /**
     * 被占用车辆
     */
    @TableField("occupied_by")
    private String occupiedBy;

    /**
     * 占用时间
     */
    @TableField("occupied_time")
    private LocalDateTime occupiedTime;

    /**
     * 创建人
     */
    @TableField("created_by")
    private String createdBy;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    @TableField("updated_by")
    private String updatedBy;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private LocalDateTime updatedTime;

    /**
     * 版本号
     */
    @TableField("version")
    private Long version;
}