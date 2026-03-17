package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.util.Date;

/**
 * 位置类型数据传输对象
 */
@Data
public class LocationTypeDTO {

    private Long id;

    private String name;

    private String allowedOperations;

    private String allowedPeripheralOperations;

    private String properties;

    private Date createTime;

    private Date updateTime;
}
