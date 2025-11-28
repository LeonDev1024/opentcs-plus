package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.BaseEntity;
import org.opentcs.map.domain.to.AllowedOperationTO;
import org.opentcs.map.domain.to.AllowedPeripheralOperationTO;
import org.opentcs.map.domain.to.PropertyTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 位置类型实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("location_type")
public class LocationType extends BaseEntity {

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
    private List<AllowedOperationTO> allowedOperations = new ArrayList<>();
    /**
     * 允许的外围设备操作
     */
    private List<AllowedPeripheralOperationTO> allowedPeripheralOperations = new ArrayList<>();

    /**
     * 扩展属性
     */
    private List<PropertyTO> properties = new ArrayList<>();


}