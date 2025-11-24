package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 图层实体类
 */
@Data
@TableName("layer")
public class Layer {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属地图模型ID
     */
    private String plantModelId;

    /**
     * 图层组ID
     */
    private Long layerGroupId;

    /**
     * 图层名称
     */
    private String name;

    /**
     * 是否可见
     */
    private Boolean visible;

    /**
     * 显示顺序
     */
    private Integer ordinal;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}