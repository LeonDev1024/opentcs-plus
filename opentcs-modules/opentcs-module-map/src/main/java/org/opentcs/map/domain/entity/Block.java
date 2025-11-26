package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BaseEntity;

import java.time.LocalDateTime;

/**
 * 区块实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("block")
public class Block extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属地图模型ID
     */
    private Long plantModelId;

    /**
     * 区块名称
     */
    private String name;

    /**
     * 区块类型：SINGLE, GROUP
     */
    private String type;

    /**
     * 区块成员（点位、路径、位置等元素的名称集合）
     */
    private String members;

    /**
     * 区块显示颜色
     */
    private String color;

    /**
     * 扩展属性
     */
    private String properties;

}