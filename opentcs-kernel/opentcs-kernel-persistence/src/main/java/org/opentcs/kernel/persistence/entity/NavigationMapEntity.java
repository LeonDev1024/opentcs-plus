package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BusinessEntity;

import java.math.BigDecimal;

/**
 * 导航地图实体
 * 支持多楼层工厂模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("navigation_map")
public class NavigationMapEntity extends BusinessEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    @TableField("vehicle_type_id")
    private Long vehicleTypeId;

    /**
     * 地图原点X坐标（毫米，相对于场景原点，用于多地图统一显示）
     */
    private BigDecimal originX;

    /**
     * 地图原点Y坐标（毫米，相对于场景原点，用于多地图统一显示）
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
     * 地图版本号
     * 格式：1.0, 1.1, 1.2, ... 1.20 -> 2.0
     */
    private String mapVersion;

    /**
     * 状态: 0-草稿(DRAFT), 1-已发布(PUBLISHED)
     */
    private String status;

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
