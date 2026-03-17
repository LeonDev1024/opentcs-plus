package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.ConfigEntity;
import org.opentcs.common.mybatis.handler.MySqlJsonTypeHandler;
import org.opentcs.common.core.dto.AllowedOperationTO;
import org.opentcs.common.core.dto.AllowedPeripheralOperationTO;
import org.opentcs.common.core.dto.PropertyTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 位置类型数据模型
 * 配置表，简化审计字段
 * 注意：位置类型全局共享，不按工厂隔离（适用于私有化部署场景）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("location_type")
public class LocationTypeEntity extends ConfigEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 位置类型名称
     */
    private String name;

    /**
     * 允许的操作列表：LOAD, UNLOAD, NOP等
     */
    @TableField(typeHandler = MySqlJsonTypeHandler.class)
    private List<AllowedOperationTO> allowedOperations = new ArrayList<>();

    /**
     * 允许的外围设备操作
     */
    @TableField(typeHandler = MySqlJsonTypeHandler.class)
    private List<AllowedPeripheralOperationTO> allowedPeripheralOperations = new ArrayList<>();

    /**
     * 扩展属性
     */
    @TableField(typeHandler = MySqlJsonTypeHandler.class)
    private List<PropertyTO> properties = new ArrayList<>();
}
