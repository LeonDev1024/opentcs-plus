package org.opentcs.map.domain.bo;

import lombok.Data;
import org.opentcs.kernel.persistence.entity.BlockEntity;
import org.opentcs.kernel.persistence.entity.LocationEntity;
import org.opentcs.kernel.persistence.entity.LocationTypeEntity;
import org.opentcs.kernel.persistence.entity.PathEntity;
import org.opentcs.kernel.persistence.entity.PointEntity;

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
     * 地图模型主键ID
     */
    private Long plantModelId;

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
    private Set<PointEntity> points;

    /**
     * 导航路径集合
     */
    private Set<PathEntity> paths;

    /**
     * 业务点位类型集合
     */
    private Set<LocationTypeEntity> locationTypes;

    /**
     * 业务点位集合
     */
    private Set<LocationEntity> locations;

    /**
     * 地图模型规则区域集合
     */
    private Set<BlockEntity> blocks;

    /**
     * 地图模型可视化
     */
    private VisualLayoutBO visualLayout;

}