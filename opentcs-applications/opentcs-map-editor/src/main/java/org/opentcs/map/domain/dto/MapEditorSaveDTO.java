package org.opentcs.map.domain.dto;

import lombok.Data;
import org.opentcs.kernel.api.dto.PointDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 地图编辑器保存请求 DTO（前端提交保存数据）
 */
@Data
public class MapEditorSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 待保存的导航地图元信息（至少包含 mapId；可选 name、版本、原点、画布 JSON 等）
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
}
