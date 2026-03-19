package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.ConfigEntity;

import java.math.BigDecimal;

/**
 * 视觉布局数据模型
 * 配置表，简化审计字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("visual_layout")
public class VisualLayoutEntity extends ConfigEntity {

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
     * 关联的导航地图ID
     */
    private Long navigationMapId;

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
