package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.DataEntity;

import java.math.BigDecimal;

/**
 * 路径数据模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("path")
public class PathEntity extends DataEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 归属导航地图ID
     */
    private Long navigationMapId;

    /**
     * 路径唯一标识
     */
    private String pathId;

    /**
     * 归属图层ID
     */
    private Long layerId;

    /**
     * 路径名称
     */
    private String name;

    /**
     * 起始点位标识
     */
    private String sourcePointId;

    /**
     * 目标点位标识
     */
    private String destPointId;

    /**
     * 路径长度
     */
    private BigDecimal length;

    /**
     * 最大允许速度
     */
    private BigDecimal maxVelocity;

    /**
     * 最大反向速度
     */
    private BigDecimal maxReverseVelocity;

    /**
     * 是否被锁定
     */
    private Boolean locked;

    /**
     * 是否被阻塞
     */
    private Boolean isBlocked;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 路径布局（JSON）。
     *
     * 用于持久化前端传入的路径几何数据，
     * 按 openTCS 的 {@code PathCreationTO.Layout} 语义封装为：
     * { "connectionType": "...", "controlPoints": [ { "x": ..., "y": ... }, ... ] }
     *
     */
    @TableField("layout")
    private String layout;
}
