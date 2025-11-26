package org.opentcs.map.domain.bo;

import lombok.Data;
import org.opentcs.map.domain.entity.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 地图模型BO
 */
@Data
public class PlantModelBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 地图模型信息
     */
    private Long plantModelId;

    /**
     * 语义拓扑地图id
     */
    private String mapId;

    /**
     * 地图模型名称，唯一标识
     */
    private String name;

    /**
     * 模型版本
     */
    private String modelVersion;


    /**
     * 导航点集合
     */
    private Set<Point> points;

    /**
     * 导航路径集合
     */
    private Set<Path> paths;

    /**
     * 业务点位类型集合
     */
    private Set<LocationType> locationTypes;

    /**
     * 业务点位集合
     */
    private Set<Location> locations;

    /**
     * 地图模型规则区域集合
     */
    private Set<Block> blocks;

    /**
     * 地图模型可视化
     */
    private VisualLayoutBO visualLayout;

}