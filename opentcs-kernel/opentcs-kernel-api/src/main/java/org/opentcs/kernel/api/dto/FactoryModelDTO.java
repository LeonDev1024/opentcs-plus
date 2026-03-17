package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工厂模型 DTO
 */
@Data
public class FactoryModelDTO {

    private Long id;

    private String factoryId;

    private String name;

    private BigDecimal scale;

    private String properties;

    private String description;

    private String status;

    private Date createTime;

    private Date updateTime;
}
