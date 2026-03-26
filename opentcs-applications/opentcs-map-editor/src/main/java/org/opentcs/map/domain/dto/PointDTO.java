package org.opentcs.map.domain.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 点位 DTO
 */
@Data
public class PointDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 归属导航地图ID
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
     * X坐标
     */
    @JsonProperty("xPosition")
    @JsonAlias({"x_position"})
    private BigDecimal xPosition;

    /**
     * Y坐标
     */
    @JsonProperty("yPosition")
    @JsonAlias({"y_position"})
    private BigDecimal yPosition;

    /**
     * Z坐标（楼层高度）
     */
    @JsonProperty("zPosition")
    @JsonAlias({"z_position"})
    private BigDecimal zPosition;

    /**
     * 车辆方向角度（弧度）
     */
    private BigDecimal vehicleOrientation;

    /**
     * 点位类型
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