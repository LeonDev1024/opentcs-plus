package org.opentcs.vehicle.application.bo;

import lombok.Data;

/**
 * 品牌业务对象，用于应用层与接口层之间的数据传递。
 * 仅包含业务字段，屏蔽 BrandEntity 的持久化注解和审计字段。
 */
@Data
public class BrandBO {

    /** 主键ID（查询/更新时使用，创建时为 null） */
    private Long id;

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
}
