package org.opentcs.map.domain.bo;

import lombok.Data;
import org.opentcs.map.domain.entity.Layer;
import org.opentcs.map.domain.entity.LayerGroup;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VisualLayoutBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 视觉布局id
     */
    private Long visualLayoutId;
    /**
     * 视觉布局名称
     */
    private String name;

    /**
     * X轴缩放比例
     */
    private BigDecimal scaleX;

    /**
     * Y轴缩放比例
     */
    private BigDecimal scaleY;

    private List<Layer> layers;

    private List<LayerGroup> layerGroups;
}
