package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BaseEntity;

import java.time.LocalDateTime;

/**
 * 图层组实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("layer_group")
public class LayerGroup extends BaseEntity {

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
     * 图层组名称
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