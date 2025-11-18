package org.opentcs.map.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 位置类型实体类
 */
@Data
@TableName("location_type")
public class LocationType {

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
     * 位置类型名称
     */
    private String name;

    /**
     * 允许的操作列表：LOAD, UNLOAD, NOP等
     */
    private String allowedOperations;

    /**
     * 允许的外围设备操作
     */
    private String allowedPeripheralOperations;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}