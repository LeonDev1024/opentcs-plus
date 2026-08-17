package org.opentcs.map.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 地图编辑器图层 DTO。
 */
@Data
public class MapEditorLayerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 图层ID（前端传入可能是临时字符串ID）。
     */
    private String id;

    /**
     * 所属图层组ID（字符串，保存时会映射为真实数据库ID）。
     */
    private String layerGroupId;

    /**
     * 图层名称。
     */
    private String name;

    /**
     * 是否可见。
     */
    private Boolean visible;

    /**
     * 显示顺序。
     */
    private Integer ordinal;

    /**
     * 扩展属性（JSON 字符串）。
     */
    private String properties;
}

