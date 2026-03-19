package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.DataEntity;

/**
 * 区块数据模型
 * 用于区域管理，如工作区域、禁行区域、等待区域、充电区域
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("block")
public class BlockEntity extends DataEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属工厂ID
     */
    private Long factoryModelId;

    /**
     * 所属导航地图ID（可选，单楼层区域可指定）
     */
    private Long navigationMapId;

    /**
     * 区块唯一标识
     */
    private String blockId;

    /**
     * 区块名称
     */
    private String name;

    /**
     * 区块类型：SINGLE, GROUP
     * 扩展支持：WORK(工作区域), FORBIDDEN(禁行区域), WAIT(等待区域), CHARGE(充电区域)
     */
    private String type;

    /**
     * 区块成员（点位ID列表，JSON格式）
     */
    private String members;

    /**
     * 区块显示颜色
     */
    private String color;

    /**
     * 扩展属性
     */
    private String properties;
}
