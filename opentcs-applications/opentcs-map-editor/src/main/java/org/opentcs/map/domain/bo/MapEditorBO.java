package org.opentcs.map.domain.bo;

import lombok.Data;
import org.opentcs.kernel.persistence.entity.*;

import java.util.Date;
import java.util.List;

/**
 * 地图编辑器业务对象
 */
@Data
public class MapEditorBO {

    private Long id;

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
