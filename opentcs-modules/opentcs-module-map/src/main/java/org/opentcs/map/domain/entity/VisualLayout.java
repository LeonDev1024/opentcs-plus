package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 视觉布局实体类
 */
@Data
@TableName("visual_layout")
public class VisualLayout {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 视觉布局名称
     */
    private String name;

    /**
     * 关联的地图模型ID
     */
    private Long plantModelId;

    /**
     * X轴缩放比例
     */
    private BigDecimal scaleX;

    /**
     * Y轴缩放比例
     */
    private BigDecimal scaleY;

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