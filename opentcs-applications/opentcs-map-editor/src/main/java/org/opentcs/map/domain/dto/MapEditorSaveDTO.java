package org.opentcs.map.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 地图编辑器保存请求 DTO（前端提交保存数据）
 */
@Data
public class MapEditorSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 地图标识
     */
    private String mapId;

    /**
     * 地图名称
     */
    private String name;

    /**
     * 地图版本号
     */
    private String mapVersion;

    /**
     * 地图原点X坐标
     */
    private BigDecimal originX;

    /**
     * 地图原点Y坐标
     */
    private BigDecimal originY;

    /**
     * 地图旋转角度
     */
    private BigDecimal rotation;

    /**
     * 点位列表
     */
    private List<PointDTO> points;

    /**
     * 路径列表
     */
    private List<PathDTO> paths;

    /**
     * 位置列表
     */
    private List<LocationDTO> locations;

    /**
     * 地图画布数据（JSON 格式，用于生成快照）
     */
    private String data;
}