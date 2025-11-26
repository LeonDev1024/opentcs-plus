package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BaseEntity;

/**
 * 地图模型实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("plant_model")
public class PlantModel extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 语义拓扑地图id
     */
    private String mapId;

    /**
     * 地图模型名称，唯一标识
     */
    private String name;

    /**
     * 模型版本
     */
    private String modelVersion;

    /**
     * 地图状态：0 未启用 1 停用
     */
    private String status;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 增加描述字段，用于描述地图模型
     */
    private String description;

    @TableLogic
    private String delFlag;
}