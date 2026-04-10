package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.util.Date;

/**
 * 车辆品牌 DTO（Brand → VehicleType → Vehicle 三级层级的顶层）
 */
@Data
public class VehicleBrandDTO {

    /** 数据库主键 */
    private Long id;

    /** 领域 brandId（与 kernel-domain 中 VehicleBrand.brandId 对应） */
    private String brandId;

    /** 品牌名称 */
    private String name;

    /** 品牌缩写代码 */
    private String code;

    /** Logo URL */
    private String logo;

    /** 官网地址 */
    private String website;

    /** 品牌描述 */
    private String description;

    /** 联系方式 */
    private String contact;

    /** 是否启用 */
    private Boolean enabled;

    /** 排序 */
    private Integer sort;

    private Date createTime;

    private Date updateTime;
}
