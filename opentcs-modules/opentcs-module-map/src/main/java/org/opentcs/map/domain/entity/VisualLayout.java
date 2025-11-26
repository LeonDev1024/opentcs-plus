package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 视觉布局实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("visual_layout")
public class VisualLayout extends BaseEntity {

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
     * 扩展属性
     */
    private String properties;

}