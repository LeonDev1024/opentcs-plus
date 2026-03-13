package org.opentcs.map.domain.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 路径布局中的控制点（仅用于 openTCS XML 导入，不映射数据库表）。
 */
@Data
public class PathLayoutControlPoint {

    /**
     * 控制点 X 坐标（mm）
     */
    private BigDecimal x;

    /**
     * 控制点 Y 坐标（mm）
     */
    private BigDecimal y;
}

