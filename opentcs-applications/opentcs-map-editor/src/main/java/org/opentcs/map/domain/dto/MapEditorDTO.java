package org.opentcs.map.domain.dto;

import lombok.Data;
import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.kernel.api.dto.PointDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 地图编辑器响应 DTO（用于加载地图返回给前端）
 */
@Data
public class MapEditorDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 导航地图元信息（名称、原点、版本、快照等）
     */
    private MapEditorMapInfoDTO mapInfo;

    /**
     * 点位列表
     */
    private List<PointDTO> points;

    /**
     * 路径列表
     */
    private List<org.opentcs.kernel.api.dto.PathDTO> paths;

    /**
     * 位置列表
     */
    private List<org.opentcs.kernel.api.dto.LocationDTO> locations;

    /**
     * 图层组列表（编辑语义）。
     */
    private List<MapEditorLayerGroupDTO> layerGroups;

    /**
     * 图层列表（编辑语义）。
     */
    private List<MapEditorLayerDTO> layers;

    /**
     * Block 列表（资源互斥规则）。
     * 对应 openTCS Block 语义：SINGLE_VEHICLE_ONLY / SAME_DIRECTION_ONLY
     */
    private List<BlockDTO> blocks;
}
