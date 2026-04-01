package org.opentcs.map.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 地图编辑器图层组 DTO。
 */
@Data
public class MapEditorLayerGroupDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 图层组ID（前端传入可能是临时字符串ID）。
     */
    private String id;

    /**
     * 图层组名称。
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

