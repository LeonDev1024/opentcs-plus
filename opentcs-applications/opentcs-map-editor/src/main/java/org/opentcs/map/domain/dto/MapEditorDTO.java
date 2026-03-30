package org.opentcs.map.domain.dto;

import lombok.Data;
import org.opentcs.kernel.api.dto.PointDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 地图编辑器响应 DTO（用于加载地图返回给前端）
 */
@Data
public class MapEditorDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 地图名称
     */
    private String name;

    /**
     * 地图标识
     */
    private String mapId;

    /**
     * 工厂模型ID
     */
    private Long factoryModelId;

    /**
     * 工厂名称
     */
    private String factoryName;

    /**
     * 地图原点X坐标
     */
    private BigDecimal originX;

    /**
     * 地图原点Y坐标
     */
    private BigDecimal originY;

    /**
     * 地图旋转角度
     */
    private BigDecimal rotation;

    /**
     * 点位列表
     */
    private List<PointDTO> points;

    /**
     * 路径列表
     */
    private List<org.opentcs.kernel.api.dto.PathDTO> paths;

    /**
     * 位置列表
     */
    private List<org.opentcs.kernel.api.dto.LocationDTO> locations;

    /**
     * 地图版本号
     */
    private String mapVersion;

    /**
     * 地图状态: 0-草稿, 1-已发布
     */
    private String status;

    /**
     * JSON 快照数据
     */
    private String data;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}