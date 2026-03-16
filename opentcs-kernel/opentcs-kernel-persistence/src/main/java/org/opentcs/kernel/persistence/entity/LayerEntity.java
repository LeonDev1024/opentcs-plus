package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.ConfigEntity;

/**
 * 图层数据模型
 * 配置表，简化审计字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("layer")
public class LayerEntity extends ConfigEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属可视化布局
     */
    private Long visualLayoutId;

    /**
     * 所属导航地图ID（支持多楼层工厂）
     */
    private Long navigationMapId;

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
}
