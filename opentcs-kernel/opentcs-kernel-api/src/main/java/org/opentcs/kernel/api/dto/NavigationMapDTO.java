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
     * 车辆类型ID（必填，对应 vehicle_type.id）
     */
    private Long vehicleTypeId;

    /**
     * 地图原点X坐标（毫米，相对于场景原点）
     */
    private BigDecimal originX;

    /**
     * 地图原点Y坐标（毫米，相对于场景原点）
     */
    private BigDecimal originY;

    /**
     * 地图旋转角度（度，相对于场景方向）
     */
    private BigDecimal rotation;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 状态
     */
    private String status;

    /**
     * 地图版本号（如：1.0, 1.1, 2.0）
     */
    private String mapVersion;

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

    // ==================== 栅格底图相关字段 ====================

    /**
     * 栅格地图OSS存储路径
     */
    private String rasterUrl;

    /**
     * 栅格地图版本号
     */
    private Integer rasterVersion;

    /**
     * 栅格地图宽度（像素）
     */
    private Integer rasterWidth;

    /**
     * 栅格地图高度（像素）
     */
    private Integer rasterHeight;

    /**
     * 栅格地图分辨率（米/像素）
     */
    private BigDecimal rasterResolution;
}
