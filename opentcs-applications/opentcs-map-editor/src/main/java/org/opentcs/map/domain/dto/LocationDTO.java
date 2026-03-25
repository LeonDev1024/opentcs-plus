package org.opentcs.map.domain.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 位置 DTO
 */
@Data
public class LocationDTO implements Serializable {

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
     * 位置类型ID
     */
    private Long locationTypeId;

    /**
     * 位置唯一标识
     */
    private String locationId;

    /**
     * 位置名称
     */
    private String name;

    /**
     * X坐标
     */
    @JsonAlias({"xPosition", "x_position"})
    private BigDecimal xPosition;

    /**
     * Y坐标
     */
    @JsonAlias({"yPosition", "y_position"})
    private BigDecimal yPosition;

    /**
     * Z坐标
     */
    @JsonAlias({"zPosition", "z_position"})
    private BigDecimal zPosition;

    /**
     * 是否被锁定
     */
    private Boolean locked;

    /**
     * 是否被占用
     */
    private Boolean isOccupied;

    /**
     * 扩展属性
     */
    private String properties;
}