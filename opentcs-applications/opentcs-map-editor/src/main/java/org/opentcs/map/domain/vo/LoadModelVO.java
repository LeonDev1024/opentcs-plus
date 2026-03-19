package org.opentcs.map.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoadModelVO implements Serializable {
    /**
     * 地图编号（业务标识，如 map_001）
     */
    private String mapId;
}
