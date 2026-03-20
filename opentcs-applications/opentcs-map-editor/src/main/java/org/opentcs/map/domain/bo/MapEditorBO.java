package org.opentcs.map.domain.bo;

import lombok.Data;
import org.opentcs.kernel.persistence.entity.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 地图编辑器业务对象
 */
@Data
public class MapEditorBO {


    /**
     * 地图名称
     */
    private String name;

    /**
     * 地图标识
     */
    private String mapId;

    /**
     * 工厂模型ID
     */
    private Long factoryModelId;

    /**
     * 工厂名称
     */
    private String factoryName;

    /**
     * 地图原点X坐标（毫米，相对于场景原点）
     */
    private BigDecimal originX;

    /**
     * 地图原点Y坐标（毫米，相对于场景原点）
     */
    private BigDecimal originY;

    /**
     * 地图旋转角度（度）
     */
    private BigDecimal rotation;

    /**
     * 点位列表
     */
    private List<PointEntity> points;

    /**
     * 路径列表
     */
    private List<PathEntity> paths;

    /**
     * 位置列表
     */
    private List<LocationEntity> locations;

    /**
     * 图层组列表
     */
    private List<LayerGroupEntity> layerGroups;

    /**
     * 图层列表
     */
    private List<LayerEntity> layers;

    private String data;

    private Date createTime;

    private Date updateTime;
}
