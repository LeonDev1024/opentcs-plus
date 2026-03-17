package org.opentcs.kernel.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 导航地图 DTO
 * 包含关联的工厂信息
 */
@Data
public class NavigationMapDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 所属工厂模型ID
     */
    private Long factoryModelId;

    /**
     * 地图唯一标识
     */
    private String mapId;

    /**
     * 地图名称（如：一楼车间、室外道路）
     */
    private String name;

    /**
     * 楼层号（负数表示地下，null表示室外/公共区域）
     */
    private Integer floorNumber;

    /**
     * 地图类型：INDOOR/OUTDOOR/MIXED
     */
    private String mapType;

    /**
     * PGM地图原点的全局X坐标
     */
    private BigDecimal originX;

    /**
     * PGM地图原点的全局Y坐标
     */
    private BigDecimal originY;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 关联的工厂名称
     */
    private String factoryName;

    /**
     * 关联的工厂编号
     */
    private String factoryId;
}
