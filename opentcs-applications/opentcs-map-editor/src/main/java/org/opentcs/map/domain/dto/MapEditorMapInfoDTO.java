package org.opentcs.map.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 地图编辑器中与导航地图（navigation_map）对应的元信息。
 * 加载接口放在 {@link MapEditorDTO#mapInfo}；保存接口放在 {@link MapEditorSaveDTO#mapInfo}。
 */
@Data
public class MapEditorMapInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 地图主键ID（仅内部兼容字段，前端与接口调用请统一使用 mapId）
     */
    private Long id;

    /**
     * 地图名称
     */
    private String name;

    /**
     * 地图业务标识
     */
    private String mapId;

    /**
     * 所属工厂模型 ID（加载时返回）
     */
    private Long factoryModelId;

    /**
     * 工厂名称（加载时返回）
     */
    private String factoryName;

    /**
     * 地图原点 X（毫米）
     */
    private BigDecimal originX;

    /**
     * 地图原点 Y（毫米）
     */
    private BigDecimal originY;

    /**
     * 地图旋转角度（度）
     */
    private BigDecimal rotation;

    /**
     * 地图版本号
     */
    private String mapVersion;

    /**
     * 地图状态: 0-草稿, 1-已发布（加载时返回）
     */
    private String status;

    /**
     * 画布/快照 JSON（加载自快照文件；保存时写入快照）
     */
    private String data;

    /**
     * 创建时间（加载时返回）
     */
    private Date createTime;

    /**
     * 更新时间（加载时返回）
     */
    private Date updateTime;
}
