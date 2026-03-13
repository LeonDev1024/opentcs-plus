package org.opentcs.map.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoadModelVO implements Serializable {
    /**
     * 地图模型主键ID
     */
    private Long modelId;
}
