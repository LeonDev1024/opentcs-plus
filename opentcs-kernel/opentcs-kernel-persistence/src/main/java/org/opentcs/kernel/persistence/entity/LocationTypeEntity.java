package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BaseEntity;
import org.opentcs.common.mybatis.handler.JsonbTypeHandler;
import org.opentcs.kernel.persistence.to.AllowedOperationTO;
import org.opentcs.kernel.persistence.to.AllowedPeripheralOperationTO;
import org.opentcs.kernel.persistence.to.PropertyTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 位置类型数据模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("location_type")
public class LocationTypeEntity extends BaseEntity {

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
    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<AllowedOperationTO> allowedOperations = new ArrayList<>();

    /**
     * 允许的外围设备操作
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<AllowedPeripheralOperationTO> allowedPeripheralOperations = new ArrayList<>();

    /**
     * 扩展属性
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<PropertyTO> properties = new ArrayList<>();
}
