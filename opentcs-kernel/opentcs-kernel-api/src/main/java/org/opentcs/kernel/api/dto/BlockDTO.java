package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 地图 Block（资源互斥分组）DTO。
 */
@Data
public class BlockDTO {

    private Long id;

    private Long navigationMapId;

    private Long factoryModelId;

    private String blockId;

    private String name;

    /**
     * SINGLE_VEHICLE_ONLY / SAME_DIRECTION_ONLY
     */
    private String type;

    private List<String> members;

    private String color;

    private Map<String, String> properties;

    private Date createTime;

    private Date updateTime;
}
