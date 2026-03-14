package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BusinessEntity;

/**
 * 地图模型数据模型
 * 业务主表，保留完整审计字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("plant_model")
public class PlantModelEntity extends BusinessEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 地图模型唯一标识符
     */
    private String mapId;

    /**
     * 地图模型名称
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
     * 描述
     */
    private String description;
}
