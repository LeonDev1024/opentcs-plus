package org.opentcs.common.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PathLayoutControlPointTO implements Serializable {

    /**
     * 控制点 X 坐标（mm）
     */
    private BigDecimal x;

    /**
     * 控制点 Y 坐标（mm）
     */
    private BigDecimal y;
}
