package org.opentcs.kernel.persistence.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 路径布局中的控制点（仅用于 openTCS XML 导入，不映射数据库表）。
 */
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
