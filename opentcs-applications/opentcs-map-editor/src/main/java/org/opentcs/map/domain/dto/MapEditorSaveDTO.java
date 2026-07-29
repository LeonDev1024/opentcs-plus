package org.opentcs.map.domain.dto;

import lombok.Data;
import org.opentcs.kernel.api.dto.BlockDTO;
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

    private MapEditorMapInfoDTO mapInfo;

    private List<PointDTO> points;

    private List<org.opentcs.kernel.api.dto.PathDTO> paths;

    private List<BlockDTO> blocks;

    private List<MapEditorLayerGroupDTO> layerGroups;

    private List<MapEditorLayerDTO> layers;
}
