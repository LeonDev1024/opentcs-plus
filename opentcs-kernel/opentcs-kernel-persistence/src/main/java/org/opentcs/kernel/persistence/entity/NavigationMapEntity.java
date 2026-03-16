package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
     * 地图类型：INDOOR/OUTDOOR/MIXED
     */
    private String mapType;

    /**
     * PGM地图原点的全局X坐标（仅导入时使用）
     */
    private BigDecimal originX;

    /**
     * PGM地图原点的全局Y坐标（仅导入时使用）
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
}
