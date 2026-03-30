package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.DataEntity;
import org.opentcs.common.core.dto.PathLayoutControlPointTO;

import java.math.BigDecimal;
import java.util.List;

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
     * 当前用于持久化前端传入的 {@link #layoutControlPoints}，
     * 并按 openTCS 的 {@code PathCreationTO.Layout} 语义封装为：
     * { "connectionType": "...", "controlPoints": [ { "x": ..., "y": ... }, ... ] }
     *
     * <p>layerId 在当前表结构中尚未落库，因此暂不持久化。</p>
     */
    @TableField("layout")
    private String layout;

    /**
     * 几何连接类型（仅导入 openTCS XML 时使用，不入库）：
     * DIRECT / ELBOW / SLANTED / POLYPATH / BEZIER / BEZIER_3
     */
    @TableField(exist = false)
    private String connectionType;

    /**
     * 路径控制点列表（仅导入 openTCS XML 时使用，不入库），
     * 用于前端重建近似的路径几何形状。
     */
    @TableField(exist = false)
    private List<PathLayoutControlPointTO> layoutControlPoints;
}
