package org.opentcs.map.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoadModelVO implements Serializable {
    /**
     * 导航地图ID
     */
    private Long navMapId;
}
