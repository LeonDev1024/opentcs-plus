package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BusinessEntity;

import java.math.BigDecimal;

/**
 * 工厂模型实体
 * 支持多楼层/多区域工厂管理
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("factory_model")
public class FactoryModelEntity extends BusinessEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工厂唯一标识符
     */
    private String factoryId;

    /**
     * 工厂名称
     */
    private String name;

    /**
     * 比例尺 (mm/px)：毫米/像素，默认1毫米=1像素
     */
    private BigDecimal scale;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private String status;
}
