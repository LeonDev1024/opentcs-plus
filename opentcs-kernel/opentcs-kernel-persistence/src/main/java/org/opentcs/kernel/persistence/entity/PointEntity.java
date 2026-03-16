package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.DataEntity;

import java.math.BigDecimal;

/**
 * 点位数据模型
 * 前端显示和后端计算共用同一套坐标
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("point")
public class PointEntity extends DataEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属地图模型ID（兼容旧版）
     */
    private Long plantModelId;

    /**
     * 归属导航地图ID（新版）
     */
    private Long navigationMapId;

    /**
     * 归属图层ID
     */
    private Long layerId;

    /**
     * 点位唯一标识
     */
    private String pointId;

    /**
     * 点位名称
     */
    private String name;

    /**
     * X坐标（前端显示和后端计算共用）
     */
    private BigDecimal xPosition;

    /**
     * Y坐标（前端显示和后端计算共用）
     */
    private BigDecimal yPosition;

    /**
     * Z坐标（楼层高度）
     */
    private BigDecimal zPosition;

    /**
     * 车辆方向角度（弧度）
     */
    private BigDecimal vehicleOrientation;

    /**
     * 点位类型：HALT_POSITION, PARK_POSITION, REPORT_POSITION, CHARGE_POSITION, ELEVATOR_WAIT
     */
    private String type;

    /**
     * 点位半径
     */
    private BigDecimal radius;

    /**
     * 是否被锁定
     */
    private Boolean locked;

    /**
     * 是否被阻塞
     */
    private Boolean isBlocked;

    /**
     * 是否被占用
     */
    private Boolean isOccupied;

    /**
     * 标签
     */
    private String label;

    /**
     * 扩展属性
     */
    private String properties;
}
