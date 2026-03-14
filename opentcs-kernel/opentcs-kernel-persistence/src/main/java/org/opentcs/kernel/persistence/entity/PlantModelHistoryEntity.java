package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.HistoryEntity;

/**
 * 地图模型历史版本快照数据模型
 * 历史表，最简化审计字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("plant_model_history")
public class PlantModelHistoryEntity extends HistoryEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 当前地图模型ID
     */
    private Long plantModelId;

    /**
     * 业务版本号，例如1.0, 1.1
     */
    private String modelVersion;

    /**
     * 快照文件路径或URL(JSON/XML)
     */
    private String fileUrl;

    /**
     * 快照类型：EDITOR_JSON, OPENTCS_XML, MERGED等
     */
    private String snapshotType;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 本次修改简要说明
     */
    private String changeSummary;
}
