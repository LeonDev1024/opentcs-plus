package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.util.Date;

/**
 * 区块数据传输对象
 */
@Data
public class BlockDTO {

    private Long id;

    private Long plantModelId;

    private Long factoryModelId;

    private Long navigationMapId;

    private String blockId;

    private String name;

    private String type;

    private String members;

    private String color;

    private String properties;

    private Date createTime;

    private Date updateTime;
}
