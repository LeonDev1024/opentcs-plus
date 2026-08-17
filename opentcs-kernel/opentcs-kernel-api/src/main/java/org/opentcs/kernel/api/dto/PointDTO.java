package org.opentcs.kernel.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 点位数据传输对象
 */
@Data
public class PointDTO {

    private Long id;

    private Long navigationMapId;

    private Long layerId;

    private String pointId;

    private String name;

    @JsonProperty("xPosition")
    @JsonAlias({"x", "x_position"})
    private BigDecimal xPosition;

    @JsonProperty("yPosition")
    @JsonAlias({"y", "y_position"})
    private BigDecimal yPosition;

    @JsonProperty("zPosition")
    @JsonAlias({"z", "z_position"})
    private BigDecimal zPosition;

    private BigDecimal vehicleOrientation;

    private String type;

    private BigDecimal radius;

    private Boolean locked;

    private Boolean isBlocked;

    private Boolean isOccupied;

    private String label;

    private String properties;

    /**
     * 点位布局数据（JSON）。极简场景可只含 x/y/z。
     */
    private String layout;

    private Date createTime;

    private Date updateTime;
}
